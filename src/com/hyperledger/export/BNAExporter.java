package com.hyperledger.export;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.impl.AggregationRelationship;
import com.archimatetool.model.impl.BusinessActor;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;
import com.archimatetool.model.impl.CompositionRelationship;
import com.archimatetool.model.impl.SpecializationRelationship;
import com.hyperledger.export.exceptions.CycleInheritanceException;
import com.hyperledger.export.exceptions.DuplicateObjectNames;
import com.hyperledger.export.exceptions.InvalidInheritanceException;
import com.hyperledger.export.exceptions.UnexpectedIdException;
import com.hyperledger.export.exceptions.UnknownTypeException;
import com.hyperledger.export.models.Asset;
import com.hyperledger.export.models.HLField;
import com.hyperledger.export.models.HLModel;
import com.hyperledger.export.models.HLModelFabric;
import com.hyperledger.export.models.HLModelInstance;
import com.hyperledger.export.models.HLNamed;
import com.hyperledger.export.models.Participant;
import com.hyperledger.export.models.Transaction;
import com.hyperledger.export.rules.HLPermRule;
import com.hyperledger.export.utils.Data;
import com.hyperledger.export.utils.Writer;

public class BNAExporter {

    private IArchimateModel model; 

	private Predicate<EObject> isModel = x -> (x instanceof BusinessRole) ||
											  (x instanceof BusinessObject) ||
											  (x instanceof BusinessProcess);
	private Predicate<EObject> isInstance = x -> x instanceof BusinessActor;	
	
	private HashMap<IArchimateConcept, HLModel> conceptToModel;
	private HashMap<IArchimateConcept, HLModelInstance> conceptToInstance;
	
	private Function<IArchimateConcept, Optional<HLNamed>> conceptToHLObject;
	
    public BNAExporter() {
    	conceptToModel = new HashMap<>();
    	conceptToInstance = new HashMap<>();
    	conceptToHLObject = concept -> {
			if (conceptToModel.containsKey(concept)) {
				return Optional.of(conceptToModel.get(concept));
			}
			if (conceptToInstance.containsKey(concept)) {
				return Optional.of(conceptToInstance.get(concept));
			}	
    		return Optional.empty();
    	};
    }
    
    public BNAExporter(IArchimateModel model) {
    	this();
    	this.model = model;
    }
        
    public void export(Data data) throws IOException, ParseException {
    	IFolder businessFolder = model.getFolder(FolderType.BUSINESS);
 	    IFolder relationsFolder = model.getFolder(FolderType.RELATIONS);
 	    
    	List<EObject> list = new ArrayList<EObject>();
    	getElements(businessFolder, list);
 	    
 	    List<HLModel> models = generateModels(list, data);
 	    List<HLModelInstance> instances = collectInstances(list);
 	    List<HLPermRule> rules = permissionRulesProcessing(models, instances);
 		
 		checkModels(models);
    	writeBNA(data, models, rules);
    }
    
    private void writeBNA(Data data, List<HLModel> models, List<HLPermRule> rules) throws IOException, ParseException {
    	Writer writer = new Writer(model, data);
	    writer.start();
    	
	    if (models != null)
	    	writer.writeModels(models);
	    writer.writeScripts(models.stream().filter(x -> x instanceof Transaction).map(x -> (Transaction) x).collect(Collectors.toList()));
	    if (rules != null)
	    	writer.writePermissions(rules);
	    writer.writePackageJSON();
		writer.writeReadme();

		writer.close();
    }
        
    private void checkModels(Collection<HLModel> models) { 	
    	Set<String> names = new HashSet<>();
    	for (HLModel model : models) {
    		if (names.contains(model.getName())) {
    			throw new DuplicateObjectNames(model.getName());
    		} else {
    			names.add(model.getName());
    		}
    	}
    	
    	for (HLModel model : models) {
    		for (HLField field : model.getFields()) {
    			if (!field.isPrimitive() && 
    					!names.contains(field.getType()) && 
    					!(field.getType().endsWith("[]") && names.contains(field.getType().substring(0, field.getType().length() - 2)))) {
    				throw new UnknownTypeException(model, field);
    			}
    		}
    	}
    }
            
    private List<HLPermRule> permissionRulesProcessing(List<HLModel> models, List<HLModelInstance> instances) {
    	Stream<IArchimateRelationship> streamModels = models
    						.stream()
					    	.flatMap(model -> model.getConcepts()
			    				.stream()
								.flatMap(concept ->	concept.getSourceRelationships()
									.stream()
									.filter(relation -> HLPermRule.isRule(relation, conceptToHLObject))))
					    	.map(x -> (IArchimateRelationship) x);
    	
    	Stream<IArchimateRelationship> streamInstances = instances
							.stream()
					    	.flatMap(instance -> instance.getConcept().getSourceRelationships()
									.stream()
									.filter(relation -> HLPermRule.isRule(relation, conceptToHLObject)))
					    	.map(x -> (IArchimateRelationship) x);
    
    	List<HLPermRule> rules = Stream.concat(streamModels, streamInstances)
    		.map(access -> HLPermRule.createRule(access, conceptToHLObject.apply(access.getSource()), conceptToHLObject.apply(access.getTarget())))
    		.filter(x -> x.isPresent())
    		.map(x -> x.get())
    		.collect(Collectors.toList());
    	
    	rules.add(HLPermRule.getNetworkAdminUserRule());
    	rules.add(HLPermRule.getNetworkAdminSystemRule());
    	rules.add(HLPermRule.getSystemAclRule());
    	return rules;
    }
    
    private List<HLModel> generateModels(List<EObject> allObjects, Data data) throws ParseException {
    	List<HLModel> models = collectModels(allObjects, data);
    	models = relationsProcessing(models);
    	models = participantsProcessing(models);
    	return models;
    }
    
    private List<HLModelInstance> collectInstances(List<EObject> allObjects) {	
    	List<HLModelInstance> instances = new ArrayList<>();
    	for (EObject object : allObjects) {
			if (isInstance.test(object)) {
				
	    		Optional<HLModelInstance> instance = HLModelInstance.createModelInstance(conceptToModel, (IArchimateConcept) object);
	    		if (instance.isPresent()) {
	    			instances.add(instance.get());
		    		conceptToInstance.put((IArchimateConcept) object, instance.get());
	    		}
			}
    	}
    	return instances;
    }
    
    private List<HLModel> collectModels(List<EObject> allObjects, Data data) throws ParseException {
    	final String namespace = data.getStringValue(Data.NAMESPACE);
    	    	
    	List<HLModel> models = new ArrayList<>();
    	for (EObject object : allObjects) {
			if (isModel.test(object)) {
	    		Optional<HLModel> model = HLModelFabric.createModel((IArchimateConcept) object, namespace);
	    		if (model.isPresent()) {
		    		models.add(model.get());
		    		conceptToModel.put((IArchimateConcept) object, model.get());
	    		}
			}
    	}    	
    	return models;
    }
     
    /**
     * На данный момент только композиция и агрегация у Asset'ов
     * @param models
     * @param concepts
     * @return список hl моделей с обработанными отношениями
     */
    private List<HLModel> relationsProcessing(List<HLModel> models) {
    	models
    		.stream()
			.forEach(model -> model.getConcepts().stream().forEach(concept -> concept.getSourceRelationships()
				.stream()
				.forEach(relation -> {
					IArchimateConcept target = relation.getTarget();
					HLField field = null;
					if (relation instanceof CompositionRelationship) {
						model.addField(HLField.createField(model, target.getName(), target.getName().toLowerCase(), HLField.Type.PROPERTY));
					} else if (relation instanceof AggregationRelationship) {
						model.addField(HLField.createField(model, target.getName(), target.getName().toLowerCase(), HLField.Type.REFER));
					} else if (relation instanceof SpecializationRelationship) {
						model.setSuperModel(conceptToModel.get(target));
					}
				}))
			);
	
    	//проверка на зацикленные наследования
    	isCorrectInheritance(models);
    	
    	return models;
    }   
    
    private void isCorrectInheritance(List<HLModel> models) {
    	Map<HLModel, Integer> was = models
							    		.stream()
							    		.collect(Collectors.toMap(model -> model, model -> 0));
    	if (models.size() == 0)
    		return;
    	
    	models
    		.stream()
    		.forEach(model -> {
    			if(was.get(model) == 0)
    				if (!inheritanceChecker(was, model))
    					throw new CycleInheritanceException(was.keySet().stream().filter(x -> was.get(x) == 1).collect(Collectors.toList()));
    		});
    }
    
    private boolean inheritanceChecker(Map<HLModel, Integer> was, HLModel current) {
    	if (current == null || was.get(current) == 2)
    		return true;
    	
		if (was.get(current) == 1) {
			return false;
		} else if (was.get(current) == 0) {
			was.put(current, 1);
			if (!inheritanceChecker(was, current.getSuperModel()))
				return false;
			was.put(current, 2);
		}
		return true;
    }
     
    private List<HLModel> participantsProcessing(List<HLModel> preProcessedModels) {
    	
    	List<List<HLModel>> composites = preProcessedModels.stream()
    		.filter(x -> (x instanceof Participant) || (x instanceof Asset)) // select Participants and Assets
    		.collect(Collectors.groupingBy(x -> x.getName())) // group them by names
    		.values().stream() 
    		.filter(list -> list.size() > 1) // select only composite Participants
    		.collect(Collectors.toList());
    	
    	List<HLModel> builtParticipants = composites.stream()
    													.map(composite -> buildParticipant(composite))
    													.filter(x -> x.isPresent())
    													.map(x -> x.get())
    													.collect(Collectors.toList());
    				
    	List<HLModel> remainder = preProcessedModels.stream()
    													.filter(x -> builtParticipants.stream().allMatch(participant -> !participant.getFullName().equals(x.getFullName())))
    													.collect(Collectors.toList());
    	
    	return Stream.concat(remainder.stream(), builtParticipants.stream()).collect(Collectors.toList());
    }
    
    private Optional<HLModel> buildParticipant(List<HLModel> models) {
    	List<Participant> candidates = models.stream()
								    		.filter(x -> (x instanceof Participant))
								    		.map(x -> (Participant) x)
								    		.collect(Collectors.toList());
    	if (candidates.size() > 1)
    		throw new DuplicateObjectNames(candidates.get(0).getName());
    	
    	if (candidates.size() == 0)
    		return Optional.empty();
    	
    	Participant participant = candidates.get(0);
    	
    	List<Asset> assets = models.stream()
									.filter(x -> (x instanceof Asset))
									.map(x -> (Asset) x)
									.collect(Collectors.toList());
    	
    	// проверка, что у ассетов нет id
    	List<Asset> unexpectedIds = assets.stream().filter(x -> x.hasId()).collect(Collectors.toList());
    	if (unexpectedIds.size() > 0)
    		throw new UnexpectedIdException(unexpectedIds.get(0).getName());
    	
    	// проверка, что ассеты ни от кого не наследуются и не наследуют
    	boolean hasInheritance = !assets
			.stream()
			.allMatch(asset -> asset.getConcepts()
				.stream()
				.allMatch(concept -> concept.getSourceRelationships()
										.stream()
										.allMatch(relation -> !(relation instanceof SpecializationRelationship))
									 &&
									 concept.getTargetRelationships()
									 	.stream()
									 	.allMatch(relation -> !(relation instanceof SpecializationRelationship))));
    	if (hasInheritance)
    		throw new InvalidInheritanceException(participant.getFullName());
    	
    	assets.stream().forEach(asset -> {
    		asset.getFields().stream().forEach(field -> participant.addField(field));
    		participant.addConcepts(asset.getConcepts());
    		asset.getConcepts().stream().forEach(concept -> conceptToModel.put(concept, participant));
    	});
    	return Optional.of(participant);
    }
        
    private void getElements(IFolder folder, List<EObject> list) {
        for(EObject object : folder.getElements()) {
            list.add(object);
        }
        
        for(IFolder f : folder.getFolders()) {
            getElements(f, list);
        }
    }
    
    public IArchimateModel getModel() {
    	return model;
    }

}
