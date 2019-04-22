package com.archimatetool.example.hl.models;

import com.archimatetool.model.IArchimateConcept;

public abstract class HLObject {
	
	private IArchimateConcept concept;
	
	public HLObject(IArchimateConcept concept) {
		this.concept = concept;
	}
	
	public String getID() {
		return concept.getId();
	}
	
	public IArchimateConcept getConcept() {
		return concept;
	}
	
	public abstract String getHLView();
	
}

