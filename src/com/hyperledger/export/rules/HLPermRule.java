package com.hyperledger.export.rules;

import com.archimatetool.model.impl.AccessRelationship;
import com.archimatetool.model.impl.ArchimateRelationship;
import com.archimatetool.model.impl.AssignmentRelationship;
import com.hyperledger.export.models.Asset;
import com.hyperledger.export.models.HLModel;
import com.hyperledger.export.models.Participant;
import com.hyperledger.export.models.Transaction;

public class HLPermRule {
	
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
	public static final String DESCRIPTION = "\tdescription:  \"%s\"";
	public static final String PARTICIPANT = "\tparticipant%s:  \"%s\"";
	public static final String OPERATION = "\toperation:  %s";
	public static final String RESOURCE = "\tresource%s:  \"%s\"";
	public static final String CONDITION = "\tcondition:  %s";
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
	
	public static HLPermRule createRule(ArchimateRelationship relation, HLModel source, HLModel target) {
	
		if (source instanceof Participant && target instanceof Asset && relation instanceof AccessRelationship) {
			HLPermRule rule = new HLPermRule();
			
			rule.name = relation.getName();
			rule.description = relation.getDocumentation();
			rule.participant = source.getFullName();
			rule.resource = target.getFullName();
			//rule.condition = "";
			//TODO
			rule.operation = OPERATION_ALL;
			rule.action = ACTION_ALLOW;
			
			return rule;
		} else if (source instanceof Participant && target instanceof Transaction && relation instanceof AssignmentRelationship) {
			HLPermRule rule = new HLPermRule();
			
			rule.name = relation.getName();
			rule.description = relation.getDocumentation();
			rule.participant = source.getFullName();
			rule.resource = target.getFullName();
			//rule.condition = "";
			//TODO
			rule.operation = OPERATION_ALL;
			rule.action = ACTION_ALLOW;
			
			return rule;
		}
		
		
		return null;
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
		String res = "";
		res += String.format(HEADER, name) + "\n";
		res += String.format(DESCRIPTION, description) + "\n";
		res += String.format(PARTICIPANT, "", participant) + "\n";
		res += String.format(OPERATION, operation) + "\n";
		res += String.format(RESOURCE, "", resource) + "\n";
		if (condition != null)
			res += String.format(CONDITION, condition) + "\n";
		res += String.format(ACTION, action) + "\n";
		res += FOOTER + "\n";
		return res;
	}
}
