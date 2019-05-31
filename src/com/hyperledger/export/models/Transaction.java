package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.AccessRelationship;

/**
 * Класс модели транзакции
 */
public class Transaction extends HLModel {

	//Ранг модели для сортировки
	private final static int rank = 3;
	
	public Transaction(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.TRANSACTION, namespace, false);
		
		concept.getSourceRelationships()
			.stream()
			.filter(x -> x instanceof AccessRelationship)
			.map(x -> (AccessRelationship) x)
			.filter(x -> x.getAccessType() == AccessRelationship.READ_ACCESS)
			.forEach(relation -> {
				List<IProperty> fields = relation.getProperties()
													.stream()
													.filter(prop -> prop.getKey().trim().toUpperCase().equals("REFER"))
													.collect(Collectors.toList());
				if (fields.size() > 0) {
					fields.stream().forEach(field -> this.fields.add(HLField.createField(this, relation.getTarget().getName(),field.getValue(), HLField.Type.REFER)));
				} else {
					this.fields.add(HLField.createField(this, relation.getTarget().getName(), relation.getTarget().getName().toLowerCase(), HLField.Type.REFER));
				}
			});
	}

	/**
	 * Установить поля для транзакции с учетом игнорирования некоторых полей
	 */
	@Override
	protected void setFields(IArchimateConcept concept) throws ParseException {
		fields = new ArrayList<HLField>();
		for (IProperty prop : concept.getProperties())
			if (!prop.getKey().trim().toUpperCase().equals("SCRIPT"))
				fields.add(HLField.createField(this, prop, HLField.Type.PROPERTY));
	}
	
	/**
	 * Получить ранг модели   
	 */
	@Override
	public int getRank() {
		return rank;
	}
	
}
