package com.archimatetool.example.exc;

public class UnexpectedIdException extends IllegalArgumentException {
	private static final String msg = "Unexpected object id \"%s\"";
	
	public UnexpectedIdException(String name) {
		super(String.format(msg, name));
	}
}
