package com.hyperledger.views.properties.tabs;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import com.archimatetool.model.impl.BusinessProcess;
import com.hyperledger.export.utils.HLPropertiesChangeHandler;

public class ScriptsEditTab extends HLTabWithConcept<BusinessProcess> {

	public static final String LABEL = "JS Editor";
	
	private ToolBar toolbar;
	private Combo combo;
	private Button link;
	private StyledText scriptEditor;
	
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
        
        initMenu();
        initCombo();
        initLinkButton();
        initScriptEditor();
	}
	
	private void initMenu() {
		toolbar = new ToolBar(composite, SWT.BORDER | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		toolbar.setLayoutData(data);
		
		ToolItem createScript = new ToolItem(toolbar, SWT.PUSH);
		createScript.setText("create");
		
		ToolItem openScript = new ToolItem(toolbar, SWT.PUSH);
		openScript.setText("open");
		
		ToolItem saveScript = new ToolItem(toolbar, SWT.PUSH);
		saveScript.setText("save");
	}
	
	private void initCombo() {
		combo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(data);
	}
	
	private void initLinkButton() {
		link = new Button(composite, SWT.PUSH);
		link.setText("Link");
	}
	
	private void initScriptEditor() {
		scriptEditor = new StyledText(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		scriptEditor.setLayoutData(data);
		scriptEditor.setEditable(false);
	}

	@Override
	protected void onConceptChanging() {
		// TODO Auto-generated method stub
		
	}

}
