package com.hyperledger.views.properties.tabs;

import java.util.ArrayList;
import java.util.function.Supplier;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ColumnLayout;

import com.archimatetool.model.impl.ArchimateRelationship;
import com.hyperledger.export.rules.HLPermRule;
import com.hyperledger.export.utils.HLPropertiesChangeHandler;

public class AccessPropertiesTab extends HLTabWithConcept<ArchimateRelationship> {

	public static final String LABEL = "Access Properties";

	private final SelectionListener actionListener = new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button btn = (Button) e.getSource();
			if (btn.getText().equals("Allow")) {
				setProperty(HLPermRule.ACTION_KEY, HLPermRule.ACTION_ALLOW);
			} else {
				setProperty(HLPermRule.ACTION_KEY, HLPermRule.ACTION_DENY);
			}
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {}
	};
	
	private final FocusListener disableFocus = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			getFolder().getShell().setFocus();
		}

		@Override
		public void focusLost(FocusEvent e) {}
	};
	
	private Button allow;
	private Button deny;
	
	private StyledText conditionText;
	
	private Button all;
	private Button create;
	private Button read;
	private Button update;
	private Button delete;
	
	public AccessPropertiesTab(CTabFolder folder, HLPropertiesChangeHandler propertiesChangeHandler) {
		super(folder, propertiesChangeHandler, LABEL);
	}
	
	@Override
	protected void initTab() {
		composite = new Composite(getFolder(), SWT.NONE);
		GridLayout layout = new GridLayout();
		GridData data = new GridData(GridData.FILL_BOTH);
		layout.numColumns = 1;
        composite.setLayout(layout);
        composite.setLayoutData(data);
        
        setLabel(concept.getName());
        
		initAllowTypeGroup(composite);
		initOperationsGroup(composite);
		initVariablesGroup(composite);
		initConditionGroup(composite);
	}
	
	private void initVariablesGroup(Composite composite) {
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		
		Group variablesGroup = new Group(composite, SWT.NULL);
		variablesGroup.setText("Condition");
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		variablesGroup.setLayout(gridLayout);
		variablesGroup.setLayoutData(gridData);
		
		Label participantLabel = new Label(variablesGroup, SWT.NULL);
		participantLabel.setText("participant variable : ");
		
		Text participantVariable = new Text(variablesGroup, SWT.BORDER);
		participantVariable.setLayoutData(gridData);
		participantVariable.setText(getProperty(HLPermRule.PARTICIPANT_VARIABLE_KEY, ""));
		participantVariable.addModifyListener(event -> setProperty(HLPermRule.PARTICIPANT_VARIABLE_KEY, participantVariable.getText()));
		
		Label resourceLabel = new Label(variablesGroup, SWT.NULL);
		resourceLabel.setText("resource variable : ");
		
		Text resourceVariable = new Text(variablesGroup, SWT.BORDER);
		resourceVariable.setLayoutData(gridData);
		resourceVariable.setText(getProperty(HLPermRule.RESOURCE_VARIABLE_KEY, ""));
		participantVariable.addModifyListener(event -> setProperty(HLPermRule.RESOURCE_VARIABLE_KEY, resourceVariable.getText()));
	}
	
	private void initConditionGroup(Composite composite) {
		GridData gridData = new GridData(GridData.FILL_BOTH);
		Group conditionGroup = new Group(composite, SWT.NULL);
		conditionGroup.setText("Condition");
		conditionGroup.setLayout(new GridLayout());
		conditionGroup.setLayoutData(gridData);

		conditionText = new StyledText(conditionGroup, SWT.H_SCROLL | SWT.V_SCROLL);
		conditionText.setLayoutData(gridData);
		conditionText.setLayout(new GridLayout());
		
		conditionText.setText(getProperty(HLPermRule.CONDITION_KEY, ""));
		conditionText.addModifyListener(event -> setProperty(HLPermRule.CONDITION_KEY, conditionText.getText()));	
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
		
		all = initCheckButton(operationsTypes, "All");
		create = initCheckButton(operationsTypes, "Create");
		read = initCheckButton(operationsTypes, "Read");
		update = initCheckButton(operationsTypes, "Update");
		delete = initCheckButton(operationsTypes, "Delete");
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
		
		create.addSelectionListener(buildOperationSelectListener(HLPermRule.CREATE));
		read.addSelectionListener(buildOperationSelectListener(HLPermRule.READ));
		update.addSelectionListener(buildOperationSelectListener(HLPermRule.UPDATE));
		delete.addSelectionListener(buildOperationSelectListener(HLPermRule.DELETE));
		all.addSelectionListener(buildOperationSelectListener(HLPermRule.ALL));
		
		try {
			Integer value = Integer.valueOf(getProperty(HLPermRule.OPERATION_KEY, "0"));
			create.setSelection((HLPermRule.CREATE & value) != 0);
			read.setSelection((HLPermRule.READ & value) != 0);
			update.setSelection((HLPermRule.UPDATE & value) != 0);
			delete.setSelection((HLPermRule.DELETE & value) != 0);
			all.setSelection((HLPermRule.ALL & value) >= HLPermRule.ALL);
		} catch(Exception ex) {
		}
		
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
		
		allow = initRadioButton(allowType, "Allow");
		deny = initRadioButton(allowType, "Deny");
		
		allow.addSelectionListener(actionListener);
		deny.addSelectionListener(actionListener);
		
		if(getProperty(HLPermRule.ACTION_KEY, HLPermRule.ACTION_ALLOW).equals(HLPermRule.ACTION_DENY)) {
			allow.setSelection(false);
			deny.setSelection(true);
		} else {
			allow.setSelection(true);
			deny.setSelection(false);
		}
		
		allowType.pack();
	}

	@Override
	protected void onConceptChanging() {
		setLabel(concept.getName());	
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
	
	private SelectionListener buildOperationSelectListener(int FLAG) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				if (btn.getSelection()) {
					try {
						int current = Integer.valueOf(getProperty(HLPermRule.OPERATION_KEY, "0"));
						setProperty(HLPermRule.OPERATION_KEY, String.valueOf(current | FLAG));
					} catch(Exception ex) {
					}
				} else {
					if (FLAG != HLPermRule.ALL) {
						try {
							int current = Integer.valueOf(getProperty(HLPermRule.OPERATION_KEY, "0"));
							setProperty(HLPermRule.OPERATION_KEY, String.valueOf(current & (FLAG ^ HLPermRule.ALL) ));
						} catch(Exception ex) {
						}
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
	}
}
