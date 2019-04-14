package com.archimatetool.example.hl;

import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

public class Asset extends HLModel {

	public Asset(IArchimateConcept concept) {
		super(concept, HLModel.HLModelType.ASSET);
	}
	
}
