package com.hyperledger.export.propwindow;

import java.io.IOException;
import java.text.ParseException;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;

import com.hyperledger.export.BNAExporter;
import com.hyperledger.export.propwindow.ExportAsBNAPage.Field;
import com.hyperledger.export.utils.Data;

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
			.addValue(Data.DESCRIPTION, Field.DESCRIPTION.getValue())
			.addValue(Data.AUTHOR_EMAIL, Field.AUTHOR_EMAIL.getValue())
			.addValue(Data.AUTHOR_NAME, Field.AUTHOR_NAME.getValue())
			.addValue(Data.LICENSE, Field.LICENSE.getValue());
		
		try {
			exporter.export(data);
		} catch (IOException | IllegalArgumentException | ParseException e) {
			showError("Can't export BNA", e.getMessage());
			return false;
		}
		
		showConfirm("Successfully", "BNA have exported");
		return true;
	}
	
	@Override
    public void addPages() {
        fPage = new ExportAsBNAPage(exporter.getModel());
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
