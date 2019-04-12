package com.archimatetool.example;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

public class ExportAsBNAWizard extends Wizard {

    private ExportAsBNAPage fPage;
    
    public ExportAsBNAWizard() {
    	
    }
    
	@Override
	public boolean performFinish() {
		
		for (ExportAsBNAPage.Field field : ExportAsBNAPage.Field.values())
			System.out.println(field.getValue());
		//ExportAsBNAPage.Field.<Some Field>.getValue(); // get value
		
		System.out.println("finish");
		return true;
	}
	
	@Override
    public void addPages() {
        fPage = new ExportAsBNAPage();
        addPage(fPage);
    }

}
