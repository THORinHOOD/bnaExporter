package com.hyperledger.export.utils;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.impl.ArchimateConcept;
import com.archimatetool.model.impl.Property;

public class HLPropertiesChangeHandler {

	private Map<IArchimateConcept, List<Consumer<Boolean>>> changePropListeners;
	
	private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (evt.getNewValue() instanceof ENotificationImpl) {
				ENotificationImpl impl = (ENotificationImpl) evt.getNewValue();
				if (impl.getNotifier() instanceof IArchimateConcept) {
					propertyChanged((IArchimateConcept) impl.getNotifier(), false);	
				} else if (impl.getNotifier() instanceof Property) {
					Property prop = (Property) impl.getNotifier();
					if (prop.eContainer() instanceof ArchimateConcept) {
						propertyChanged((IArchimateConcept) prop.eContainer(), false);
					}
				} else if (impl.getNotifier() instanceof IFolder) {
					if (impl.getEventType() == ENotificationImpl.REMOVE) {
						if (impl.getOldValue() instanceof IArchimateConcept) {
							propertyChanged((IArchimateConcept) impl.getOldValue(), true);
						}
					}
				}
			}
		}
	};
	
	public HLPropertiesChangeHandler() {
		changePropListeners = new HashMap<>();
		IEditorModelManager.INSTANCE.addPropertyChangeListener(propertyChangeListener);
	}

	public void removedConcept(IArchimateConcept concept) {
		
	}
	
	public void propertyChanged(IArchimateConcept concept, boolean isRemoved) {
		if (changePropListeners.containsKey(concept)) {
			List<Consumer<Boolean>> conceptListeners = changePropListeners.get(concept);
			if (conceptListeners != null) {
				conceptListeners.stream().forEach(x -> x.accept(isRemoved));
			}
		}
	}
	
	public void removePropertyChangeListener(IArchimateConcept concept, Consumer<Boolean> listener) {
		if (changePropListeners.containsKey(concept)) {
			List<Consumer<Boolean>> conceptListeners = changePropListeners.get(concept);
			if (conceptListeners != null) {
				conceptListeners.remove(listener);
			}
		}
	}
	
	public void addPropertyChangeListener(IArchimateConcept concept, Consumer<Boolean> listener) {
		if (changePropListeners.containsKey(concept)) {
			List<Consumer<Boolean>> conceptListeners = changePropListeners.get(concept);
			if (conceptListeners != null) {
				conceptListeners.add(listener);
			}
		} else {
			changePropListeners.put(concept, new ArrayList<>());
			changePropListeners.get(concept).add(listener);
		}
	}
	
	public void dispose() {
		changePropListeners.keySet().stream().forEach(key -> changePropListeners.get(key).clear());
		changePropListeners.clear();
		IEditorModelManager.INSTANCE.removePropertyChangeListener(propertyChangeListener);
	}
}
