package com.asgar.myexport.js;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.impl.BusinessProcess;

public class JSEditorCombo extends JSEditorObject {
	
	private Combo combo;
	private List<BusinessProcess> bpList;
	private BusinessProcess selectedBP;
	
	public JSEditorCombo(JSEditorView view) {
		super(view);
		
		combo = new Combo(view.getParent(), SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		combo.setLayoutData(data);
		
		combo.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent e) {}
			
			@Override
			public void mouseDown(MouseEvent e) {
				updateBPList();				
			}
			
			@Override
			public void mouseDoubleClick(MouseEvent e) {}
		});
		
		combo.addListener(SWT.Selection, new Listener() {
	        @Override
	        public void handleEvent(Event e) {
	        	if (bpList != null) {
		        	selectedBP = bpList.get(combo.getSelectionIndex());
		        	view.getEditor().updateScript(view.findScript(selectedBP));
	        	}
	        }
	    });
	}
	
	public void selectBP(BusinessProcess bp) {
		selectedBP = bp;
		updateBPList();
	}
	
    public void updateBPList() {
    	if (view.getCurrentModel() != null) {
	    	bpList = view.getCurrentModel().getFolder(FolderType.BUSINESS).getElements()
															    			.stream()
															    			.filter(x -> x instanceof BusinessProcess)
															    			.map(x -> (BusinessProcess) x)
															    			.collect(Collectors.toList());
			List<String> names = bpList.stream()
									   .map(x -> x.getName())
									   .collect(Collectors.toList());
			
			combo.setItems(names.toArray(new String[names.size()]));
			
			if (bpList != null && selectedBP != null) {
		     	int index;
		 		for (index = 0; index < bpList.size(); index++)
		 			if (bpList.get(index).getId().equals(selectedBP.getId()))
		 				break;
		     	combo.select(index);
	    	}
			
    	}
    }
    
    public BusinessProcess getSelectedBP() {
    	return selectedBP;
    }

	@Override
	public void dispose() {
		combo.dispose();
	} 
}
