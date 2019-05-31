package com.hyperledger.export.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateRelationship;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.AccessRelationship;
import com.archimatetool.model.impl.AssignmentRelationship;
import com.archimatetool.model.impl.BusinessActor;
import com.archimatetool.model.impl.BusinessObject;
import com.archimatetool.model.impl.BusinessProcess;
import com.archimatetool.model.impl.BusinessRole;
import com.hyperledger.export.exceptions.InvalidConceptName;
import com.hyperledger.export.exceptions.InvalidConditionVariablesNames;
import com.hyperledger.export.exceptions.InvalidPropertyValue;
import com.hyperledger.export.models.HLModel;
import com.hyperledger.export.models.HLModelInstance;
import com.hyperledger.export.models.HLNamed;
import com.hyperledger.export.models.Participant;
import com.hyperledger.export.models.Transaction;

/**
 * Правило доступа HyperLedger Composer
 */
public class HLPermRule {
	
	// Ключ действия
	public static final String ACTION_KEY = "ACTION";
	// Ключ операции
	public static final String OPERATION_KEY = "OPERATION";
	// Ключ условия
	public static final String CONDITION_KEY = "CONDITION";
	// Ключ имени переменной участника
	public static final String PARTICIPANT_VARIABLE_KEY = "PARTICIPANT_VAR";
	// Ключ имени переменной ресурса 
	public static final String RESOURCE_VARIABLE_KEY = "RESOURCE_VAR";
	
	// Значение операции создание
	public static final int CREATE = 0b1;
	// Значени операции чтение
	public static final int READ = 0b10;
	// Значени операции обновление
	public static final int UPDATE = 0b100;
	// Значени операции удаление
	public static final int DELETE = 0b1000;
	// Значение всех операций
	public static final int ALL = CREATE | READ | UPDATE | DELETE;
	
	// Текстовое представление create
	public static final String OPERATION_CREATE = "CREATE";
	// Текстовое представление read
	public static final String OPERATION_READ = "READ";
	// Текстовое представление update
	public static final String OPERATION_UPDATE = "UPDATE";
	// Текстовое представление delete
	public static final String OPERATION_DELETE = "DELETE";
	// Текстовое представление all
	public static final String OPERATION_ALL = "ALL";
	
	// Текствовое представление запрета доступа
	public static final String ACTION_DENY = "DENY";
	// Текстовое представление разрешения доступа
	public static final String ACTION_ALLOW = "ALLOW";

	// Шаблон заголовка
	public static final String HEADER = "rule %s {";
	// Шаблон описания
	public static final String DESCRIPTION = "\tdescription: \"%s\"";
	// Шаблон участника
	public static final String PARTICIPANT = "\tparticipant%s: \"%s\"";
	// Шаблон операции
	public static final String OPERATION = "\toperation: %s";
	// Шаблон ресурса
	public static final String RESOURCE = "\tresource%s: \"%s\"";
	// Шаблон условия
	public static final String CONDITION = "\tcondition: (%s)";
	// Шаблон типа доступа
	public static final String ACTION = "\taction: %s";
	// Закрывающяя скобка
	public static final String FOOTER = "}";
	
	// Имя правила
	private String name = "";
	// Описание правила
	private String description = "";
	// Участник
	private String participant = "";
	// Операции
	private String operation = "";
	// Ресурс
	private String resource = "";
	// Условие
	private String condition;
	// Действие
	private String action= "";
	// Переменная участника
	private String participantVariable = "";
	// Переменная ресурса
	private String resourceVariable = "";
	
	// Правило для админа сети
	private static HLPermRule networkAdminUserRule;
	// Правило для админа сети
	private static HLPermRule networkAdminSystemRule;
	// Правило сети
	private static HLPermRule systemAclRule;
	
	/**
	 * Получить правило сети
	 * @return
	 */
	public static HLPermRule getSystemAclRule() {
		if (systemAclRule == null) {
			systemAclRule = new HLPermRule();
			systemAclRule.name = "SystemACL";
			systemAclRule.description = "System ACL to permit all access";
			systemAclRule.participant = "org.hyperledger.composer.system.Participant";
			systemAclRule.operation = OPERATION_ALL;
			systemAclRule.resource = "org.hyperledger.composer.system.**";
			systemAclRule.action = ACTION_ALLOW;
		} 
		return systemAclRule;
	}
	
	/**
	 * Получить правило админа сети
	 * @return
	 */
	public static HLPermRule getNetworkAdminUserRule() {
		if (networkAdminUserRule == null) {
			networkAdminUserRule = new HLPermRule();
			networkAdminUserRule.name = "NetworkAdminUser";
			networkAdminUserRule.description = "Grant business network administrators full access to user resources";
			networkAdminUserRule.participant = "org.hyperledger.composer.system.NetworkAdmin";
			networkAdminUserRule.operation = OPERATION_ALL;
			networkAdminUserRule.resource = "**";
			networkAdminUserRule.action = ACTION_ALLOW;
		} 
		return networkAdminUserRule;
	}
	
	/**
	 * Получить правило админа сети
	 * @return
	 */
	public static HLPermRule getNetworkAdminSystemRule() {
		if (networkAdminSystemRule == null) {
			networkAdminSystemRule = new HLPermRule();
			networkAdminSystemRule.name = "NetworkAdminSystem";
			networkAdminSystemRule.description = "Grant business network administrators full access to system resources";
			networkAdminSystemRule.participant = "org.hyperledger.composer.system.NetworkAdmin";
			networkAdminSystemRule.operation = OPERATION_ALL;
			networkAdminSystemRule.resource = "org.hyperledger.composer.system.**";
			networkAdminSystemRule.action = ACTION_ALLOW;
		} 
		return networkAdminSystemRule;
	}
	
	/**
	 * Создать правило по отношению, источнику и цели
	 * @param relation отношение
	 * @param source источник
	 * @param target цель
	 * @return правило
	 */
	public static Optional<HLPermRule> createRule(IArchimateRelationship relation, Optional<HLNamed> source, Optional<HLNamed> target) {
		if (!source.isPresent() || !target.isPresent()) {
			return Optional.empty();
		}

		if (checkSource(source.get()) && checkRelation(relation, target.get())) {
			if (relation.getName().trim().equals("")) {
				throw new InvalidConceptName(relation.getClass().getSimpleName(), relation.getName());
			}
			HLPermRule rule = new HLPermRule();
			rule.name = relation.getName();
			rule.description = relation.getDocumentation();
			rule.participant = source.get().getFullName();
			rule.resource = target.get().getFullName();
			setProperties(relation, rule);
			return Optional.of(rule);	
		}		
		return Optional.empty();
	}
	
	/**
	 * Проверить источник на корректность
	 * @param source
	 * @return
	 */
	private static boolean checkSource(HLNamed source) {
		if (source instanceof Participant) {
			return true;
		}
		
		if (source instanceof HLModelInstance) {
			HLModelInstance sourceInst = (HLModelInstance) source;
			if (sourceInst.instanceOf() instanceof Participant) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Проверить отношение на корректность
	 * @param relation
	 * @param target
	 * @return
	 */
	private static boolean checkRelation(IArchimateRelationship relation, HLNamed target) {
		return (relation instanceof AccessRelationship || (relation instanceof AssignmentRelationship && target instanceof Transaction));
	}
	
	/**
	 * Проверить является ли данный набор - правилом
	 * @param relation
	 * @param conceptToHLObject
	 * @return
	 */
	public static boolean isRule(IArchimateRelationship relation, Function<IArchimateConcept, Optional<HLNamed>> conceptToHLObject) {
		if (!((relation instanceof AccessRelationship) || (relation instanceof AssignmentRelationship))) {
			return false;
		}
		Optional source = conceptToHLObject.apply(relation.getSource());
		Optional target = conceptToHLObject.apply(relation.getTarget());
			
		if (!source.isPresent() || !target.isPresent())
			return false;

		return ((source.get() instanceof Participant) || (source.get() instanceof HLModelInstance && ((HLModelInstance)source.get()).instanceOf() instanceof Participant));
	}
	
	/**
	 * Является ли правилом
	 * @param relation
	 * @return
	 */
	public static boolean isRule(IArchimateRelationship relation) {
		
		if (!((relation instanceof AccessRelationship) || (relation instanceof AssignmentRelationship))) {
			return false;
		}
		
		IArchimateConcept source = relation.getSource();
		IArchimateConcept target = relation.getTarget();
		
		return ((source instanceof BusinessRole) || (source instanceof BusinessActor)) && 
			   ((target instanceof BusinessObject) || (target instanceof BusinessProcess) || (target instanceof BusinessActor));
	}
	
	/**
	 * Установить свойства
	 * @param relation
	 * @param rule
	 */
	private static void setProperties(IArchimateRelationship relation, HLPermRule rule) {
		boolean setAction = false;
		boolean setOperation = false;
		
		for (IProperty prop : relation.getProperties()) {
			String value = prop.getKey().trim().toUpperCase();
			switch(value) {
				case ACTION_KEY:
					setAction(prop.getValue().trim().toUpperCase(), rule);
					setAction = true;
					break;
				case OPERATION_KEY:
					setOperation(prop.getValue().trim(), rule);
					setOperation = true;
					break;
				case CONDITION_KEY:
					rule.condition = prop.getValue().trim();
					break;
				case PARTICIPANT_VARIABLE_KEY:
					rule.participantVariable = prop.getValue().trim();
					break;
				case RESOURCE_VARIABLE_KEY:
					rule.resourceVariable = prop.getValue().trim();
					break;
			}
		}
		
		if (!setAction) {
			rule.action = ACTION_ALLOW;
		}
		
		if (!setOperation) {
			rule.operation = OPERATION_ALL;
		}

		if (rule.condition != null && !rule.condition.trim().equals("") && 
		    (rule.participantVariable == null || rule.resourceVariable == null || rule.participantVariable.equals("") || rule.resourceVariable.equals(""))) {
			List<String> names = new ArrayList<>();
			if (rule.participantVariable.equals(""))
				names.add(rule.participantVariable);
			if (rule.resourceVariable.equals(""))
				names.add(rule.resourceVariable);
			throw new InvalidConditionVariablesNames(rule.name, names.toArray(new String[names.size()]));
		}		
	}
	
	/**
	 * Установить операции
	 * @param operation
	 * @param rule
	 */
	private static void setOperation(String operation, HLPermRule rule) {
		try {
			int value = Integer.valueOf(operation);
			rule.operation = getOperation(value);
		} catch(Exception ex) {
			throw new InvalidPropertyValue(rule.name, OPERATION_KEY, operation, "Integer values");
		}
	}

	/**
	 * Установить тип доступа
	 * @param action
	 * @param rule
	 */
	private static void setAction(String action, HLPermRule rule) {
		boolean set = false;
		switch(action) {
			case ACTION_ALLOW:
				set = true;
				rule.action = ACTION_ALLOW;
				break;
			case ACTION_DENY:
				set = true;
				rule.action = ACTION_DENY;
				break;
		}
		
		if (!set) {
			throw new InvalidPropertyValue(rule.name, ACTION_KEY, action, ACTION_ALLOW, ACTION_DENY);
		}
	}
	
	/**
	 * Получить текстовое представление операций
	 * @param actions
	 * @return
	 */
	public static String getOperation(int actions) {
		String res = "";
		if ((actions & ALL) >= ALL)
			return OPERATION_ALL;
		
		if ((actions & CREATE) != 0)
			res = OPERATION_CREATE + ", ";
		
		if ((actions & READ) != 0)
			res += OPERATION_READ + ", ";
		
		if ((actions & UPDATE) != 0)
			res += OPERATION_UPDATE + ", ";
		
		if ((actions & DELETE) != 0)
			res += OPERATION_DELETE + ", ";
	
		return res.substring(0, res.length() - 2);
	}

	/**
	 * Получить HyperLedger Composer представление правила
	 * @return
	 */
	public String getHLView() {
		boolean haveCondition = condition != null && !condition.trim().equals("");
		String res = "";
		res += String.format(HEADER, name) + "\n";
		res += String.format(DESCRIPTION, description) + "\n";
		res += String.format(PARTICIPANT, !haveCondition ? "" : "(" + participantVariable + ")", participant) + "\n";
		res += String.format(OPERATION, operation) + "\n";
		res += String.format(RESOURCE, !haveCondition ? "" : "(" + resourceVariable + ")", resource) + "\n";
		if (haveCondition)
			res += String.format(CONDITION, condition) + "\n";
		res += String.format(ACTION, action) + "\n";
		res += FOOTER + "\n";
		return res;
	}
}
