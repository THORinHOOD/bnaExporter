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
	
	private SelectionListener buildSelectionListener(Runnable onClick, String errorTitle, String errorMsg) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					onClick.run();
				} catch (Exception exception) {
					view.showError(errorTitle, errorMsg);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
	}
	
	
	public JSEditorMenu(JSEditorView view) {
		super(view);
		
		toolbar = new ToolBar(view.getParent(), SWT.BORDER | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		toolbar.setLayoutData(data);
		
		ToolItem createScript = new ToolItem(toolbar, SWT.PUSH);
		createScript.setText("create");
		createScript.addSelectionListener(buildSelectionListener(this::createScript, "Script creation error", "Can't create script"));
				
		ToolItem openScript = new ToolItem(toolbar, SWT.PUSH);
		openScript.setText("open");
		openScript.addSelectionListener(buildSelectionListener(this::openScript, "Script opening error", "Can't open script"));
		
		ToolItem saveScript = new ToolItem(toolbar, SWT.PUSH);
		saveScript.setText("save");
		saveScript.setToolTipText("save");
		saveScript.addSelectionListener(buildSelectionListener(view.getEditor()::saveScript, "Script saving error", "Can't save script"));
		
		ToolItem saveAsScript = new ToolItem(toolbar, SWT.PUSH);
		saveAsScript.setText("saveAs");
		saveAsScript.setToolTipText("saveAs");
	}
	
	private void openScript() {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setText("Open Script");
        dialog.setFilterExtensions(new String[] { "*.js" } );
        String path = dialog.open();
        if (path == null) {
            return;
        }
        
        view.getEditor().updateScript(new File(path));
	}
	
	private void createScript() {
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
        
        try {
			if (script.createNewFile()) {
				view.getEditor().updateScript(script);
			} else {
				view.showError("Error creating script", "Can't create script");
			}
		} catch (IOException e) {
			view.showError("Error creating script", "Can't create script");
		}
	}

	@Override
	public void dispose() {
		toolbar.dispose();
	}
}
