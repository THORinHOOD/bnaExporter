package com.archimatetool.example.hl.models;

import java.text.ParseException;

import com.archimatetool.model.IArchimateConcept;

public class Transaction extends HLModel {

	private final static int rank = 3;
	
	public Transaction(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.TRANSACTION, namespace, false);
	}

	@Override
	public int getRank() {
		return rank;
	}
	
}
