package com.hyperledger.export.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IProperty;
import com.hyperledger.export.models.Transaction;

public class ScriptsHandler {
	
	public static String scriptToText(File script) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(script));
		String line;
		String res = "";
		while ((line = reader.readLine()) != null)
			res += line + "\n";
		reader.close();
		return res;
	}
	
	public static String getScriptFile(IArchimateModel model, List<Transaction> transactions) throws IOException {
		String res = "";
		
		for (Transaction tx : transactions) {
			File script = getScript(model, tx);
			if (script != null && script.exists())
				res += scriptToText(script);
		}
		
		return res;
	}
	
	private static File getScript(IArchimateModel model, Transaction tx) {
		File script = null;
		
		Optional<IProperty> prop = model.getMetadata().getEntries().stream().filter(x -> x.getKey().equals(tx.getID())).findFirst();
		if (prop.isPresent())
			script = new File(prop.get().getValue());
		
		return script;
	}

}
