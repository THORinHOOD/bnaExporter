package com.archimatetool.example.exc;

public class NotCorrectField extends IllegalArgumentException {
	
	private static final String msg = "The model \"%s\" has an invalid field \"%s\"";
	
	public NotCorrectField(String model, String field) {
		super(String.format(msg, model, field));
	}
	
}
