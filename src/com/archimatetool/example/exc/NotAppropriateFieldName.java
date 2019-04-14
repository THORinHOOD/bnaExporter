package com.archimatetool.example.exc;

public class NotAppropriateFieldName extends IllegalArgumentException {
		
	public NotAppropriateFieldName(String object, String fieldName) {		
		super(makeMessage(object, fieldName));
	}
	
	private static String makeMessage(String object, String fieldName) {
		return object + " have field with not appropriate name : " + fieldName;
	}
	
}
