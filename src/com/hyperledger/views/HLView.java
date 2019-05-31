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

/**
 * Окно расширения с вкладками
 */
public class HLView extends ViewPart {

	//ID
	public static final String ID = "com.hyperledger.views.properties.PropertiesView";

	@Inject IWorkbench workbench;
	
	// Папка с вкладками
	private CTabFolder folder;
	// Обрабтчик выбора
	private HLSelectionHandler selectionHandler;
	// Обработчик измнений концепций
	private HLPropertiesChangeHandler propertiesChangeHandler;
	// Вкладки
	private Map<String, HLTabWithConcept> tabs;

	/**
	 * Создание окна
	 */
	@Override
	public void createPartControl(Composite parent) {
		tabs = new HashMap<>();
		folder = new CTabFolder(parent, SWT.NONE);
		initSelectionHandler();
		initPropertiesChangeHandler();		
	}
	
	/**
	 * Обработка выбора концепции
	 * @param isConcept
	 * @param concept
	 */
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
	
	/**
	 * Открытие новой вкладки
	 * @param tab
	 * @param concept
	 */
	private void openNewTab(HLTabWithConcept tab, IArchimateConcept concept) {
		if (!tabs.containsKey(concept.getId())) {
			tabs.put(concept.getId(),tab);
			tab.open(concept);
			tab.addCloseListener(id -> tabs.remove(id));
		} else {
			folder.setSelection(tabs.get(concept.getId()).getTab());
		}
	}
			
	/**
	 * Инициализация обработчика выбора
	 */
	private void initSelectionHandler() {
		selectionHandler = new HLSelectionHandler();
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(selectionHandler);
		selectionHandler.setSelectionConceptListener(this::archimateConceptSelectionHandler);
	}
	
	/**
	 * Инициализация обработчика изменений концепций
	 */
	private void initPropertiesChangeHandler() {
		propertiesChangeHandler = new HLPropertiesChangeHandler();
	}
		
	/**
	 * Удалание окна и очищение ресурсов
	 */
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
	
	/**
	 * Установка фокуса
	 */
	@Override
	public void setFocus() {
		//viewer.getControl().setFocus();
	}
}
