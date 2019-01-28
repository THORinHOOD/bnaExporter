package com.archimatetool.example;

import com.archimatetool.model.IProperty;

public abstract class HLObject {
	public abstract String getHLView();

	public static class HLProp {
		private String type;
		private String name;
		
		public static final String[] primitiveTypes = {
				"String",
				"Double",
				"Integer",
				"Long",
				"DateTime",
				"Boolean"
		};
		
		protected boolean isPrimitive(String type) {
			for (String primType : primitiveTypes)
				if (primType.equals(type))
					return true;
			return false;
		}
		
		public HLProp(IProperty prop) {
			type = prop.getKey();
			name = prop.getValue();
		}
		
		public HLProp(String type, String name) {
			this.type = type;
			this.name = name;
		}
		
		public String getType() {
			return type;
		}
		
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			if (isPrimitive(type))
				return "o " + type + " " + name;
			else
				return "--> " + type + " " + name;
		}	
	}
}

