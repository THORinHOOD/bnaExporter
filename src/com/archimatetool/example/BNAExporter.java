package com.archimatetool.example;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.example.exc.DuplicateObjectNames;
import com.archimatetool.example.exc.UnknownTypeException;
import com.archimatetool.example.hl.models.HLField;
import com.archimatetool.example.hl.models.HLModel;
import com.archimatetool.example.hl.models.HLModelFabric;
import com.archimatetool.example.hl.models.Transaction;
import com.archimatetool.example.hl.pcl.HLPermRule;
import com.archimatetool.example.utils.Data;
import com.archimatetool.example.utils.Writer;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.impl.AccessRelationship;
import com.archimatetool.model.impl.ArchimateRelationship;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;

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
 	    
 	    List<HLModel> models = getModels(businessFolder, data);
 		List<ArchimateRelationship> relations = getModelRelations(relationsFolder);
 		List<HLPermRule> rules = getPermRules(models, relations);
 		//models = acceptRelations(models, relations);
 		
 		checkModels(models);
    	writeBNA(data, models, rules);
    }
    
    private void writeBNA(Data data, List<HLModel> models, List<HLPermRule> rules) throws IOException, ParseException {
    	Writer writer = new Writer(model, data);
	    writer.start();
    	
		writer.writeModels(models);
	    writer.writeScripts(models.stream().filter(x -> x instanceof Transaction).map(x -> (Transaction) x).collect(Collectors.toList()));
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
    
//    private Map<String, HLModel> acceptRelations(Map<String, HLModel> models, List<ArchimateRelationship> relations) {
//    	for (ArchimateRelationship relation : relations) {
//    		HLModel source = models.get(relation.getSource().getId());
//			HLModel target = models.get(relation.getTarget().getId());
//			
//    		if (relation instanceof CompositionRelationship) {
//    			HLField field = HLField.createField(source, target.getName(), target.getName().toLowerCase(), HLField.Type.PROPERTY);
//    			source.addField(field);
//    		} else if (relation instanceof AggregationRelationship) {
//    			HLField field = HLField.createField(source, target.getName(), target.getName().toLowerCase(), HLField.Type.REFER);
//    			source.addField(field);
//    		}
//    	}
//    	    	
//    	return models;
//    }
    
    private List<HLPermRule> getPermRules(List<HLModel> models, List<ArchimateRelationship> relations) {
    	List<HLPermRule> rules = new ArrayList<>(); 
    	for (ArchimateRelationship relation : relations) {
    		 if (relation instanceof AccessRelationship) {
    			 rules.add(HLPermRule.createRule((AccessRelationship)relation, models));
    		 }
    	}
    	rules.add(HLPermRule.getNetworkAdminUserRule());
    	rules.add(HLPermRule.getNetworkAdminSystemRule());
    	rules.add(HLPermRule.getSystemAclRule());
    
    	return rules;
    }
    
    private List<ArchimateRelationship> getModelRelations(IFolder folder) {
    	return folder.getElements().stream()
    		.filter(x -> x instanceof ArchimateRelationship)
    		.map(x -> (ArchimateRelationship) x)
    		.filter(x -> isModel.test(x.getSource()) && isModel.test(x.getTarget()))
    		.collect(Collectors.toList());
    }
    
    private List<HLModel> getModels(IFolder folder, Data data) throws ParseException {
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
