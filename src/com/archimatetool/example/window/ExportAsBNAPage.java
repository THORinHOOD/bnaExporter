package com.archimatetool.example.window;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IProperty;

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
		
		public void setText(String text) {
			this.input.setText(text);
		}
		
		public String getValue() {
			if (input != null)
				return input.getText();
			return "null";
		}
	};
	
	private IArchimateModel model;
	
	public ExportAsBNAPage(IArchimateModel model) {
		this("ExportAsBNAWizard", model);
	}
	
	protected ExportAsBNAPage(String pageName, IArchimateModel model) {
		super(pageName);
		this.model = model;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
        container.setLayout(new GridLayout());
        setControl(container);
        
        GridData gd = new GridData();
        gd.widthHint = 500;
        
        for (Field field : Field.values()) {
        	addField(container, gd, field);
        }
        
        Field.BUSINESS_NETWORK_NAME.setText(model.getName());
        Field.DESCRIPTION.setText(model.getPurpose());
        
        for (IProperty prop : model.getProperties()) {
        	String key = prop.getKey().toLowerCase().trim();
        	String value = prop.getValue().replaceAll(" ", "");
        	if (key.equals("namespace")) {
        		Field.NAMESPACE.setText(value);
        	} else if (key.equals("author") || key.equals("author name") || key.equals("author_name")) {
        		Field.AUTHOR_NAME.setText(value);
        	} else if (key.equals("email") || key.equals("author email") || key.equals("author_email")) {
        		Field.AUTHOR_EMAIL.setText(value);
        	} else if (key.equals("license")) {
        		Field.LICENSE.setText(value);
        	}
        }
        
        
        setTitle("Export BNA");
	}
	
	private void addField(Composite container, GridData gd, Field field) {
		Label label = new Label(container, SWT.READ_ONLY);
        label.setText(field.toString());
       
        Text input = new Text(container, SWT.BORDER | SWT.SINGLE);
        input.setLayoutData(gd);
        
        field.setInput(input);
	}

}
