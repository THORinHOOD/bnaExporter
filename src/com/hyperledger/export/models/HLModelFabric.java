package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.Optional;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;

/**
 * Фабрика для создания классов моделей по концепции
 */
public class HLModelFabric {
	
	/**
	 * Создание модели на основе концепции
	 * @param concept концепция
	 * @param namespace пространство имён
	 * @return модель
	 * @throws ParseException
	 */
	public static Optional<HLModel> createModel(IArchimateConcept concept, String namespace) throws ParseException {
		HLModel model = null;
		if (concept instanceof BusinessRole) {
			model = new Participant(concept, namespace);
		} else if (concept instanceof BusinessObject) {
			model = new Asset(concept, namespace);
		} else if (concept instanceof BusinessProcess) {
			model = new Transaction(concept, namespace);
		} 
		if (model != null) {
			return Optional.of(model);
		} else {
			return Optional.empty();
		}
	}
}
