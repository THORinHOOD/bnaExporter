package com.hyperledger.export.exceptions;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

/**
 * Класс ошибки некорректного значения свойства концепции
 */
public class InvalidPropertyValue extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "Invalid concept (%s) property (%s) value (%s);\n Corrent values: %s";
	
	public InvalidPropertyValue(String concept, String property, String value, String... correctValues) {
		super(String.format(msg, concept, property, value, String.join(", ", correctValues)));
	}
}
