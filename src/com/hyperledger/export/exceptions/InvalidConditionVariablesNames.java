package com.hyperledger.export.exceptions;

/**
 * Класс ошибки некорректных имен переменных условия
 */
public class InvalidConditionVariablesNames extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "Invalid condition \"%s\" variables names : %s";
	
	public InvalidConditionVariablesNames(String condition, String... names) {
		super(String.format(msg, condition, String.join(", ", names)));
	}
}
