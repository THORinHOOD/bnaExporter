package com.archimatetool.exmaple.hl.scripts;

import com.archimatetool.example.hl.models.HLObject;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.impl.BusinessProcess;

public class HLProcess extends HLObject {
	
	public final String HEADER = "async function %s(%s) {\n";
	public final String FOOTER = "}\n";

	private String code;
	
	public HLProcess(BusinessProcess process) {
		super((IArchimateConcept) process);
		String params = "params";
		code = String.format(HEADER, process.getName(), params) + "\n" + FOOTER;
	}
	
	public void addLine(String line) {
		code += line + "\n";
	}
	
	public void setCode(String code) {
		this.code = code;
	}
	
	@Override
	public String getHLView() {
		return code;
	}

}
