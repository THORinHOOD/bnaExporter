package com.hyperledger.views.properties.access;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.archimatetool.model.IArchimateConcept;

public abstract class HLTab {
	private TabItem tab;
	private String label;
	private TabFolder folder;
	private Boolean isOpened;
	
	public HLTab(TabFolder folder, String label) {
		this.folder = folder;
		this.label = label;
		isOpened = false;
	}
			
	protected void openTab(Composite control) {
		if (!isOpened) {
			tab = new TabItem(folder, SWT.NONE);
			tab.setText(this.label);
			tab.setControl(control);
			isOpened = true;
		}
	}
	
	protected void closeTab() {
		if (isOpened) {
			tab.dispose();
			isOpened = false;
		}
	}
	
	public void setLabel(String label) {
		this.label = label;
		if (tab != null && !tab.isDisposed())
			tab.setText(label);
	}
	
	public String getLabel() {
		return label;
	}
	
	public TabItem getTab() {
		return tab;
	}
	
	public TabFolder getFolder() {
		return folder;
	}
		
	public abstract void open(IArchimateConcept concept);
	public abstract void close();
}
