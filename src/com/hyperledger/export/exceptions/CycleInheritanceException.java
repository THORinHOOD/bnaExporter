package com.hyperledger.export.exceptions;

import java.util.List;

import com.hyperledger.export.models.HLModel;

public class CycleInheritanceException extends IllegalArgumentException {
	private static final String msg = "Cycle inheritance : ";
	
	public CycleInheritanceException(List<HLModel> models) {
		super(makeMsg(models));
	}
	
	private static String makeMsg(List<HLModel> models) {
		String res = msg;
		for (int i = 0; i < models.size() - 1; i++)
			res += models.get(i).getFullName() + ", ";
		res += models.get(models.size() - 1).getFullName();
		return res;
	}
}
