package com.hyperledger.export.exceptions;

/**
 * Класс ошибки некорректного имени поля
 */
public class NotAppropriateFieldName extends IllegalArgumentException {
		
	public NotAppropriateFieldName(String object, String fieldName) {		
		super(makeMessage(object, fieldName));
	}
	
	/**
	 * Создать текст ошибки
	 * @param object имя модели
	 * @param fieldName имя поля
	 * @return текст ошибки
	 */
	private static String makeMessage(String object, String fieldName) {
		return object + " have field with not appropriate name : " + fieldName;
	}
	
}
