package com.hyperledger.export.exceptions;

import com.hyperledger.export.models.HLField;
import com.hyperledger.export.models.HLModel;

public class UnknownTypeException extends IllegalArgumentException {
	
	public UnknownTypeException(HLModel model, HLField field) {
		super(makeMessage(model.getName(), field.getType(), field.getName()));
	}
	
	private static String makeMessage(String model, String fieldType, String fieldName) {
		return "Object \"" + model + "\" have field \"" + fieldName + "\" with unknown type \"" + fieldType + "\"";
	}
	
}
