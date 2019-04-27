package com.asgar.myexport.js;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IMetadata;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateFactory;

public class JSEditorLinkButton extends JSEditorObject {
	
	private Button link;
	
	public JSEditorLinkButton(JSEditorView view) {
		super(view);
		
		link = new Button(view.getParent(), SWT.PUSH);
		link.setText("Link");
		link.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (view.getEditor().getCurrentScript() != null && view.getSelectedBP() != null) {
					try {
						linkScriptToProcess(view.getEditor().getCurrentScript(), view.getSelectedBP());
					} catch (IOException e1) {
						view.showError("Error", "Can't link script to model");
					}
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}
	
	public void linkScriptToProcess(File script, IArchimateConcept process) throws IOException {
		if (view.getCurrentModel() != null) {
			IMetadata metadata = view.getCurrentModel().getMetadata();
			
			if (metadata == null) {
				IArchimateFactory f = ArchimateFactory.init();
				metadata = f.createMetadata();
			}
			
			Optional<IProperty> prop = metadata.getEntries().stream().filter(x -> x.getValue().equals(process.getId())).findFirst();
			
			if (prop.isPresent()) {
				prop.get().setValue(script.getPath());
			} else {
				metadata.addEntry(process.getId(), script.getPath());
			}
			
			view.getCurrentModel().eResource().save(null);
		}
	}

	@Override
	public void dispose() {
		link.dispose();
	}
}
