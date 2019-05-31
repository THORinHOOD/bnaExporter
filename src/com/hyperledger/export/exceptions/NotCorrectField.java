package com.hyperledger.export.exceptions;

/**
 * Класс ошибки некорректного поля
 */
public class NotCorrectField extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "The model \"%s\" has an invalid field \"%s\"";
	
	public NotCorrectField(String model, String field) {
		super(String.format(msg, model, field));
	}
	
}
