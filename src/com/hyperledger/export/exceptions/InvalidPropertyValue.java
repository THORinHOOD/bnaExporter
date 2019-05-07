package com.hyperledger.export.exceptions;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

public class InvalidPropertyValue extends IllegalArgumentException {
	private static final String msg = "Invalid concept (%s) property (%s) value (%s);\n Corrent values: %s";
	
	public InvalidPropertyValue(String concept, String property, String value, String... correctValues) {
		super(String.format(msg, concept, property, value, String.join(", ", correctValues)));
	}
}
