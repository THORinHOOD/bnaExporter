package com.archimatetool.example.hl.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

public class Asset extends HLModel {

	public Asset(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.ASSET, namespace, true);
	}
	
}
