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
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IModelExporter;
import com.archimatetool.editor.ui.components.ExtendedWizardDialog;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateDiagramModel;
import com.archimatetool.model.impl.BusinessRole;

public class MyExporter implements IModelExporter {
    
    String MY_EXTENSION = ".bna";
    String MY_EXTENSION_WILDCARD = "*.bna"; 
    
    FileOutputStream fos;
    ZipOutputStream zipOut;
    
    String ps = File.separatorChar + "";
    
    public MyExporter() {
    }

    private boolean closed = false;
    private Display display;
    private Shell shell;
    
    public void initExportBtn(Display display, Shell shell) {
    	final Button exportBtn = new Button(shell, SWT.BUTTON1);
        exportBtn.setText("Button With Text");
    }
    
    
	public void PropWindow() {
        this.display = PlatformUI.getWorkbench().getDisplay();
        shell = new Shell(display);
        shell.setSize(400, 250);
        shell.setText("Export BNA");
        initExportBtn(display, shell);
                
        shell.open();
        while (!shell.isDisposed()) {
            // read the next OS event queue and transfer it to a SWT event
            if (!display.readAndDispatch())
             {
            // if there are currently no other OS event to process
            // sleep until the next OS event is available
                display.sleep();
             }
        }

        // disposes all associated windows and their components
        display.dispose();
	}
    
    @Override
    public void export(IArchimateModel model) throws IOException, IllegalArgumentException {
    	WizardDialog dialog = new ExtendedWizardDialog(Display.getCurrent().getActiveShell(), new ExportAsBNAWizard(), "ExportAsBNAWizard");
    	dialog.setPageSize(400, 400);
    	dialog.setMinimumPageSize(400, 400);
        dialog.open();
    	
//        askSaveFile();
//        
//        if(fos == null || zipOut == ull)
//            return;
//        
      //  IFolder folderViews = getViews(model);   
      //  List<ArchimateDiagramModel> views = folderViews.getElements().stream().map(view -> (ArchimateDiagramModel)view).collect(Collectors.toList());
        
//    	String namespace = getOnly(model.getProperties(), prop -> prop.getKey().equals("namespace")).getValue();
//        System.out.println(namespace);
//    
//        zipOut.close();
//        fos.close();
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
    
    private void writeModel(List<HLObject> modelObjects) throws IOException {
    	File dir = new File("model");
    	deleteDir(dir);
    	if (dir.mkdir()) {
			File file = new File(dir, "org.example.mynetwork.cto");
			
			if (file.exists())
				file.delete();
			
			byte[] buffer = new byte[1024];
			if (file.createNewFile()) {
				try (PrintWriter pw = new PrintWriter(file)) {
		    		for (HLObject obj : modelObjects)
		    			pw.println(obj.getHLView() + "\n");
		    	}
		
				try (FileInputStream fis = new FileInputStream(dir.getName() + ps + file.getName())) {
					zipOut.putNextEntry(new ZipEntry(dir.getName() + ps + file.getName()));
					int length;
					while ((length = fis.read(buffer)) > 0)
						zipOut.write(buffer, 0, length);
					zipOut.closeEntry();
				}
			}
    	}
    	
    	deleteDir(dir);
    }
    
    private static boolean deleteDir(File dir) {
    	if (!dir.exists() || !dir.isDirectory())
    		return dir.delete();
    	
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                if (!deleteDir(new File(dir, child)))
                    return false;
            }
        }

        return dir.delete(); // The directory is empty now and can be deleted.
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
        
        fos = new FileOutputStream(path);
        zipOut = new ZipOutputStream(fos);
        
        return;
    }
}
