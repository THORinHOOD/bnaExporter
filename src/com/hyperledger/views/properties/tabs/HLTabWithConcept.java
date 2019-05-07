package com.hyperledger.views.properties.tabs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.archimatetool.editor.model.IEditorModelManager;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateFactory;
import com.hyperledger.views.properties.HLPropertiesChangeHandler;

public abstract class HLTabWithConcept<T extends IArchimateConcept> extends HLTab {

	protected T concept;
	protected Composite composite;
	private HLPropertiesChangeHandler propertiesChangeHandler;
	
	public HLTabWithConcept(CTabFolder folder, HLPropertiesChangeHandler propertyChangeListener, String label) {
		super(folder, label);
		this.propertiesChangeHandler = propertyChangeListener;
	}

	public void open(T concept) {
		this.concept = concept;		
		initTab();
		openTab(composite);
		propertiesChangeHandler.addPropertyChangeListener(this.concept, this::onConceptChanging);
		getTab().addDisposeListener(e -> propertiesChangeHandler.removePropertyChangeListener(this.concept, this::onConceptChanging));
	}

	public void close() {
		closeTab();
	}
		
	protected String getProperty(String key) {
		if (concept != null) {
			Optional<IProperty> property = concept.getProperties().stream().filter(prop -> prop.getKey().equals(key)).findFirst();
			if (property.isPresent()) {
				return property.get().getValue();
			}
		}
		return null;
	}
	
	protected String getProperty(String key, String defaultValue) {
		if (concept != null) {
			Optional<IProperty> property = concept.getProperties().stream().filter(prop -> prop.getKey().equals(key)).findFirst();
			if (property.isPresent()) {
				return property.get().getValue();
			} else {
				IProperty prop = ArchimateFactory.init().createProperty();
				prop.setKey(key);
				prop.setValue(defaultValue);
				save();
				return defaultValue;
			}
		} else {
			return defaultValue;
		}
	}
	
	protected void setProperty(String key, String value) {
		if (concept != null) {
			Optional<IProperty> property = concept.getProperties().stream().filter(prop -> prop.getKey().equals(key)).findFirst();
			if (property.isPresent()) {
				if (!property.get().getValue().equals(value)) {
					property.get().setValue(value);
				}
			} else {
				IProperty prop = ArchimateFactory.init().createProperty();
				prop.setKey(key);
				prop.setValue(value);
				concept.getProperties().add(prop);
			}
			save();
		}
	}
		
	private void save() {
		try {
			concept.getArchimateModel().eResource().save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void addCloseListener(Consumer<String> onClose) {
		getTab().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				if (concept != null) {
					onClose.accept(concept.getId());
				}
			}
		});
	}
	
	protected abstract void initTab();
	protected abstract void onConceptChanging();
}
