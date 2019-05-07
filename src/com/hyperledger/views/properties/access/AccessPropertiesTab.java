package com.hyperledger.views.properties.access;

import java.util.ArrayList;

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
import org.eclipse.ui.forms.widgets.ColumnLayout;

import com.archimatetool.model.impl.AccessRelationship;
import com.hyperledger.export.rules.HLPermRule;
import com.hyperledger.views.properties.HLTabWithConcept;

public class AccessPropertiesTab extends HLTabWithConcept<AccessRelationship> {

	public static final String LABEL = "Access Properties";
			
	private static final String ACTION_KEY = "ACTION";
	private static final String OPERATION_KEY = "OPERATION";
	private static final String CONDITION_KEY = "CONDITION";
	
	private final SelectionListener actionListener = new SelectionListener() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			Button btn = (Button) e.getSource();
			if (btn.getText().equals("Allow") && btn.getSelection()) {
				setProperty(ACTION_KEY, HLPermRule.ACTION_ALLOW);
			} else {
				setProperty(ACTION_KEY, HLPermRule.ACTION_DENY);
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
	
	public AccessPropertiesTab(CTabFolder folder) {
		super(folder, LABEL);
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
		
		text.setText(getProperty(CONDITION_KEY, ""));
		text.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				setProperty(CONDITION_KEY, text.getText());
			}
		});
		
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
		
		create.addSelectionListener(buildOperationSelectListener(HLPermRule.CREATE));
		read.addSelectionListener(buildOperationSelectListener(HLPermRule.READ));
		update.addSelectionListener(buildOperationSelectListener(HLPermRule.UPDATE));
		delete.addSelectionListener(buildOperationSelectListener(HLPermRule.DELETE));
		all.addSelectionListener(buildOperationSelectListener(HLPermRule.ALL));
		
		try {
			create.setSelection((HLPermRule.CREATE & Integer.valueOf(getProperty(OPERATION_KEY, "0"))) != 0);
			read.setSelection((HLPermRule.READ & Integer.valueOf(getProperty(OPERATION_KEY, "0"))) != 0);
			update.setSelection((HLPermRule.UPDATE & Integer.valueOf(getProperty(OPERATION_KEY, "0"))) != 0);
			delete.setSelection((HLPermRule.DELETE & Integer.valueOf(getProperty(OPERATION_KEY, "0"))) != 0);
			all.setSelection((HLPermRule.ALL & Integer.valueOf(getProperty(OPERATION_KEY, "0"))) >= HLPermRule.ALL);
		} catch(Exception ex) {
			//TODO
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
		
		Button allow = initRadioButton(allowType, "Allow");
		Button deny = initRadioButton(allowType, "Deny");
		
		allow.addSelectionListener(actionListener);
		deny.addSelectionListener(actionListener);
		
		if(getProperty(ACTION_KEY, HLPermRule.ACTION_ALLOW).equals(HLPermRule.ACTION_DENY)) {
			allow.setSelection(false);
			deny.setSelection(true);
		} else {
			allow.setSelection(true);
			deny.setSelection(false);
		}
		
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
	
	private SelectionListener buildOperationSelectListener(int FLAG) {
		return new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button btn = (Button) e.getSource();
				if (btn.getSelection()) {
					try {
						int current = Integer.valueOf(getProperty(OPERATION_KEY, "0"));
						setProperty(OPERATION_KEY, String.valueOf(current | FLAG));
					} catch(Exception ex) {
						//TODO
					}
				} else {
					if (FLAG != HLPermRule.ALL) {
						try {
							int current = Integer.valueOf(getProperty(OPERATION_KEY, "0"));
							setProperty(OPERATION_KEY, String.valueOf(current & (FLAG ^ HLPermRule.ALL) ));
						} catch(Exception ex) {
							//TODO
						}
					}
				}
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		};
	}
	
	public void dispose() {
		getTab().dispose();
	}
	
}
