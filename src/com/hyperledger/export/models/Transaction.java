package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.ArrayList;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.hyperledger.export.models.HLField.Type;

public class Transaction extends HLModel {

	private final static int rank = 3;
	
	public Transaction(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.TRANSACTION, namespace, false);
		
		getFields()
			.stream()
			.forEach(prop -> prop.setRelation(Type.REFER));
	}

	@Override
	protected void setFields(IArchimateConcept concept) throws ParseException {
		fields = new ArrayList<HLField>();
		for (IProperty prop : concept.getProperties())
			if (!prop.getKey().trim().toUpperCase().equals("SCRIPT"))
				fields.add(HLField.createField(this, prop, HLField.Type.PROPERTY));
	}
	
	@Override
	public int getRank() {
		return rank;
	}
	
}
