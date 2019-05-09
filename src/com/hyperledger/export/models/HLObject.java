package com.hyperledger.export.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;

public abstract class HLObject {
	
	private List<IArchimateConcept> concepts;
	private String id;
	
	private HLObject() {
		concepts = new ArrayList<IArchimateConcept>();
	}
	
	public HLObject(HLObject object) {
		this();
		concepts.addAll(object.getConcepts());
		id = object.getID();
	}
	
	public HLObject(IArchimateConcept concept) {
		this();
		concepts.add(concept);
		id = concept.getId();
	}

	public HLObject addConcept(IArchimateConcept concept) {
		concepts.add(concept);
		return this;
	}
	
	public HLObject addConcepts(Collection concepts) {
		this.concepts.addAll(concepts);
		return this;
	}
	
	public String getID() {
		return id;
	}
	
	public List<IArchimateConcept> getConcepts() {
		return concepts;
	}
	
	public abstract String getHLView();
	
}

