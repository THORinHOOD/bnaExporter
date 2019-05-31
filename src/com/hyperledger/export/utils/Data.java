package com.hyperledger.export.utils;

import java.util.HashMap;

/**
 * Дополнительные данные сети
 */
public class Data {
	// Индекс значения пространства имён
	public static final int NAMESPACE = 0;
	// Индекс значения имени сети
	public static final int BUSINESS_NETWORK_NAME = 1;
	// Индекс значения описания сети
	public static final int DESCRIPTION = 2;
	// Индекс значения имени автора
	public static final int AUTHOR_NAME = 3;
	// Индекс значения почты автора
	public static final int AUTHOR_EMAIL = 4;
	// Индекс значения лицензии
	public static final int LICENSE = 5;
	
	// Дополнительные данные сети
	private HashMap<Integer, Object> data;
	
	public Data() {
		data = new HashMap<Integer, Object>();
	}
	
	/**
	 * Добавить значение
	 * @param key ключ 
	 * @param value значение
	 * @return дополнительные данные
	 */
	public Data addValue(Integer key, Object value) {
		if (data.containsKey(key))
			throw new IllegalArgumentException("Data with key " + key.toString() + " already exist.");
		data.put(key, value);
		return this;
	}
	
	/**
	 * Обновить значение
	 * @param key
	 * @param value
	 * @return
	 */
	public Data updateValue(Integer key, Object value) {
		if (!data.containsKey(key))
			throw new IllegalArgumentException("Data with key " + key.toString() + " does not exist.");
		data.put(key, value);
		return this;
	}
	
	/**
	 * Получить значение
	 * @param key
	 * @return
	 */
	public <T> T getValue(Integer key) {
		return (T) data.get(key);
	}
	
	/**
	 * Получить строковое представление значения по ключу
	 * @param key
	 * @return
	 */
	public String getStringValue(Integer key) {
		if (!data.containsKey(key))
			throw new IllegalArgumentException("Data with key " + key.toString() + " does not exist.");
		return (String) data.get(key);
	}

}
