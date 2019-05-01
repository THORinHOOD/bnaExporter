package com.hyperledger.export.models;

import java.text.ParseException;

import com.archimatetool.model.IArchimateConcept;
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
	public int getRank() {
		return rank;
	}
	
}
