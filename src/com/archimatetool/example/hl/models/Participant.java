package com.archimatetool.example.hl.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

public class Participant extends HLModel {
	
	private final static int rank = 1;
	
	public Participant(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.PARTICIPANT, namespace, true);
	}

	@Override
	public int getRank() {
		return rank;
	}
	
}
