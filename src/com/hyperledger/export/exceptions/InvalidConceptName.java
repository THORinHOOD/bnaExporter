package com.hyperledger.export.exceptions;

/**
 * Класс ошибки некорректного имени концепции
 */
public class InvalidConceptName extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "Concept (%s) has an invalid name (%s).";
	
	public InvalidConceptName(String typeOfConcept, String name) {
		super(String.format(msg, typeOfConcept, name));
	}
}
