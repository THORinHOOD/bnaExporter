package com.hyperledger.export.exceptions;

public class InvalidInheritanceException extends IllegalArgumentException {
	private static final String msg = "Invalid inheritance : %s";
	
	public InvalidInheritanceException(String name) {
		super(String.format(msg, name));
	}
}
