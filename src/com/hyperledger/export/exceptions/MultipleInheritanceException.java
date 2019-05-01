package com.hyperledger.export.exceptions;

public class MultipleInheritanceException extends IllegalArgumentException {
	private static final String msg = "Multiple inheritance : \"%s\"";
	
	public MultipleInheritanceException(String name) {
		super(String.format(msg, name));
	}
}
