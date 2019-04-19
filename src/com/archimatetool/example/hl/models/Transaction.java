package com.archimatetool.example.hl.models;

import java.text.ParseException;

import com.archimatetool.model.IArchimateConcept;

public class Transaction extends HLModel {

	public Transaction(IArchimateConcept concept) throws ParseException {
		super(concept, HLModel.HLModelType.TRANSACTION, false);
	}
	
}