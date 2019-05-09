package com.hyperledger.views;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.part.ViewPart;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.impl.ArchimateRelationship;
import com.archimatetool.model.impl.BusinessProcess;
import com.hyperledger.export.rules.HLPermRule;
import com.hyperledger.export.utils.HLPropertiesChangeHandler;
import com.hyperledger.export.utils.HLSelectionHandler;
import com.hyperledger.views.properties.tabs.AccessPropertiesTab;
import com.hyperledger.views.properties.tabs.HLTabWithConcept;
import com.hyperledger.views.properties.tabs.ScriptsEditTab;

public class HLView extends ViewPart {

	public static final String ID = "com.hyperledger.views.properties.PropertiesView";

	@Inject IWorkbench workbench;
	
	private CTabFolder folder;
	private HLSelectionHandler selectionHandler;
	private HLPropertiesChangeHandler propertiesChangeHandler;
	private Map<String, HLTabWithConcept> tabs;

	@Override
	public void createPartControl(Composite parent) {
		tabs = new HashMap<>();
		folder = new CTabFolder(parent, SWT.NONE);
		initSelectionHandler();
		initPropertiesChangeHandler();		
	}
	
	private void archimateConceptSelectionHandler(Boolean isConcept, IArchimateConcept concept) {
		if (isConcept) {
			if (concept instanceof ArchimateRelationship) {
				ArchimateRelationship relation = (ArchimateRelationship) concept;
				if (HLPermRule.isRule(relation)) {
					AccessPropertiesTab accessPropertiesTab = new AccessPropertiesTab(folder, propertiesChangeHandler);
					openNewTab(accessPropertiesTab, concept);
				}
			} else if (concept instanceof BusinessProcess) {
				ScriptsEditTab scriptsEditTab = new ScriptsEditTab(folder, propertiesChangeHandler);
				openNewTab(scriptsEditTab, concept);
			}
		}
	}
	
	private void openNewTab(HLTabWithConcept tab, IArchimateConcept concept) {
		if (!tabs.containsKey(concept.getId())) {
			tabs.put(concept.getId(),tab);
			tab.open(concept);
			tab.addCloseListener(id -> tabs.remove(id));
		} else {
			folder.setSelection(tabs.get(concept.getId()).getTab());
			tabs.get(concept.getId()).getTab().getControl().setFocus();
		}
	}
			
	private void initSelectionHandler() {
		selectionHandler = new HLSelectionHandler();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionHandler);
		selectionHandler.setSelectionConceptListener(this::archimateConceptSelectionHandler);
	}
	
	private void initPropertiesChangeHandler() {
		propertiesChangeHandler = new HLPropertiesChangeHandler();
	}
		
	@Override
	public void dispose() {
		if (selectionHandler != null) {
			getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(selectionHandler);
			selectionHandler.dispose();
		}
		if (propertiesChangeHandler != null) {
			propertiesChangeHandler.dispose();
		}
		tabs.values().stream().forEach(tab -> tab.dispose());
		tabs.clear();
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		//viewer.getControl().setFocus();
	}
}
