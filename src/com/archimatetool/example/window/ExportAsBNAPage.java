package com.archimatetool.example.window;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.ui.UIUtils;

public class ExportAsBNAPage extends WizardPage {
	public enum Field {
		BUSINESS_NETWORK_NAME("Business network name"),
		DESCRIPTION("Description"),
		AUTHOR_NAME("Author name"),
		AUTHOR_EMAIL("Author email"),
		LICENSE("License"),
		NAMESPACE("Namespace");
		
		private String caption;
		private Text input;
		
		Field(String caption) {
			this.caption = caption;
		}
		
		@Override
		public String toString() {
			return caption;
		}
		
		public void setInput(Text input) {
			this.input = input;
		}
		
		public String getValue() {
			if (input != null)
				return input.getText();
			return "null";
		}
	};
	
	public ExportAsBNAPage() {
		this("ExportAsBNAWizard");
	}
	
	protected ExportAsBNAPage(String pageName) {
		super(pageName);
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout());
        setControl(container);
        
        GridData gd = new GridData();
        gd.widthHint = 500;
        gd.horizontalSpan = 2;
        
        
        for (Field field : Field.values()) {
        	addField(container, gd, field);
        }

	}
	
	private void addField(Composite container, GridData gd, Field field) {
		Label label = new Label(container, SWT.READ_ONLY);
        label.setText(field.toString());
       
        Text input = new Text(container, SWT.BORDER | SWT.SINGLE);
        input.setLayoutData(gd);
        
        field.setInput(input);
	}

}
