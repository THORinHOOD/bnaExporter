package com.archimatetool.example.utils;

import java.util.HashMap;

public class Data {
	
	public static final int NAMESPACE = 0;
	public static final int BUSINESS_NETWORK_NAME = 1;
	public static final int DESCRIPTION = 2;
	public static final int AUTHOR_NAME = 3;
	public static final int AUTHOR_EMAIL = 4;
	public static final int LICENSE = 5;
	
	private HashMap<Integer, Object> data;
	
	public Data() {
		data = new HashMap<Integer, Object>();
	}
	
	public Data addValue(Integer key, Object value) {
		if (data.containsKey(key))
			throw new IllegalArgumentException("Data with key " + key.toString() + " already exist.");
		data.put(key, value);
		return this;
	}
	
	public Data updateValue(Integer key, Object value) {
		if (!data.containsKey(key))
			throw new IllegalArgumentException("Data with key " + key.toString() + " does not exist.");
		data.put(key, value);
		return this;
	}
	
	public <T> T getValue(Integer key) {
		return (T) data.get(key);
	}
		
	public String getStringValue(Integer key) {
		if (!data.containsKey(key))
			throw new IllegalArgumentException("Data with key " + key.toString() + " does not exist.");
		return (String) data.get(key);
	}

}
