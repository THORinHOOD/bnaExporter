package com.archimatetool.example.exc;

public class MultipleInheritanceException extends IllegalArgumentException {
	private static final String msg = "Multiple inheritance : \"%s\"";
	
	public MultipleInheritanceException(String name) {
		super(String.format(msg, name));
	}
}
