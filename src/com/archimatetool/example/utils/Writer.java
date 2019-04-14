package com.archimatetool.example.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.archimatetool.example.Messages;
import com.archimatetool.example.hl.HLObject;

public class Writer {
	
    String MY_EXTENSION = ".bna";
    String MY_EXTENSION_WILDCARD = "*.bna"; 
    
    FileOutputStream fos;
    ZipOutputStream zipOut;
    
    String ps = File.separatorChar + "";
    
	private Data data;
	
	public Writer(Data data) {
		this.data = data;
	}
	
	public void writeReadme() throws FileNotFoundException, IOException {
		File file = new File("README.md");
		
		if (file.exists())
			file.delete();
		
		if (file.createNewFile()) {
			ArrayList<String> lines = new ArrayList<String>();
			lines.add("# " + data.getStringValue(Data.BUSINESS_NETWORK_NAME) + "\n");
			lines.add(data.getStringValue(Data.DESCRIPTION));
			writeFile(file, file.getName(), lines);
		}
		
		if (file.exists())
			file.delete();
	}
	
	public void writeModels(List<? extends HLObject> modelObjects) throws IOException {
    	File dir = new File("models");
    	deleteDir(dir);
    	if (dir.mkdir()) {
			File file = new File(dir, data.getStringValue(Data.NAMESPACE) + ".cto");
			
			if (file.exists())
				file.delete();
			
			if (file.createNewFile()) {
				ArrayList<String> lines = new ArrayList<String>();
				lines.add("\n" + "namespace " + data.getStringValue(Data.NAMESPACE) + "\n");
				for (HLObject obj : modelObjects)
	    			lines.add(obj.getHLView() + "\n");
				
				writeFile(file, dir.getName() + ps + file.getName(), lines);
			}
    	}
    	
    	deleteDir(dir);
    }
	
	private void writeFile(File file, String pathInZip,  List<String> lines) throws IOException {
		byte[] buffer = new byte[1024];
		
		try (PrintWriter pw = new PrintWriter(file)) {
			for (String line : lines)
				pw.println(line);
		}
		
		try (FileInputStream fis = new FileInputStream(pathInZip)) {
			zipOut.putNextEntry(new ZipEntry(pathInZip));
			int length;
			while ((length = fis.read(buffer)) > 0)
				zipOut.write(buffer, 0, length);
			zipOut.closeEntry();
		}
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
    
    public void start() throws FileNotFoundException {
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
    
    public void close() {
    	try {
	    	zipOut.close();
	    	fos.close();
    	} catch(IOException ex) {
    		System.out.println(ex.getMessage());
    	}
    }
}
