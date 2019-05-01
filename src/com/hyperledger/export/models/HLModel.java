package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.hyperledger.export.exceptions.InvalidTypeOfIdField;
import com.hyperledger.export.exceptions.MultipleInheritanceException;

public abstract class HLModel extends HLObject {
	
	public enum HLModelType {
		PARTICIPANT, ASSET, TRANSACTION;
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}
	
	private final String HEADER = "%s %s";
	private final String IDENTIFIED_BY = "identified by %s";
	private final String EXTENDS = "extends %s";
	private final String CONTENT = "{\n%s}";
	
	protected HLModelType type;
	
	protected String documentation;
	protected String name;
	protected String namespace;
	protected List<HLField> fields;
		
	protected boolean identified;
	protected boolean hasId = false;
	protected HLField idField = null;
	
	protected HLModel superModel;
	protected boolean extendsModel = false;
	
	public HLModel(IArchimateConcept concept, HLModelType type, String namespace, boolean identified) throws ParseException {
		super(concept);
		
		this.namespace = namespace;
		this.type = type;
		this.identified = identified;
		name = concept.getName();
		
		fields = new ArrayList<HLField>();
		for (IProperty prop : concept.getProperties())
			fields.add(HLField.createField(this, prop, HLField.Type.PROPERTY));
		
		if (this.identified)
			findIdProp();
				
		documentation = concept.getDocumentation();
		
	}
	
	protected void findIdProp() throws ParseException {
		for (int i = 0; i < fields.size(); i++) {
			HLField field = fields.get(i);
			if (field.isIdentifiedByThis()) {
				if (idField == null) {
					
					if (!field.getType().equals("String")) {
						throw new InvalidTypeOfIdField(this, field);
					}
					
					idField = field;
					hasId = true;
					
				} else {
					throw new ParseException("Id field should be one : " + name, i);
				}
			}
		}
		
		if (idField == null)
			hasId = false;
	}
	
	public String getName() {
		return name;
	}
	
	public String getFullName() {
		return namespace + "." + name;
	}
	
	public List<HLField> getFields() {
		return fields;
	}
	
	public void addField(HLField field) {
		fields.add(field);
	}
	
	@Override
	public String getHLView() {
		String name = getName();
		String fields = "";
	
		for (HLField field : this.fields) {
			fields += "\t" + field.toString() + "\n";
		}
		
		String result = String.format(HEADER, type.toString(), name);
		if (hasId)
			result += " " + String.format(IDENTIFIED_BY, idField.getName());
		if (extendsModel)
			result += " " + String.format(EXTENDS, superModel.getName());
		result += "\n" + String.format(CONTENT, fields);
		
		if ((documentation != null) && (!documentation.trim().equals(""))) {
			String hlDocumentation = "/**\n";
			String[] lines = documentation.split("\n");
			for (String line : lines)
				hlDocumentation += " * " + line + "\n";
			hlDocumentation += " */\n";
			result = hlDocumentation + result;
		}
		
		return result;
	}
	
	public boolean hasId() {
		return hasId;
	}
	
	public void setSuperModel(HLModel model) {
		if (this.extendsModel) {
			throw new MultipleInheritanceException(getName());
		}
		this.superModel = model;
		this.extendsModel = true;
	}
	
	public boolean isExtends() {
		return extendsModel;
	}
	
	public HLModel getSuperModel() {
		return isExtends() ? superModel : null;
	}
	
	public abstract int getRank();
}
