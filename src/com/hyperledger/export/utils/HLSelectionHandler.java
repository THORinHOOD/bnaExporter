package com.hyperledger.export.utils;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;

import com.archimatetool.model.IArchimateConcept;

public class HLSelectionHandler implements ISelectionListener {
	
	private IArchimateConcept selected;
	private BiConsumer selectionConceptsListener;
	
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
	
	private void handleArchimateConcept(IArchimateConcept concept) {
		if (concept == null) {
			selectedNewConceptEvent(false, concept);
			return;
		}
		selected = concept;
		selectedNewConceptEvent(true, selected);
	}
	
	public void setSelectionConceptListener(BiConsumer<Boolean, IArchimateConcept> listener) {
		selectionConceptsListener = listener;
	}

	public void selectedNewConceptEvent(Boolean selectedConcept, IArchimateConcept concept) {
		if (selectionConceptsListener != null)
			selectionConceptsListener.accept(selectedConcept, concept);
	}
	
	public void dispose() {
		selectionConceptsListener = null;
	}
	
}
