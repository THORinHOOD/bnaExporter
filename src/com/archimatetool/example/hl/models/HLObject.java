package com.archimatetool.example.hl.models;

public abstract class HLObject {
	
	private String ID;
	
	public HLObject(String ID) {
		this.ID = ID;
	}
	
	public String getID() {
		return ID;
	}
	
	public abstract String getHLView();
	
}

