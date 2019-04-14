package com.archimatetool.example.hl;

import com.archimatetool.model.IArchimateConcept;

public class Transaction extends HLModel {

	public Transaction(IArchimateConcept concept) {
		super(concept, HLModel.HLModelType.TRANSACTION);
	}

}
