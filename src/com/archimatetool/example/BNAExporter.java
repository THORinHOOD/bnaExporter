package com.archimatetool.example;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.example.exc.DuplicateObjectNames;
import com.archimatetool.example.exc.UnexpectedIdException;
import com.archimatetool.example.exc.UnknownTypeException;
import com.archimatetool.example.hl.models.Asset;
import com.archimatetool.example.hl.models.HLField;
import com.archimatetool.example.hl.models.HLModel;
import com.archimatetool.example.hl.models.HLModelFabric;
import com.archimatetool.example.hl.models.Participant;
import com.archimatetool.example.hl.models.Transaction;
import com.archimatetool.example.hl.pcl.HLPermRule;
import com.archimatetool.example.utils.Data;
import com.archimatetool.example.utils.Writer;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.impl.AccessRelationship;
import com.archimatetool.model.impl.AggregationRelationship;
import com.archimatetool.model.impl.ArchimateRelationship;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;
import com.archimatetool.model.impl.CompositionRelationship;

public class BNAExporter {

    private IArchimateModel model; 

	private Predicate<EObject> isModel = x -> (x instanceof BusinessRole) || (x instanceof BusinessObject) || (x instanceof BusinessProcess);
		
    public BNAExporter() {
    	
    }
    
    public BNAExporter(IArchimateModel model) {
    	this.model = model;
    }
        
    public void export(Data data) throws IOException, ParseException {
    	IFolder businessFolder = model.getFolder(FolderType.BUSINESS);
 	    IFolder relationsFolder = model.getFolder(FolderType.RELATIONS);
 	    
 	    List<HLModel> models = generateModels(businessFolder, data);
 		List<HLPermRule> rules = permissionRulesProcessing(models);
 		
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
            
    /**
     * На данный момент только доступ для participant's к asset'ам 
     * @param models
     * @return
     */
    private List<HLPermRule> permissionRulesProcessing(List<HLModel> models) {
    	
    	List<HLPermRule> rules = models.stream()
    			.map(model -> model.getConcept().getSourceRelationships().stream()
    							.filter(x -> (x instanceof AccessRelationship))
    							.map(x -> (AccessRelationship) x)
    							.map(access -> HLPermRule.createRule(access, model, getHLModelByConcept(models, access.getTarget())))
    							.collect(Collectors.toList()))
    			.flatMap(List::stream)
    	        .collect(Collectors.toList());
    	
    	rules.add(HLPermRule.getNetworkAdminUserRule());
    	rules.add(HLPermRule.getNetworkAdminSystemRule());
    	rules.add(HLPermRule.getSystemAclRule());
    
    	return rules;
    }

    private List<HLModel> generateModels(IFolder folder, Data data) throws ParseException {
    	List<HLModel> models = collectAllModels(folder, data);
    	models = assetsRelationsProcessing(models);
    	models = participantsProcessing(models);
    	return models;
    }
  
    private List<HLModel> collectAllModels(IFolder folder, Data data) throws ParseException {
    	final String namespace = data.getStringValue(Data.NAMESPACE);
    	
    	List<EObject> list = new ArrayList<EObject>();
    	getElements(folder, list);
    	
    	List<IArchimateConcept> correctConcepts = list.stream()
									    			.map(x -> (IArchimateConcept) x)
													.filter(isModel).collect(Collectors.toList());
    	List<HLModel> models = new ArrayList<>();
    	for (EObject object : list) {
			if (isModel.test(object)) {
	    		HLModel model = HLModelFabric.createModel((IArchimateConcept) object, namespace);
	    		models.add(model);
			}
    	}
    	return models;
    }
    
    /**
     * На данный момент только композиция и агрегация у Asset'ов
     * @param models
     * @param concepts
     * @return
     */
    private List<HLModel> assetsRelationsProcessing(List<HLModel> models) {
    	models.stream()
    			.forEach(model -> model.getConcept().getSourceRelationships()
    						.stream()
    						.forEach(relation -> {
    							IArchimateConcept target = relation.getTarget();
    							HLField field = null;
    							if (relation instanceof CompositionRelationship) {
    								field = HLField.createField(model, target.getName(), target.getName().toLowerCase(), HLField.Type.PROPERTY);
    							} else if (relation instanceof AggregationRelationship) {
    								field = HLField.createField(model, target.getName(), target.getName().toLowerCase(), HLField.Type.REFER);
    							}
    							
    							if (field != null)
    								model.addField(field);
    						})
    					);
    	return models;
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
    													.filter(x -> builtParticipants.stream().allMatch(participant -> !participant.getName().equals(x.getName())))
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
    	
    	assets.stream().forEach(asset -> asset.getFields().stream().forEach(field -> participant.addField(field)));
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
    
    //TODO ПРИДУМАТЬ КАК УБРАТЬ ЭТУ ЗАГЛУШКУ
    public HLModel getHLModelByConcept(List<HLModel> models, IArchimateConcept concept) {
    	List<HLModel> candidates = models.stream()
    			.filter(model -> model.getID().equals(concept.getId()))
    			.collect(Collectors.toList());
    	if (candidates.size() != 1)
    		throw new IllegalArgumentException("Can\'t find model by concept \"" + concept.getName() + "\"");
    	return candidates.get(0);
    }
    
}
