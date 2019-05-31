package com.hyperledger.export.exceptions;

import com.hyperledger.export.models.HLField;
import com.hyperledger.export.models.HLModel;

/**
 * Класс ошибки некорректного типа идентифицирующего поля
 */
public class InvalidTypeOfIdField extends IllegalArgumentException {
	//Шаблон текста ошибки
	private static final String msg = "Model (%s) is identified by field (%s) but the type of the field is not String.";
	
	public InvalidTypeOfIdField(String modelName, String fieldName) {
		super(String.format(msg, modelName, fieldName));
	}
	
	public InvalidTypeOfIdField(HLModel model, HLField field) {
		this(model.getFullName(), field.getName());
	} 
}
