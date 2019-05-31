package com.hyperledger.export.exceptions;

/**
 * Класс ошибки множественного наследования
 */
public class MultipleInheritanceException extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "Multiple inheritance : \"%s\"";
	
	public MultipleInheritanceException(String name) {
		super(String.format(msg, name));
	}
}
