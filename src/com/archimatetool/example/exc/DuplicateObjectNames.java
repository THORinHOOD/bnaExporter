package com.archimatetool.example.exc;

public class DuplicateObjectNames extends IllegalArgumentException {
	
	public DuplicateObjectNames(String name) {
		super(makeMessage(name));
	}
	
	private static String makeMessage(String name) {
		return "Two objects has equal name : " + name;
	}
	
}
