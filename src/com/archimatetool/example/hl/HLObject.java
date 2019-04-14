package com.archimatetool.example.hl;

import java.text.ParseException;

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

