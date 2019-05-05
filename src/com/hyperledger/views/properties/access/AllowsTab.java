package com.hyperledger.views.properties.access;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.ui.forms.widgets.ColumnLayout;

public class AllowsTab extends HLTab {

	public static final String LABEL = "Allows";
	
	private final FocusListener disableFocus = new FocusListener() {
		@Override
		public void focusGained(FocusEvent e) {
			getFolder().getShell().setFocus();
		}

		@Override
		public void focusLost(FocusEvent e) {}
	};
	
	public AllowsTab(TabFolder folder) {
		super(folder, LABEL);
		
		final Composite composite = new Composite(folder, SWT.NONE);
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 1;
        composite.setLayout(layout);
        
		initAllowTypeGroup(composite);
		initOperationsGroup(composite);
		
		getTab().setControl(composite);
	}
	
	private void initOperationsGroup(Composite composite) {
		Group operationsTypes = new Group(composite, SWT.NULL);
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
							}
						}
						
						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
							if (allBtns.stream().allMatch(btn -> btn.getSelection() || btn.equals(all))) {
								all.setSelection(true);
							}
						}
					});
				}
			});
		
		operationsTypes.pack();
	}
	
	private void initAllowTypeGroup(Composite composite) {
		Group allowType = new Group(composite, SWT.NULL);
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
}
