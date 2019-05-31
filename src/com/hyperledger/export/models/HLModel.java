package com.hyperledger.export.models;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.hyperledger.export.exceptions.InvalidTypeOfIdField;
import com.hyperledger.export.exceptions.MultipleInheritanceException;

/**
 * Класс модели
 */
public abstract class HLModel extends HLObject implements HLNamed {
	
	public enum HLModelType {
		PARTICIPANT, ASSET, TRANSACTION;
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}
	
	//Шаблон заголовка
	private final String HEADER = "%s %s";
	//Шаблон identified by
	private final String IDENTIFIED_BY = "identified by %s";
	//Шаблон extends
	private final String EXTENDS = "extends %s";
	//Шаблон содержимого
	private final String CONTENT = "{\n%s}";
	
	//Тип класса модели
	protected HLModelType type;
	
	//Комментарий
	protected String documentation;
	//Имя модели
	protected String name;
	//Пространство имён 
	protected String namespace;
	//Поля модели
	protected List<HLField> fields;
		
	//Должно ли быть идентифицирующее поле
	protected boolean identified;
	//Есть ли id поле у модели
	protected boolean hasId = false;
	//ID поле модели
	protected HLField idField = null;
	
	//Родительская модель
	protected HLModel superModel;
	//Наследуется ли модель
	protected boolean extendsModel = false;

	public HLModel(IArchimateConcept concept, HLModelType type, String namespace, boolean identified) throws ParseException {
		super(concept);
		
		this.namespace = namespace;
		this.type = type;
		this.identified = identified;
		name = concept.getName();
		documentation = concept.getDocumentation();	
		
		setFields(concept);
	}
	
	/**
	 * Устанвовить свойства концепции в поля модели
	 * @param concept концепция
	 * @throws ParseException
	 */
	protected void setFields(IArchimateConcept concept) throws ParseException {
		fields = new ArrayList<HLField>();
		for (IProperty prop : concept.getProperties())
			fields.add(HLField.createField(this, prop, HLField.Type.PROPERTY));
		
		if (this.identified)
			findIdProp();
	}
	
	/**
	 * Найти идентифицирующее свойство
	 * @throws ParseException
	 */
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
	
	/**
	 * Получить имя модели
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Получить полное имя модели
	 */
	public String getFullName() {
		return namespace + "." + name;
	}
	
	/**
	 * Получить поля модели
	 * @return список полей
	 */
	public List<HLField> getFields() {
		return fields;
	}
	
	/**
	 * Добавить поле модели
	 * @param field поле
	 */
	public void addField(HLField field) {
		fields.add(field);
	}
	
	/**
	 * Получить HyperLedger Modeling представление модели
	 */
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
	
	/**
	 * Есть ли id у модели
	 * @return есть ли у модели id
	 */
	public boolean hasId() {
		return hasId;
	}
	
	/**
	 * Установить родительскую модель
	 * @param model модель
	 */
	public void setSuperModel(HLModel model) {
		if (this.extendsModel) {
			throw new MultipleInheritanceException(getName());
		}
		this.superModel = model;
		this.extendsModel = true;
	}
	
	/**
	 * Наследуется ли модель
	 * @return наследуется ли модель
	 */
	public boolean isExtends() {
		return extendsModel;
	}
	
	/**
	 * Получить родительскую модель
	 * @return родительская модель
	 */
	public HLModel getSuperModel() {
		return isExtends() ? superModel : null;
	}
	
	/**
	 * Получить ранг модели для сортировки
	 * @return ранг
	 */
	public abstract int getRank();
}
