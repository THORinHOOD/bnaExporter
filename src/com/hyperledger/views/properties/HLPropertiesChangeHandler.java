package com.hyperledger.views.properties;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.impl.ENotificationImpl;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.impl.ArchimateConcept;
import com.archimatetool.model.impl.Property;

public class HLPropertiesChangeHandler {

	private Map<IArchimateConcept, List<Runnable>> listeners;
	
	private final PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			
			if (evt.getNewValue() instanceof ENotificationImpl) {
				ENotificationImpl impl = (ENotificationImpl) evt.getNewValue();
				if (impl.getNotifier() instanceof IArchimateConcept) {
					propertyChanged((IArchimateConcept) impl.getNotifier());	
				} else if (impl.getNotifier() instanceof Property) {
					Property prop = (Property) impl.getNotifier();
					if (prop.eContainer() instanceof ArchimateConcept) {
						propertyChanged((ArchimateConcept) prop.eContainer());
					}
				}
			}
		}
	};
	
	public HLPropertiesChangeHandler() {
		listeners = new HashMap<>();
		IEditorModelManager.INSTANCE.addPropertyChangeListener(propertyChangeListener);
	}

	public void propertyChanged(IArchimateConcept concept) {
		if (listeners.containsKey(concept)) {
			List<Runnable> conceptListeners = listeners.get(concept);
			if (conceptListeners != null) {
				conceptListeners.stream().forEach(x -> x.run());
			}
		}
	}
	
	public void removePropertyChangeListener(IArchimateConcept concept, Runnable listener) {
		if (listeners.containsKey(concept)) {
			List<Runnable> conceptListeners = listeners.get(concept);
			if (conceptListeners != null) {
				conceptListeners.remove(listener);
			}
		}
	}
	
	public void addPropertyChangeListener(IArchimateConcept concept, Runnable listener) {
		if (listeners.containsKey(concept)) {
			List<Runnable> conceptListeners = listeners.get(concept);
			if (conceptListeners != null) {
				conceptListeners.add(listener);
			}
		} else {
			listeners.put(concept, new ArrayList<>());
			listeners.get(concept).add(listener);
		}
	}
	
	public void dispose() {
		listeners.keySet().stream().forEach(key -> listeners.get(key).clear());
		listeners.clear();
		IEditorModelManager.INSTANCE.removePropertyChangeListener(propertyChangeListener);
	}
}
