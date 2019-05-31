package com.hyperledger.export.exceptions;

/**
 * Класс ошибки непредвиденного идентифицирующего поля
 */
public class UnexpectedIdException extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "Unexpected object id \"%s\"";
	
	public UnexpectedIdException(String name) {
		super(String.format(msg, name));
	}
}
