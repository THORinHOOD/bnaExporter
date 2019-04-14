package com.archimatetool.example.hl;

import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

public abstract class HLModel extends HLObject {
	
	public enum HLModelType {
		PARTICIPANT, ASSET, TRANSACTION;
		
		@Override
		public String toString() {
			return super.toString().toLowerCase();
		}
	}
	
	private final String header = "%s %s identified by %s";
	private final String content = "{\n%s}";
	protected HLModelType type;
	
	protected String documentation;
	protected String name;
	protected List<HLProp> properties;
	
	public HLModel(IArchimateConcept concept, HLModelType type) {
		this.type = type;
		name = concept.getName();
		
		properties = new ArrayList<HLProp>();
		properties.add(new HLProp("String", name.toLowerCase() + "Id"));
		for (IProperty prop : concept.getProperties())
			properties.add(new HLProp(prop));
		
		documentation = concept.getDocumentation();
	}
	
	public String getName() {
		return name;
	}
	
	public List<HLProp> getProperties() {
		return properties;
	}
	
	@Override
	public String getHLView() {
		String name = getName();
		String id = getName().toLowerCase() + "Id";
		String fields = "";
		for (HLProp prop : properties)
			fields += "\t" + prop.toString() + "\n"; 
		
		String result = String.format(header, type.toString(), name, id) + "\n" + String.format(content, fields);
		
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
}
