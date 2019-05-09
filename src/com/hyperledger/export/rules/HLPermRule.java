package com.hyperledger.export.rules;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
import com.hyperledger.export.models.Asset;
import com.hyperledger.export.models.HLModel;
import com.hyperledger.export.models.Participant;
import com.hyperledger.export.models.Transaction;

public class HLPermRule {
	
	public static final String ACTION_KEY = "ACTION";
	public static final String OPERATION_KEY = "OPERATION";
	public static final String CONDITION_KEY = "CONDITION";
	public static final String PARTICIPANT_VARIABLE_KEY = "PARTICIPANT_VAR";
	public static final String RESOURCE_VARIABLE_KEY = "RESOURCE_VAR";
	
	public static final int CREATE = 0b1;
	public static final int READ = 0b10;
	public static final int UPDATE = 0b100;
	public static final int DELETE = 0b1000;
	public static final int ALL = CREATE | READ | UPDATE | DELETE;
	
	public static final String OPERATION_CREATE = "CREATE";
	public static final String OPERATION_READ = "READ";
	public static final String OPERATION_UPDATE = "UPDATE";
	public static final String OPERATION_DELETE = "DELETE";
	public static final String OPERATION_ALL = "ALL";
	
	public static final String ACTION_DENY = "DENY";
	public static final String ACTION_ALLOW = "ALLOW";
	
	public static final String HEADER = "rule %s {";
	public static final String DESCRIPTION = "\tdescription: \"%s\"";
	public static final String PARTICIPANT = "\tparticipant%s: \"%s\"";
	public static final String OPERATION = "\toperation: %s";
	public static final String RESOURCE = "\tresource%s: \"%s\"";
	public static final String CONDITION = "\tcondition: (%s)";
	public static final String ACTION = "\taction: %s";
	public static final String FOOTER = "}";
	
	public static final String EQUALS = "(%s == %s)";
	public static final String NOT_EQUALS = "(%s != %s)";

	private String name = "";
	private String description = "";
	private String participant = "";
	private String operation = "";
	private String resource = "";
	private String condition;
	private String action= "";
	private String participantVariable = "";
	private String resourceVariable = "";
	
	private static HLPermRule networkAdminUserRule;
	private static HLPermRule networkAdminSystemRule;
	private static HLPermRule systemAclRule;
	
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

	public static Optional<HLPermRule> createRule(IArchimateRelationship relation, HLModel source, HLModel target) {
	
		if (relation.getName().trim().equals("")) {
			throw new InvalidConceptName(relation.getClass().getSimpleName(), relation.getName());
		}
		
		if (source instanceof Participant && target instanceof Asset && relation instanceof AccessRelationship) {
			HLPermRule rule = new HLPermRule();
			rule.name = relation.getName();
			rule.description = relation.getDocumentation();
			rule.participant = source.getFullName();
			rule.resource = target.getFullName();
			setProperties(relation, rule);
			return Optional.of(rule);
		} else if (source instanceof Participant && target instanceof Transaction && relation instanceof AssignmentRelationship) {
			HLPermRule rule = new HLPermRule();
			rule.name = relation.getName();
			rule.description = relation.getDocumentation();
			rule.participant = source.getFullName();
			rule.resource = target.getFullName();
			setProperties(relation, rule);
			return Optional.of(rule);
		}
		
		return Optional.empty();
	}
	
	public static boolean isRule(IArchimateRelationship relation, Map<IArchimateConcept, HLModel> conceptToModel) {
		if (!((relation instanceof AccessRelationship) || (relation instanceof AssignmentRelationship))) {
			return false;
		}
		HLModel source = null;
		HLModel target = null;
		if (conceptToModel.containsKey(relation.getSource()))
			source = conceptToModel.get(relation.getSource());
		if (conceptToModel.containsKey(relation.getTarget()))
			target = conceptToModel.get(relation.getTarget());
		if (source == null || target == null)
			return false;
		return ((source instanceof Participant) || (relation.getSource() instanceof BusinessActor)) && 
			   ((target instanceof Transaction) || (target instanceof Asset) || (target instanceof Participant) || (relation.getTarget() instanceof BusinessActor));
	}
	
	public static boolean isRule(IArchimateRelationship relation) {
		
		if (!((relation instanceof AccessRelationship) || (relation instanceof AssignmentRelationship))) {
			return false;
		}
		
		IArchimateConcept source = relation.getSource();
		IArchimateConcept target = relation.getTarget();
		
		return ((source instanceof BusinessRole) || (source instanceof BusinessActor)) && 
			   ((target instanceof BusinessObject) || (target instanceof BusinessProcess) || (target instanceof BusinessActor));
	}
	
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
			
			if (!setAction) {
				throw new IllegalArgumentException("Missed access rule \""+ rule.name +"\" action property");
			}
			
			if (!setOperation) {
				throw new IllegalArgumentException("Missed access rule \""+ rule.operation +"\" action property");
			}
			
			if (rule != null && !rule.condition.equals("") && (rule.participantVariable.equals("") || rule.resourceVariable.equals(""))) {
				List<String> names = new ArrayList<>();
				if (rule.participantVariable.equals(""))
					names.add(rule.participantVariable);
				if (rule.resourceVariable.equals(""))
					names.add(rule.resourceVariable);
				throw new InvalidConditionVariablesNames(rule.name, names.toArray(new String[names.size()]));
			}
		}
		
		if (!setAction) {
			rule.action = ACTION_ALLOW;
		}
		
		if (!setOperation) {
			rule.operation = OPERATION_ALL;
		}
		
	}
	
	private static void setOperation(String operation, HLPermRule rule) {
		try {
			int value = Integer.valueOf(operation);
			rule.operation = getOperation(value);
		} catch(Exception ex) {
			throw new InvalidPropertyValue(rule.name, OPERATION_KEY, operation, "Integer values");
		}
	}

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
	
	private void setVariables(HLPermRule rule, HLModel source, HLModel target) {
		
	}
	
	public static String getOperation(int actions) {
		String res = "";
		if (check(actions, ALL))
			return OPERATION_ALL;
		
		if (check(actions, CREATE))
			res = OPERATION_CREATE + ", ";
		
		if (check(actions, READ))
			res += OPERATION_READ + ", ";
		
		if (check(actions, UPDATE))
			res += OPERATION_UPDATE + ", ";
		
		if (check(actions, DELETE))
			res += OPERATION_DELETE + ", ";
	
		return res.substring(0, res.length() - 2);
	}
	
	private static boolean check(int a, int... b) {
		for (int val : b)
			if ((a & val) == 0)
				return false;
		return true;
	}
	
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
