package com.hyperledger.export.models;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.AssignmentRelationship;
import com.archimatetool.model.impl.BusinessActor;
import com.archimatetool.model.impl.BusinessRole;

/**
 * Экземпляр модели
 */
public class HLModelInstance implements HLNamed {
	
	//Модель, от кого экземпляр
	private HLModel instanceOf;
	//Концепция
	private IArchimateConcept concept;
	//ID экземпляра
	private String id;
	
	/**
	 * Создать экземпляр по коцепции
	 * @param conceptToModel словарь соответствия между мконцепцией и моделью
	 * @param concept концепция
	 * @return экземпляр
	 */
	public static Optional<HLModelInstance> createModelInstance(Map<IArchimateConcept, HLModel> conceptToModel, IArchimateConcept concept) {
		Optional<HLModel> model = getModel(conceptToModel, concept);
		if (!model.isPresent())
			return Optional.empty();
		Optional<String> id = getId(concept);
		if (!id.isPresent())
			return Optional.empty();
		
		return Optional.of(new HLModelInstance(model.get(), concept, id.get()));
	}
	
	/**
	 * Получить модель по концепции
	 * @param conceptToModel соответствие между концепцией и моделью
	 * @param concept концепция
	 * @return модель
	 */
	private static Optional<HLModel> getModel(Map<IArchimateConcept, HLModel> conceptToModel, IArchimateConcept concept) {
		for (IArchimateRelationship relation : concept.getSourceRelationships()) {
			if (relation instanceof AssignmentRelationship) {
				if (relation.getTarget() instanceof BusinessRole && relation.getSource() instanceof BusinessActor) {
					if (conceptToModel.containsKey(relation.getTarget())) {
						return Optional.of(conceptToModel.get(relation.getTarget()));
					} 
				}
			}
		}
		
		return Optional.empty();
	}
	
	/**
	 * Получить id экземпляра
	 * @param concept концепция
	 * @return id
	 */
	private static Optional<String> getId(IArchimateConcept concept) {
		Optional<IProperty> idProperty = concept.getProperties()
				.stream()
				.filter(prop -> prop.getKey().trim().toUpperCase().equals("ID"))
				.findFirst();
		if (!idProperty.isPresent()) {
			return Optional.empty();
		}
		
		String id = idProperty.get().getValue().trim();
		if (id.equals("")) {
			String tmpl = "Instance \"%s\" have incorrect id property";
			throw new IllegalArgumentException(String.format(tmpl, concept.getName()));
		}
		return Optional.of(id);
	}
	
	private HLModelInstance(HLModel instanceOf, IArchimateConcept concept, String id) {
		this.instanceOf = instanceOf;
		this.id = id;
		this.concept = concept;
	}
	
	/**
	 * Получить полное имя
	 */
	public String getFullName() {
		return instanceOf.getFullName() + "#" + id;
	}
	
	/**
	 * Получить имя
	 */
	public String getName() {
		return id;
	}
	
	/**
	 * Экземпляром чего является
	 * @return
	 */
	public HLModel instanceOf() {
		return instanceOf;
	}
	
	/**
	 * Получить концепцию
	 * @return концепция
	 */
	public IArchimateConcept getConcept() {
		return concept;
	}
}
