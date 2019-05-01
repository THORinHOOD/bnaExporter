package com.hyperledger.export.exceptions;

public class DuplicateObjectNames extends IllegalArgumentException {
	
	public DuplicateObjectNames(String name) {
		super(makeMessage(name));
	}
	
	private static String makeMessage(String name) {
		return "Two objects has equal name : " + name;
	}
	
}
