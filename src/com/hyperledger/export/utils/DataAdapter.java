package com.hyperledger.export.utils;

import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IMetadata;
import com.archimatetool.model.impl.ArchimateFactory;

public class DataAdapter {
	
	public static final String ID = "Hyperledger";
	public static final String PATH_TO_FILE = "hyperledger.xml";
	
	public static void save(IArchimateModel model, String key, Object value) {
		IArchimateFactory factory = ArchimateFactory.init();
		IMetadata metaData = getMetaData(model, factory);
		
	}
	
	private static IMetadata getMetaData(IArchimateModel model, IArchimateFactory factory) {
		IMetadata metadata = model.getMetadata();
		if (metadata == null) {
			metadata = factory.createMetadata();
			metadata.addEntry(ID, PATH_TO_FILE);
		}
		return metadata;
	}
}


