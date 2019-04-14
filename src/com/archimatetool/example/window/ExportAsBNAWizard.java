package com.archimatetool.example.window;

import java.io.IOException;
import java.text.ParseException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import com.archimatetool.example.BNAExporter;
import com.archimatetool.example.exc.DuplicateObjectNames;
import com.archimatetool.example.exc.NotAppropriateFieldName;
import com.archimatetool.example.exc.UnknownTypeException;
import com.archimatetool.example.utils.Data;
import com.archimatetool.example.window.ExportAsBNAPage.Field;

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
		} catch (IOException | ParseException | DuplicateObjectNames | NotAppropriateFieldName | UnknownTypeException e) {
			showError("Can't export BNA", e.getMessage());
			return false;
		}
		
		showConfirm("Successfully", "BNA was exported");
		return true;
	}
	
	@Override
    public void addPages() {
        fPage = new ExportAsBNAPage();
        addPage(fPage);
    }
	
	private void showError(String title, String message) {
		getShell().getDisplay().asyncExec
	    (new Runnable() {
	        public void run() {
	            MessageDialog.openWarning(getShell(), title, message);
	        }
	    });
	}

	private void showConfirm(String title, String message) {
		getShell().getDisplay().asyncExec
	    (new Runnable() {
	        public void run() {
	            MessageDialog.openConfirm(getShell(), title, message);
	        }
	    });
	}
}
