package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

public class Asset extends HLModel {
	
	public final static int rank = 2;
	
	public Asset(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.ASSET, namespace, true);
	}
	
	@Override
	public int getRank() {
		return rank;
	}
	
	@Override
	public String getHLView() {
		String res = super.getHLView();
		if (!hasId)
			res = "abstract " + res;
		return res;
	}
}
