package com.hyperledger.export.exceptions;

public class InvalidConditionVariablesNames extends IllegalArgumentException {
	private static final String msg = "Invalid condition \"%s\" variables names : %s";
	
	public InvalidConditionVariablesNames(String condition, String... names) {
		super(String.format(msg, condition, String.join(", ", names)));
	}
}
