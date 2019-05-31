package com.hyperledger.export.exceptions;

/**
 * Класс ошибки повторяющихся имён
 */
public class DuplicateObjectNames extends IllegalArgumentException {
	
	public DuplicateObjectNames(String name) {
		super(makeMessage(name));
	}
	
	/**
	 * Создать сообщение ошибки
	 * @param name имя, которое повторяется
	 * @return сообщение ошибки
	 */
	private static String makeMessage(String name) {
		return "Two objects has equal name : " + name;
	}
	
}
