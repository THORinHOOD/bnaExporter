package com.hyperledger.export.models;

import java.text.ParseException;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;

public class HLModelFabric {
	public static HLModel createModel(IArchimateConcept concept, String namespace) throws ParseException {
		if (concept instanceof BusinessRole) {
			return new Participant(concept, namespace);
		} else if (concept instanceof BusinessObject) {
			return new Asset(concept, namespace);
		} else if (concept instanceof BusinessProcess) {
			return new Transaction(concept, namespace);
		} else {
			throw new IllegalArgumentException("Unknown concept : " + concept.getClass());
		}
	}
}
