package com.archimatetool.example;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.example.exc.DuplicateObjectNames;
import com.archimatetool.example.exc.UnknownTypeException;
import com.archimatetool.example.hl.Asset;
import com.archimatetool.example.hl.HLField;
import com.archimatetool.example.hl.HLModel;
import com.archimatetool.example.hl.Participant;
import com.archimatetool.example.hl.Transaction;
import com.archimatetool.example.utils.Data;
import com.archimatetool.example.utils.Writer;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
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
	
    	Writer writer = new Writer(data);
	    writer.start();
    	
	    IFolder businessFolder = model.getFolder(FolderType.BUSINESS);
	    IFolder relationsFolder = model.getFolder(FolderType.RELATIONS);
	    
	    Map<String, HLModel> models = getModels(businessFolder);
		List<ArchimateRelationship> relations = getModelRelations(relationsFolder);
		models = acceptRelations(models, relations);
		
		checkModels(models.values());
		
		writer.writeModels(models.values());
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
    			if (!field.isPrimitive() && !names.contains(field.getType())) {
    				throw new UnknownTypeException(model, field);
    			}
    		}
    	}
    }
    
    private Map<String, HLModel> acceptRelations(Map<String, HLModel> models, List<ArchimateRelationship> relations) {
    	for (ArchimateRelationship relation : relations) {
    		HLModel source = models.get(relation.getSource().getId());
			HLModel target = models.get(relation.getTarget().getId());
			
    		if (relation instanceof CompositionRelationship) {
    			HLField field = HLField.createField(source, target.getName(), target.getName().toLowerCase(), HLField.Type.PROPERTY);
    			source.addField(field);
    		} else if (relation instanceof AggregationRelationship) {
    			HLField field = HLField.createField(source, target.getName(), target.getName().toLowerCase(), HLField.Type.REFER);
    			source.addField(field);
    		}
    	}
    	    	
    	return models;
    }
    
    private List<ArchimateRelationship> getModelRelations(IFolder folder) {
    	return folder.getElements().stream()
    		.filter(x -> x instanceof ArchimateRelationship)
    		.map(x -> (ArchimateRelationship) x)
    		.filter(x -> isModel.test(x.getSource()) && isModel.test(x.getTarget()))
    		.collect(Collectors.toList());
    }
    
    private Map<String, HLModel> getModels(IFolder folder) {
    	List<EObject> list = new ArrayList<EObject>();
    	getElements(folder, list);
    	
    	
    	Function<IArchimateConcept, HLModel> getModel = x -> {
    		try {
	    		if (x instanceof BusinessRole) {
	    			return new Participant(x);
	    		} else if (x instanceof BusinessObject) {
	    			return new Asset(x);
	    		} else if (x instanceof BusinessProcess) {
	    			return new Transaction(x);
	    		}
    		} catch(ParseException ex) {
    			System.out.println(ex.getMessage());
    		}
    		
    		return null;
    	};
    	
    	return list.stream()
    			.map(x -> (IArchimateConcept) x)
				.filter(isModel)
				.map(getModel)
				.collect(Collectors.toMap(x -> x.getID(), x -> x));			
    }
        
    private void getElements(IFolder folder, List<EObject> list) {
        for(EObject object : folder.getElements()) {
            list.add(object);
        }
        
        for(IFolder f : folder.getFolders()) {
            getElements(f, list);
        }
    }

    
}
