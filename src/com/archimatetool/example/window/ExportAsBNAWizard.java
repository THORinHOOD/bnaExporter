package com.archimatetool.example.window;

import java.io.IOException;

import org.eclipse.jface.wizard.Wizard;

import com.archimatetool.example.BNAExporter;
import com.archimatetool.example.utils.Data;

import static com.archimatetool.example.window.ExportAsBNAPage.Field;

public class ExportAsBNAWizard extends Wizard {

    private ExportAsBNAPage fPage;
    
    private BNAExporter exporter;
    
    public ExportAsBNAWizard(BNAExporter exporter) {
    	this.exporter = exporter;
    }
    
	@Override
	public boolean performFinish() {

		Data data = new Data();
		data = data
			.addValue(Data.NAMESPACE, Field.NAMESPACE.getValue())
			.addValue(Data.BUSINESS_NETWORK_NAME, Field.BUSINESS_NETWORK_NAME.getValue())
			.addValue(Data.DESCRIPTION, Field.DESCRIPTION.getValue());
		
		try {
			exporter.export(data);
		} catch (IOException e) {
			System.out.println(e.getMessage());
			return false;
		}
		
		System.out.println("finish");
		return true;
	}
	
	@Override
    public void addPages() {
        fPage = new ExportAsBNAPage();
        addPage(fPage);
    }

}
