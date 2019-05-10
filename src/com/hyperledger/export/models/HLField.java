package com.hyperledger.export.models;

import java.util.Arrays;

import com.archimatetool.model.IProperty;
import com.hyperledger.export.exceptions.NotAppropriateFieldName;
import com.hyperledger.export.exceptions.NotCorrectField;

public class HLField {
	
	public enum Type {
		REFER, PROPERTY
	}
	
	private Type relation;
	private String type;
	private String name;
	private boolean identifiedByThis = false;
	
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

	public HLField copy(HLModel model) {
		return createField(model, type, name, relation);
	}
	
	public static HLField createField(HLModel model, IProperty prop, Type relation) {
		return createField(model, prop.getKey(), prop.getValue(), relation);
	}
	
	public static HLField createField(HLModel model, String type, String name, Type relation) {
		HLField field = new HLField(type, name, relation);		
		
		if (!field.correctField())
			throw new NotCorrectField(model.getName(), name);
		
		return field;
	}
	
	public boolean correctField() {
		return !(Character.isDigit(getName().charAt(0)) || getName().charAt(0) == '@' || !onlyCorrectSymbols(getName()));
	}
	
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
		
	public String getType() {
		return type;
	}
	
	public String getName() {
		return name;
	}
	
	public boolean isIdentifiedByThis() {
		return identifiedByThis;
	}
	
	public boolean isPrimitive() {
		return Arrays.asList(primitiveTypes).contains(type);
	}
	
	public void setRelation(Type relation) {
		this.relation = relation;
	}
	
	public Type getRelation() {
		return relation;
	}
	
	
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
