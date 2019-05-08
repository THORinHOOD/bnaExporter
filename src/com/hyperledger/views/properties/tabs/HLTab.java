package com.hyperledger.views.properties.tabs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.archimatetool.model.IArchimateConcept;

public abstract class HLTab {
	private CTabItem tab;
	private String label;
	private CTabFolder folder;
	private Boolean isOpened;
	
	public HLTab(CTabFolder folder, String label) {
		this.folder = folder;
		this.label = label;
		isOpened = false;
	}
			
	protected void openTab(Composite control) {
		if (!isOpened) {
			tab = new CTabItem(folder, SWT.NONE);
			setLabel(this.label);
			tab.setControl(control);
			tab.setShowClose(true);
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
		if (label.trim().equals("")) {
			label = "(Unknown)";
		}
		this.label = label;
		if (tab != null && !tab.isDisposed())
			tab.setText(label);
	}
	
	public String getLabel() {
		return label;
	}
	
	public CTabItem getTab() {
		return tab;
	}
	
	public CTabFolder getFolder() {
		return folder;
	}
	
	public void dispose() {
		tab.dispose();
	}
	
	public void showError(String title, String message) {
		folder.getDisplay().asyncExec
	    (new Runnable() {
	        public void run() {
	            MessageDialog.openWarning(folder.getShell(), title, message);
	        }
	    });
	}
}
