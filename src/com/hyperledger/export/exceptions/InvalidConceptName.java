package com.hyperledger.export.exceptions;

public class InvalidConceptName extends IllegalArgumentException {
	private static final String msg = "Concept (%s) has an invalid name (%s).";
	
	public InvalidConceptName(String typeOfConcept, String name) {
		super(String.format(msg, typeOfConcept, name));
	}
}
