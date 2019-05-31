package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

/**
 * Класс модели asset hyperledger composer
 */
public class Asset extends HLModel {
	//Ранг модели (для сортировки)
	public final static int rank = 2;
	
	public Asset(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.ASSET, namespace, true);
	}
	
	/**
	 * Получить ранг
	 */
	@Override
	public int getRank() {
		return rank;
	}
	
	/**
	 * Получить HL Modeling представление модели
	 */
	@Override
	public String getHLView() {
		String res = super.getHLView();
		if (!this.hasId) {
			
			HLModel current = this;
			while (current != null && current.isExtends()) {
				if (current.hasId) {
					this.hasId = true;
					break;
				} else {
					current = current.getSuperModel();
				}
			}
			
			if (!this.hasId)
				res = "abstract " + res;
		}
		return res;
	}
}
