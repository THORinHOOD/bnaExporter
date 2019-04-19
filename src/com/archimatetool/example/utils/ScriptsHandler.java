package com.archimatetool.example.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.archimatetool.example.hl.models.Transaction;
import com.archimatetool.exmaple.hl.scripts.HLProcess;

public class ScriptsHandler {
	private static Map<String, HLProcess> processes;
	
	static {
		processes = new HashMap<String, HLProcess>();
	}
	
	public static void addProcess(HLProcess process) {
		processes.put(process.getID(), process);
	}
	
	public static HLProcess getProcess(String id) {
		return processes.get(id);
	}
	
	public static boolean processExist(String id) {
		return processes.containsKey(id);
	}
	
	public static String getScriptFile(List<Transaction> transactions) {
		String res = "";
		for (HLProcess process : processes.values())
			if (transactions.stream().anyMatch(tr -> tr.getID().equals(process.getID())))
				res += process.getHLView();
		return res;
	}
	
	public static Collection<HLProcess> getAll() {
		return processes.values();
	}
}
