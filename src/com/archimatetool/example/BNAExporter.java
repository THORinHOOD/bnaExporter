package com.archimatetool.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EObject;

import com.archimatetool.example.hl.Asset;
import com.archimatetool.example.hl.HLModel;
import com.archimatetool.example.hl.Participant;
import com.archimatetool.example.hl.Transaction;
import com.archimatetool.example.utils.Data;
import com.archimatetool.example.utils.Writer;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;

public class BNAExporter {

    private IArchimateModel model; 
    
    public BNAExporter() {
    	
    }
    
    public BNAExporter(IArchimateModel model) {
    	this.model = model;
    }
        
    public void export(Data data) throws IOException {
    	Writer writer = new Writer(data);
	    writer.start();
    	
	    IFolder business = model.getFolder(FolderType.BUSINESS);
		List<HLModel> models = getModels(business);
		
		writer.writeModels(models);
	    writer.writeReadme();
		
		writer.close();
    }
    
    private <T> T getOnly(List<T> list, Predicate<? super T> predicate) {
    	List<T> objs = list.stream().filter(predicate).collect(Collectors.toList());
    	if (objs.size() != 1)
    		throw new IllegalArgumentException("In this list can't find only one object");
		return objs.get(0);
    }
    
    private IFolder getViews(IArchimateModel model) {
    	List<IFolder> folders = model.getFolders().stream().filter(folder -> folder.getName().equals("Views")).collect(Collectors.toList());
    	if (folders.size() != 1)
    		throw new IllegalArgumentException("Must be 1 \"Views\"");
    	return folders.get(0);
    }
    
    private List<HLModel> getModels(IFolder folder) {
    	List<EObject> list = new ArrayList<EObject>();
    	getElements(folder, list);
    	
    	Predicate<EObject> isModel = x -> (x instanceof BusinessRole) || (x instanceof BusinessObject) || (x instanceof BusinessProcess);
    	
    	Function<IArchimateConcept, HLModel> getModel = x -> {
    		if (x instanceof BusinessRole) {
    			return new Participant(x);
    		} else if (x instanceof BusinessObject) {
    			return new Asset(x);
    		} else if (x instanceof BusinessProcess) {
    			return new Transaction(x);
    		}
    		return null;
    	};
    	
    	return list.stream()
    			.map(x -> (IArchimateConcept) x)
				.filter(isModel)
				.map(getModel)
				.collect(Collectors.toList());			
    }
    
    private List<Participant> getParticipants(IFolder folder) {
        List<EObject> list = new ArrayList<EObject>();
        
        getElements(folder, list);
        
        return list.stream()
				.filter(x -> (IArchimateConcept) x instanceof BusinessRole)
				.map(x -> new Participant((BusinessRole)((IArchimateConcept)x)))
				.collect(Collectors.toList());
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
