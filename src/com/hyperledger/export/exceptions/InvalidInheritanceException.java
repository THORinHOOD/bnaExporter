package com.hyperledger.export.exceptions;

/**
 * Класс ошибки некорректного наследования
 */
public class InvalidInheritanceException extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "Invalid inheritance : %s";
	
	public InvalidInheritanceException(String name) {
		super(String.format(msg, name));
	}
}
