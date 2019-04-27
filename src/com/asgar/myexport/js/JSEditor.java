package com.asgar.myexport.js;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;

import com.archimatetool.example.utils.ScriptsHandler;

public class JSEditor extends JSEditorObject {
	
	private StyledText text;
	private File currentScript;
	
	public JSEditor(JSEditorView view) {
		super(view);
		
		text = new StyledText(view.getParent(), SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		text.setLayoutData(data);
		text.setEditable(false);
		
	}
	
	public void updateScript(File script) {
		currentScript = script;
		textSetScript(currentScript);
	}
	
	public void saveScript() throws IOException {
		if (currentScript == null && !currentScript.exists()) 
			return;
		
		BufferedWriter writer = new BufferedWriter(new FileWriter(currentScript));
		writer.write(text.getText());
		writer.flush();
		writer.close();
	}
	
	private void textSetScript(File script) {
    	if ((script == null) || !script.exists()) {
	    	setEmpty();
    	} else if (script.exists()) {
    		try {
		    	text.setEnabled(true);
		     	text.setEditable(true);
		     	text.setText(ScriptsHandler.scriptToText(script));
		     	view.setScriptTitle(script.getName());
    		} catch(IOException ex) {
    			setEmpty();
    		}
    	}
    }
	
	private void setEmpty() {
		text.setEnabled(false);
     	text.setEditable(false);
     	text.setText("");
		view.setScriptTitle("");
	}
	
	public void setFocus() {
		if (text != null) {
			text.setFocus();
		}
	}
	
	public File getCurrentScript() {
		return currentScript;
	}

	@Override
	public void dispose() {
		text.dispose();
	}
}
