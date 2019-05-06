package com.hyperledger.views.properties.access;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.forms.widgets.ColumnLayout;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.impl.AccessRelationship;

public class AccessPropertiesTab extends HLTab {

	public static final String LABEL = "Access Properties";
	
	private final FocusListener disableFocus = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			getFolder().getShell().setFocus();
		}

		@Override
		public void focusLost(FocusEvent e) {}
	};
	
	private Composite composite;
	
	public AccessPropertiesTab(TabFolder folder) {
		super(folder, LABEL);
	}
	
	public void open(IArchimateConcept rel) {
		initTab((AccessRelationship) rel);
		openTab(composite);
	}
	
	public void close() {
		closeTab();
	}
	
	private void initTab(AccessRelationship rel) {
		composite = new Composite(getFolder(), SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(GridData.FILL_BOTH);
		layout.numColumns = 1;
        composite.setLayout(layout);
        composite.setLayoutData(data);
        
        setLabel(rel.getName());
        
		initAllowTypeGroup(composite);
		initOperationsGroup(composite);
		initConditionGroup(composite);
	}
	
	private void initConditionGroup(Composite composite) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		Group conditionGroup = new Group(composite, SWT.NULL);
		conditionGroup.setText("Condition");
		conditionGroup.setLayout(new GridLayout());
		conditionGroup.setLayoutData(gridData);
		

		StyledText text = new StyledText(conditionGroup, SWT.H_SCROLL | SWT.V_SCROLL);
		text.setLayoutData(gridData);
		text.setLayout(new GridLayout());
		
		conditionGroup.pack();
	}
	
	private void initOperationsGroup(Composite composite) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		Group operationsTypes = new Group(composite, SWT.NULL);
		operationsTypes.setLayoutData(gridData);
		operationsTypes.setText("Operations");
		RowLayout layout = new RowLayout();
		operationsTypes.setLayout(layout);
		
		ArrayList<Button> allBtns = new ArrayList<>();
		
		Button all = initCheckButton(operationsTypes, "All");
		Button create = initCheckButton(operationsTypes, "Create");
		Button read = initCheckButton(operationsTypes, "Read");
		Button update = initCheckButton(operationsTypes, "Update");
		Button delete = initCheckButton(operationsTypes, "Delete");
		allBtns.add(all);
		allBtns.add(create);
		allBtns.add(read);
		allBtns.add(update);
		allBtns.add(delete);
		
		all.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (all.getSelection())
					allBtns.stream().forEach(btn -> btn.setSelection(true));
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				if (all.getSelection())
					allBtns.stream().forEach(btn -> btn.setSelection(true));
			}
		});
		
		allBtns
			.stream()
			.forEach(button -> {
				if (!button.equals(all)) {
					button.addSelectionListener(new SelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (allBtns.stream().allMatch(btn -> btn.getSelection() || btn.equals(all))) {
								all.setSelection(true);
							} else {
								all.setSelection(false);
							}
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							if (allBtns.stream().allMatch(btn -> btn.getSelection() || btn.equals(all))) {
								all.setSelection(true);
							} else {
								all.setSelection(false);
							}
						}
					});
				}
			});
		
		operationsTypes.pack();
	}
	
	private void initAllowTypeGroup(Composite composite) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		Group allowType = new Group(composite, SWT.NULL);
		allowType.setLayoutData(gridData);
		allowType.setText("Type of access rule");
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 1;
		allowType.setLayout(layout);
		
		initRadioButton(allowType, "Allow").setSelection(true);
		initRadioButton(allowType, "Deny").setSelection(false);
		
		allowType.pack();
	}

	private Button initCheckButton(Composite composite, String label) {
		return initButton(composite, label, SWT.CHECK);
	}
	
	private Button initRadioButton(Composite composite, String label) {
		return initButton(composite, label, SWT.RADIO);
	}
	
	private Button initButton(Composite composite, String label, int style) {
		Button btn = new Button(composite, style);
		btn.setText(label);
		btn.addFocusListener(disableFocus);
		return btn;
	}
	
	public void dispose() {
		getTab().dispose();
	}
}
