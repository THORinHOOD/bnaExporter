/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.archimatetool.editor.model.IModelExporter;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.BusinessRole;

import sun.misc.ObjectInputFilter.FilterInfo;

public class MyExporter implements IModelExporter {
    
    String MY_EXTENSION = ".bna";
    String MY_EXTENSION_WILDCARD = "*.bna"; 
    
    FileOutputStream fos;
    ZipOutputStream zipOut;
    
    public MyExporter() {
    }

    @Override
    public void export(IArchimateModel model) throws IOException {
        askSaveFile();
        
        if(fos == null || zipOut == null)
            return;
        
        List<HLObject> hlObjects = getHLObjects(model.getFolder(FolderType.BUSINESS));
        writeModel(hlObjects.stream().filter( x -> x instanceof HLModel ).collect(Collectors.toList()));
        
        zipOut.close();
        fos.close();
    }
    
    private void writeModel(List<HLObject> modelObjects) throws IOException {
    	File file = new File("org.example.mynetwork.cto");
    	file.delete();
    	byte[] buffer = new byte[1024];
    	if (file.createNewFile()) {
    		try (PrintWriter pw = new PrintWriter(file)) {
	    		for (HLObject obj : modelObjects)
	    			pw.println(obj.getHLView());
	    	}

			try (FileInputStream fis = new FileInputStream(file)) {
				zipOut.putNextEntry(new ZipEntry(file.getName()));
				int length;
				while ((length = fis.read(buffer)) > 0)
					zipOut.write(buffer, 0, length);
				zipOut.closeEntry();
			}
    	}
    }
    
    private List<HLObject> getHLObjects(IFolder folder) {
        List<EObject> list = new ArrayList<EObject>();
        
        getElements(folder, list);
        
        return list.stream()
				.filter(x -> (IArchimateConcept)x instanceof BusinessRole)
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

    /**
     * Ask user for file name to save to
     * @throws FileNotFoundException 
     */
    private void askSaveFile() throws FileNotFoundException {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        dialog.setText(Messages.MyExporter_0);
        dialog.setFilterExtensions(new String[] { MY_EXTENSION_WILDCARD, "*.*" } );
        String path = dialog.open();
        if(path == null) {
            return; //null;
        }
        
        // Only Windows adds the extension by default
        if(dialog.getFilterIndex() == 0 && !path.endsWith(MY_EXTENSION)) {
            path += MY_EXTENSION;
        }
        
//        // Make sure the file does not already exist
//        if(file.exists()) {
//            boolean result = MessageDialog.openQuestion(Display.getCurrent().getActiveShell(),
//                    Messages.MyExporter_0,
//                    NLS.bind(Messages.MyExporter_1, file));
//            if(!result) {
//                return null;
//            }
//        }
        
        fos = new FileOutputStream(path);
        zipOut = new ZipOutputStream(fos);
        
        return;
    }
}
