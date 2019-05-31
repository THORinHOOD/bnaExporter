package com.hyperledger.export.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.archimatetool.model.IArchimateConcept;

/**
 * Обработчик выбора
 */
public class HLSelectionHandler implements ISelectionListener {
	
	// Выбранная концепция
	private IArchimateConcept selected;
	// Обработчик выбора концепции
	private BiConsumer selectionConceptsListener;
	
	/**
	 * Изменения выбора концепции
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (selection == null)
			return;
		
		if(selection instanceof IStructuredSelection && !selection.isEmpty()) {
		    Object object = ((IStructuredSelection) selection).getFirstElement();
		    if (object == null)
		    	return;
		    
		    if(object instanceof IArchimateConcept) {
		    	handleArchimateConcept((IArchimateConcept) object);
		    } else if (object instanceof IAdaptable) {
		    	handleArchimateConcept(((IAdaptable)object).getAdapter(IArchimateConcept.class));
		    }
		}
	}
	
	/**
	 * Установка текущей выбранной концепции
	 * @param concept
	 */
	private void handleArchimateConcept(IArchimateConcept concept) {
		if (concept == null) {
			selectedNewConceptEvent(false, concept);
			return;
		}
		selected = concept;
		selectedNewConceptEvent(true, selected);
	}
	
	/**
	 * Установка обработчика
	 * @param listener
	 */
	public void setSelectionConceptListener(BiConsumer<Boolean, IArchimateConcept> listener) {
		selectionConceptsListener = listener;
	}

	/**
	 * Событие выбора другой концепции
	 * @param selectedConcept
	 * @param concept
	 */
	public void selectedNewConceptEvent(Boolean selectedConcept, IArchimateConcept concept) {
		if (selectionConceptsListener != null)
			selectionConceptsListener.accept(selectedConcept, concept);
	}
	
	/**
	 * Удаление обработчиков
	 */
	public void dispose() {
		selectionConceptsListener = null;
	}
	
}
