package com.hyperledger.views.properties.access;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

public class HLTab {
	private TabItem tab;
	private String label;
	private TabFolder folder;
	
	public HLTab(TabFolder folder, String label) {
		this.folder = folder;
		this.label = label;
		
		tab = new TabItem(folder, SWT.NONE);
		tab.setText(this.label);
	}
			
	public void setLabel(String label) {
		this.label = label;
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
	
}
