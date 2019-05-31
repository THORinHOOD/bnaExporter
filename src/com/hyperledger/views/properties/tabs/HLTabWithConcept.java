package com.hyperledger.views.properties.tabs;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateFactory;
import com.hyperledger.export.utils.HLPropertiesChangeHandler;

/**
 * Класс предок вкладки с концепцией
 */
public abstract class HLTabWithConcept<T extends IArchimateConcept> extends HLTab {

	// Концепция
	protected T concept;
	// Контейнер
	protected Composite composite;
	// Обработчик изменений концепции
	protected HLPropertiesChangeHandler propertiesChangeHandler;
	
	public HLTabWithConcept(CTabFolder folder, HLPropertiesChangeHandler propertyChangeListener, String label) {
		super(folder, label);
		this.propertiesChangeHandler = propertyChangeListener;
	}

	/**
	 * Открытие вкладки
	 * @param concept
	 */
	public void open(T concept) {
		this.concept = concept;		
		initTab();
		openTab(composite);
		propertiesChangeHandler.addPropertyChangeListener(this.concept, this::onConceptChanging);
		getTab().addDisposeListener(e -> propertiesChangeHandler.removePropertyChangeListener(this.concept, this::onConceptChanging));
	}

	/**
	 * Закрытие вкладки
	 */
	public void close() {
		closeTab();
	}
		
	/**
	 * Получить свойство по ключу
	 * @param key
	 * @return
	 */
	protected String getProperty(String key) {
		if (concept != null) {
			Optional<IProperty> property = concept.getProperties().stream().filter(prop -> prop.getKey().equals(key)).findFirst();
			if (property.isPresent()) {
				return property.get().getValue();
			}
		}
		return null;
	}
	
	/**
	 * Получить свойство по ключу, есил его нет, тогда вернуть по умолчанию и сохранить его
	 * @param key
	 * @param defaultValue
	 * @return
	 */
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
	
	/**
	 * Удалить свойство
	 * @param key
	 */
	protected void removeProperty(String key) {
		if (concept != null) {
			int was = concept.getProperties().size();
			List<IProperty> properties = concept.getProperties().stream().filter(prop -> prop.getKey().equals(key)).collect(Collectors.toList());
			concept.getProperties().removeAll(properties);
			if (concept.getProperties().size() - was != 0)
				save();
		}
	}
	
	/**
	 * Установить свойтсво
	 * @param key
	 * @param value
	 */
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
		
	/**
	 * Сохранить модель
	 */
	private void save() {
		try {
			concept.getArchimateModel().eResource().save(null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Установить обработчик закрытия вкладки
	 * @param onClose
	 */
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
	
	/**
	 * Инициализция вкладки
	 */
	protected abstract void initTab();
	/**
	 * При измнении концепции
	 * @param isRemoved
	 */
	protected abstract void onConceptChanging(boolean isRemoved);
}
