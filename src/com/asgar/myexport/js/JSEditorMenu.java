package com.asgar.myexport.js;

import java.io.File;
import java.io.IOException;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

public class JSEditorMenu extends JSEditorObject {
	
	private ToolBar toolbar;
	
	public JSEditorMenu(JSEditorView view) {
		super(view);
		
		toolbar = new ToolBar(view.getParent(), SWT.BORDER | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		toolbar.setLayoutData(data);
		
		
		ToolItem createScript = new ToolItem(toolbar, SWT.PUSH);
		createScript.setText("create");
		createScript.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					createScript();
				} catch (IOException e1) {
					view.showError("Error creating script", "Can't create script");
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		ToolItem openScript = new ToolItem(toolbar, SWT.PUSH);
		openScript.setText("open");
		openScript.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					openScript();
				} catch (IOException e1) {
					view.showError("Error opening script", "Can't open script");
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		ToolItem saveScript = new ToolItem(toolbar, SWT.PUSH);
		saveScript.setText("save");
		saveScript.setToolTipText("save");
		saveScript.addSelectionListener(new SelectionListener() {	
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					view.getEditor().saveScript();
				} catch (IOException e1) {
					view.showError("Error saving script", "Can't save script");
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		ToolItem saveAsScript = new ToolItem(toolbar, SWT.PUSH);
		saveAsScript.setText("saveAs");
		saveAsScript.setToolTipText("saveAs");
	}
	
	private void openScript() throws IOException {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setText("Open Script");
        dialog.setFilterExtensions(new String[] { "*.js" } );
        String path = dialog.open();
        if (path == null) {
            return;
        }
        
        view.getEditor().updateScript(new File(path));
	}
	
	private void createScript() throws IOException {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        dialog.setText("Create Script");
        dialog.setFilterExtensions(new String[] { "*.js" } );
        String path = dialog.open();
        if(path == null) {
            return;
        }
        
        if(dialog.getFilterIndex() == 0 && !path.endsWith(".js")) {
            path += ".js";
        }
        
        File script = new File(path);
        if (script.exists()) {
        	view.showError("Error creating script", "Script \"" + path + "\" already exists");
        	return;
        }
        
        if (script.createNewFile()) {
        	view.getEditor().updateScript(script);
        } else {
        	view.showError("Error creating script", "Can't create script");
        }
	}

	@Override
	public void dispose() {
		toolbar.dispose();
	}
}
