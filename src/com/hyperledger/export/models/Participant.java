package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.impl.AssignmentRelationship;
import com.archimatetool.model.impl.BusinessActor;

/**
 * Класс модели участник
 */
public class Participant extends HLModel {
	
	//Ранг модели для сортировки
	private final static int rank = 1;

	public Participant(IArchimateConcept concept, String namespace) throws ParseException {
		super(concept, HLModel.HLModelType.PARTICIPANT, namespace, true);
	}

	/**
	 * Получить ранг
	 */
	@Override
	public int getRank() {
		return rank;
	}
	
	/**
	 * Получить HyperLedger Modeling представление модели
	 */
	@Override
	public String getHLView() {
		String res = super.getHLView();
		
		if (!this.hasId) {
			
			HLModel current = this;
			while (current != null) {
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
