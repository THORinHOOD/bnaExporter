package com.archimatetool.example;

import java.util.ArrayList;
import java.util.List;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;

public class Participant extends HLModel {
	
	private String name;
	private List<HLProp> props;
	
	private final String header = "participant %s identified by %s";
	private final String content = "{\n%s}";
	
	public Participant(IArchimateConcept concept) {
		name = concept.getName();
		
		props = new ArrayList<HLProp>();
		
		props.add(new HLProp("String", name.toLowerCase() + "Id"));
		for (IProperty prop : concept.getProperties())
			props.add(new HLProp(prop));
	}
	
	public String getName() {
		return name;
	}
	
	public List<HLProp> getProps() {
		return props;
	}
	
	@Override
	public String getHLView() {
		String name = getName();
		String id = getName().toLowerCase() + "Id";
		String fields = "";
		for (HLProp prop : props)
			fields += "\t" + prop.toString() + "\n"; 
		
		return String.format(header, name, id) + "\n" + String.format(content, fields);
	}
}
