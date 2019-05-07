package com.hyperledger.views.properties;

import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateFactory;

public abstract class HLTabWithConcept<T extends IArchimateConcept> extends HLTab<T> {

	protected T concept;
	protected Composite composite;
	
	public HLTabWithConcept(CTabFolder folder, String label) {
		super(folder, label);
	}

	@Override
	public void open(T concept) {
		this.concept = concept;
		initTab();
		openTab(composite);
	}

	@Override
	public void close() {
		closeTab();
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
		System.out.println(concept);
		if (concept != null) {
			Optional<IProperty> property = concept.getProperties().stream().filter(prop -> prop.getKey().equals(key)).findFirst();
			if (property.isPresent()) {
				property.get().setValue(value);
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
}
