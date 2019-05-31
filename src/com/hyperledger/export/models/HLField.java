package com.hyperledger.export.models;

import java.util.Arrays;

import com.archimatetool.model.IProperty;
import com.hyperledger.export.exceptions.NotAppropriateFieldName;
import com.hyperledger.export.exceptions.NotCorrectField;

/**
 * HyperLedger поле модели
 */
public class HLField {
	
	public enum Type {
		REFER, PROPERTY
	}
	
	//Тип отношения
	private Type relation;
	//Тип поля
	private String type;
	//Имя поля
	private String name;
	//Идентифицирующее поле
	private boolean identifiedByThis = false;
	
	//Примитивыне типы
	public static final String[] primitiveTypes = {
			"String",
			"Double",
			"Integer",
			"Long",
			"DateTime",
			"Boolean",
			"String[]",
			"Double[]",
			"Integer[]",
			"Long[]",
			"DateTime[]",
			"Boolean[]"
	};
	
	/**
	 * Получить копию поля
	 * @param model модель, для которой требуется копия
	 * @return поле
	 */
	public HLField copy(HLModel model) {
		return createField(model, type, name, relation);
	}
	
	/**
	 * Создать поле
	 * @param model модель
	 * @param prop свойство концепции
	 * @param relation тип отношения
	 * @return поле
	 */
	public static HLField createField(HLModel model, IProperty prop, Type relation) {
		return createField(model, prop.getKey(), prop.getValue(), relation);
	}
	
	/**
	 * Создать поле
	 * @param model модель
	 * @param type тип поля
	 * @param name имя поля
	 * @param relation тип отншения
	 * @return поле
	 */
	public static HLField createField(HLModel model, String type, String name, Type relation) {
		HLField field = new HLField(type, name, relation);		
		
		if (!field.correctField())
			throw new NotCorrectField(model.getName(), name);
		
		return field;
	}
	
	/**
	 * Проверка корректности имени поля
	 * @return корректность
	 */
	public boolean correctField() {
		return !(Character.isDigit(getName().charAt(0)) || getName().charAt(0) == '@' || !onlyCorrectSymbols(getName()));
	}
	
	/**
	 * Только корректные символы в строке
	 * @param line строка
	 * @return корректность
	 */
	private static boolean onlyCorrectSymbols(String line) {
		return line.matches("^[a-zA-Z0-9_$@]+$");
	}
	
	private HLField(String type, String name, Type relation) {
		
		this.relation = relation;
		this.type = type.replaceAll(" ", "");
		this.name = name.replaceAll(" ", "");
		
		if (this.name.contains(":id") || this.type.contains(":id")) {
			identifiedByThis = true;
			this.name = this.name.replaceAll(":id", "");
			this.type = this.type.replaceAll(":id", "");
			if (!this.type.equals("String")) {
				throw new IllegalArgumentException("ID property must be of type \"String\"");
			}
		}

	}
	
	/**
	 * Получить тип поля
	 * @return тип поля
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * Получить имя поля
	 * @return имя поля
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Идентифицирующее поле
	 * @return идентифицирующее ли поле
	 */
	public boolean isIdentifiedByThis() {
		return identifiedByThis;
	}
	
	/**
	 * Примитвный ли тип
	 * @return примитвный ли тип
	 */
	public boolean isPrimitive() {
		return Arrays.asList(primitiveTypes).contains(type);
	}
	
	/**
	 * Установить отношения
	 * @param relation отношение
	 */
	public void setRelation(Type relation) {
		this.relation = relation;
	}
	
	/**
	 * Получить отношение
	 * @return отношение
	 */
	public Type getRelation() {
		return relation;
	}
	
	/**
	 * Строковое представление поля
	 */
	@Override
	public String toString() {
		String result = type + " " + name;
		
		if (relation == Type.PROPERTY)
			result = "o " + result;
		else
			result = "--> " + result;
		
		return result;
	}	
}
