package com.hyperledger.views.properties.tabs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.archimatetool.model.impl.BusinessProcess;
import com.hyperledger.export.utils.HLPropertiesChangeHandler;
import com.hyperledger.export.utils.ScriptsHandler;

public class ScriptsEditTab extends HLTabWithConcept<BusinessProcess> {

	public static final String LABEL = "JS Editor";
	public static final String SCRIPT_KEY = "SCRIPT";
	
	private ToolBar toolbar;
	private StyledText scriptEditor;
	private Label scriptPath;
	
	private Optional<File> currentScript = Optional.empty();
	
	public ScriptsEditTab(CTabFolder folder, HLPropertiesChangeHandler propertyChangeListener) {
		super(folder, propertyChangeListener, LABEL);
	}
	
	@Override
	protected void initTab() {
		composite = new Composite(getFolder(), SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(GridData.FILL_BOTH);
		layout.numColumns = 2;
        composite.setLayout(layout);
        composite.setLayoutData(data);
        
        scriptPath = new Label(composite, SWT.NONE);
        initMenu();
        initScriptEditor();
        
        setLabel(concept.getName());
	}
	
	private void initMenu() {
		toolbar = new ToolBar(composite, SWT.BORDER | SWT.HORIZONTAL);
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
		saveScript.addSelectionListener(buildSelectionListener(this::saveScript, "Script saving error", "Can't save script"));
	}
	
	private void initScriptEditor() {
		scriptEditor = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		scriptEditor.setLayoutData(data);
		scriptEditor.setEnabled(true);
		scriptEditor.setEditable(true);

		openScript(getProperty(SCRIPT_KEY));
	}
	
	private void createScript() {
		currentScript = Optional.empty();
		scriptEditor.setText("");
		scriptPath.setText("New Script");
		removeProperty(SCRIPT_KEY);
	}
	
	private void saveScript() {
		if (!currentScript.isPresent() || !currentScript.get().exists()) {
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
	        	showError("Error creating script", "Script \"" + path + "\" already exists");
	        	return;
	        }
	        
	        try {
				if (script.createNewFile()) {
					setProperty(SCRIPT_KEY, script.getPath());
					currentScript = Optional.of(script);
				} else {
					showError("Error creation script", "Can't create script");
				}
			} catch (IOException e) {
				showError("Error creation script", "Can't create script");
			}
		}
		
		if (!currentScript.isPresent() || !currentScript.get().exists()) {
			return;
		}
		
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(currentScript.get()));
			writer.write(scriptEditor.getText());	
			writer.flush();
			writer.close();
			setProperty(SCRIPT_KEY, currentScript.get().getPath());
		} catch (IOException e) {
			showError("Script saving error", "Can't save script");
		}
	}
	
	private void openScript() {
		FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setText("Open Script");
        dialog.setFilterExtensions(new String[] { "*.js" } );
        String path = dialog.open();
        openScript(path);
	}
	
	private void openScript(String path) {
		setCurrentScript(path);
		if (currentScript.isPresent()) {
			if (scriptEditor != null && !scriptEditor.isDisposed()) {
				Optional<String> text = ScriptsHandler.scriptToText(currentScript.get());
				if (text.isPresent()) {
					scriptEditor.setText(text.get());
				} else {
					scriptEditor.setText("");
				}
				setProperty(SCRIPT_KEY, currentScript.get().getPath());
				scriptPath.setText(currentScript.get().getPath());
			} else {
				scriptEditor.setText("");
			}
		} else {
			scriptEditor.setText("");
		}
	}
	
	private boolean setCurrentScript(String path) {
		if (path == null) {
			currentScript = Optional.empty();
			return false;
		}
		File file = new File(path);
		if (file.exists()) {
			currentScript = Optional.of(file);
			return true;
		} else {
			currentScript = Optional.empty();
			return false;
		}
	}
		
	private SelectionListener buildSelectionListener(Runnable onClick, String errorTitle, String errorMsg) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				try {
					onClick.run();
				} catch (Exception exception) {
					showError(errorTitle, errorMsg);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
	}
	
	@Override
	protected void onConceptChanging() {
		setLabel(concept.getName());
	}

}
