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
import com.archimatetool.model.impl.AccessRelationship;
import com.archimatetool.model.impl.ArchimateRelationship;
import com.hyperledger.export.rules.HLPermRule;
import com.hyperledger.export.utils.HLPropertiesChangeHandler;
import com.hyperledger.export.utils.HLSelectionHandler;
import com.hyperledger.views.properties.tabs.AccessPropertiesTab;
import com.hyperledger.views.properties.tabs.HLTabWithConcept;

public class HLView extends ViewPart {

	public static final String ID = "com.hyperledger.views.properties.PropertiesView";

	@Inject IWorkbench workbench;
	
	private Action action1;
	private Action action2;
	private Action doubleClickAction;

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
		//accessPropsTab = new AccessPropertiesTab(folder);
		
//	    viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);	
//	    
//		Table table = viewer.getTable();
//
//		TableColumn column = new TableColumn(table, SWT.LEFT);
//		column.setText("Column 1");
//		column.setWidth(100);
//
//		TableColumn column2 = new TableColumn(table, SWT.LEFT);
//		column2.setText("Column 2");
//		column2.setWidth(100);
//
//		table.setHeaderVisible(true);		
	}
	
	private void archimateConceptSelectionHandler(Boolean isConcept, IArchimateConcept concept) {
		if (isConcept) {
			if (concept instanceof ArchimateRelationship) {
				ArchimateRelationship relation = (ArchimateRelationship) concept;
				if (HLPermRule.isHLAccessRelation(relation)) {
					openAccessRelationshipTab(relation);
				}
			}
		}
	}
	
	private void openAccessRelationshipTab(ArchimateRelationship relation) {
		if (!tabs.containsKey(relation.getId())) {
			AccessPropertiesTab accessPropertiesTab = new AccessPropertiesTab(folder, propertiesChangeHandler);
			tabs.put(relation.getId(), accessPropertiesTab);
			accessPropertiesTab.open(relation);
			accessPropertiesTab.addCloseListener(id -> tabs.remove(id));
		} else {
			folder.setSelection(tabs.get(relation.getId()).getTab());
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
		super.dispose();
	}
	
//	private void hookContextMenu() {
//		MenuManager menuMgr = new MenuManager("#PopupMenu");
//		menuMgr.setRemoveAllWhenShown(true);
//		menuMgr.addMenuListener(new IMenuListener() {
//			public void menuAboutToShow(IMenuManager manager) {
//				PropertiesView.this.fillContextMenu(manager);
//			}
//		});
//		Menu menu = menuMgr.createContextMenu(viewer.getControl());
//		viewer.getControl().setMenu(menu);
//		getSite().registerContextMenu(menuMgr, viewer);
//	}

//	private void contributeToActionBars() {
//		IActionBars bars = getViewSite().getActionBars();
//		fillLocalPullDown(bars.getMenuManager());
//		fillLocalToolBar(bars.getToolBarManager());
//	}

//	private void fillLocalPullDown(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(new Separator());
//		manager.add(action2);
//	}
//
//	private void fillContextMenu(IMenuManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//		// Other plug-ins can contribute there actions here
//		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
//	}
	
//	private void fillLocalToolBar(IToolBarManager manager) {
//		manager.add(action1);
//		manager.add(action2);
//	}

//	private void makeActions() {
//		action1 = new Action() {
//			public void run() {
//				showMessage("Action 1 executed");
//			}
//		};
//		action1.setText("Action 1");
//		action1.setToolTipText("Action 1 tooltip");
//		action1.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().
//			getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//		
//		action2 = new Action() {
//			public void run() {
//				showMessage("Action 2 executed");
//			}
//		};
//		action2.setText("Action 2");
//		action2.setToolTipText("Action 2 tooltip");
//		action2.setImageDescriptor(workbench.getSharedImages().
//				getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
//		doubleClickAction = new Action() {
//			public void run() {
//				IStructuredSelection selection = viewer.getStructuredSelection();
//				Object obj = selection.getFirstElement();
//				showMessage("Double-click detected on "+obj.toString());
//			}
//		};
//	}

//	private void hookDoubleClickAction() {
//		viewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
//	}
//	private void showMessage(String message) {
//		MessageDialog.openInformation(
//			viewer.getControl().getShell(),
//			"Hyperledger Properties ",
//			message);
//	}

	@Override
	public void setFocus() {
		//viewer.getControl().setFocus();
	}
}
