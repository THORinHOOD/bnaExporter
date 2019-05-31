package com.hyperledger.export.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;

/**
 * Класс предок для hyperledger моделей
 */
public abstract class HLObject {
	
	//Концепции, на основе которых создана модель
	private List<IArchimateConcept> concepts;
	//ID
	private String id;
	
	private HLObject() {
		concepts = new ArrayList<IArchimateConcept>();
	}
	
	public HLObject(HLObject object) {
		this();
		concepts.addAll(object.getConcepts());
		id = object.getID();
	}
	
	public HLObject(IArchimateConcept concept) {
		this();
		concepts.add(concept);
		id = concept.getId();
	}

	/**
	 * Добавить концепцию
	 * @param concept концепция
	 * @return объект
	 */
	public HLObject addConcept(IArchimateConcept concept) {
		concepts.add(concept);
		return this;
	}
	
	/**
	 * Добавить коллекцию концепций
	 * @param concepts концепции
	 * @return объект
	 */
	public HLObject addConcepts(Collection concepts) {
		this.concepts.addAll(concepts);
		return this;
	}
	
	/**
	 * Получить id
	 * @return id
	 */
	public String getID() {
		return id;
	}
	
	/**
	 * Получить список концепций
	 * @return список концепций
	 */
	public List<IArchimateConcept> getConcepts() {
		return concepts;
	}
	
	/**
	 * Получить HyperLedger представление модели
	 * @return HyperLedger представление модели
	 */
	public abstract String getHLView();
	
}

