package com.hyperledger.export.exceptions;

import java.util.List;

import com.hyperledger.export.models.HLModel;

/**
 * Класс ошибки зацикленного наследования
 */
public class CycleInheritanceException extends IllegalArgumentException {
	
	//Шаблон для создания сообщения ошибки
	private static final String msg = "Cycle inheritance : ";
	
	public CycleInheritanceException(List<HLModel> models) {
		super(makeMsg(models));
	}
	
	/**
	 * Статический метод для создания сообщения об ошибке
	 * @param models Модели, которые участвуют в наследовании
	 * @return Сообщение об ошибке
	 */
	private static String makeMsg(List<HLModel> models) {
		String res = msg;
		for (int i = 0; i < models.size() - 1; i++)
			res += models.get(i).getFullName() + ", ";
		res += models.get(models.size() - 1).getFullName();
		return res;
	}
}
