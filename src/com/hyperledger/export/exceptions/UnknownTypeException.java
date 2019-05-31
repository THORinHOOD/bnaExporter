package com.hyperledger.export.exceptions;

import com.hyperledger.export.models.HLField;
import com.hyperledger.export.models.HLModel;

/**
 * Класс ошибки неопредённого типа поля
 */
public class UnknownTypeException extends IllegalArgumentException {
	
	public UnknownTypeException(HLModel model, HLField field) {
		super(makeMessage(model.getName(), field.getType(), field.getName()));
	}
	
	/**
	 * Создать текст ошибки
	 * @param model имя модели
	 * @param fieldType тип поля
	 * @param fieldName имя поля
	 * @return текст ошибки
	 */
	private static String makeMessage(String model, String fieldType, String fieldName) {
		return "Object \"" + model + "\" have field \"" + fieldName + "\" with unknown type \"" + fieldType + "\"";
	}
	
}
