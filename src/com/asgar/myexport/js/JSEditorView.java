package com.asgar.myexport.js;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.archimatetool.example.utils.ScriptsHandler;
import com.archimatetool.exmaple.hl.scripts.HLProcess;
import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.impl.BusinessProcess;

public class JSEditorView extends ViewPart implements ISelectionListener {
	
	public static String ID = "com.asgar.myexport.views.JSEditorView";
	
	private BusinessProcess currentProcess;
	private IArchimateModel currentModel;
	private List<BusinessProcess> currentModelBP;
	
	private StyledText text;
	private Combo combo;
	private Composite parent;
	
	private IPropertyListener propChanged = new IPropertyListener() {
		@Override
		public void propertyChanged(Object source, int propId) {
			if (currentProcess != null && combo != null)
				updateCombo();
		}
	};
	
	public JSEditorView() {
	
	}
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		GridData gridData = new GridData(GridData.FILL_BOTH);
		parent.setLayout(layout);
		parent.setLayoutData(gridData);
		
		combo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY );
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		combo.addListener(SWT.Selection, new Listener() {
	         @Override
	         public void handleEvent(Event e) {
	        	currentProcess = currentModelBP.get(combo.getSelectionIndex());
            	choosedBP();
	         }
	     });
		
		
		text = new StyledText(parent, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		gridData = new GridData(GridData.FILL_BOTH);
		text.setLayoutData(gridData);
		text.setEditable(false);
		
        
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		
		text.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (currentProcess != null) {
					ScriptsHandler.getProcess(currentProcess.getId()).setCode(text.getText());
				}
			}
		});		
	}
	
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
    	if (part == this) {
    		return;
    	}
    	
        if(selection instanceof IStructuredSelection && !selection.isEmpty()) {
            IStructuredSelection struct = (IStructuredSelection) selection;
            Object object = struct.getFirstElement();
            IArchimateConcept concept = null;
            
            if(object instanceof IArchimateConcept) {
                concept = (IArchimateConcept) object;
            } else if (object instanceof IAdaptable) {
                concept = ((IAdaptable)object).getAdapter(IArchimateConcept.class);
            }
            
            if (concept instanceof BusinessProcess) {
	            boolean update = (currentProcess == null) || !currentProcess.getId().equals(concept.getId());
	            currentProcess = (BusinessProcess) concept;
	            if (update) {
	            	choosedBP();
	            	part.removePropertyListener(propChanged);
	            	part.addPropertyListener(propChanged);
	            	currentModel = currentProcess.getArchimateModel();
	            	if (combo != null) {
	            		updateCombo();
	            	}
	            }
	            
	            return;
            }
        }
        
    }

    private void updateCombo() {
    	currentModelBP = currentModel.getFolder(FolderType.BUSINESS).getElements()
    			.stream()
    			.filter(x -> x instanceof BusinessProcess)
    			.map(x -> (BusinessProcess) x)
    			.collect(Collectors.toList());
		List<String> names = currentModelBP.stream()
							.map(x -> x.getName())
							.collect(Collectors.toList());
		combo.setItems(names.toArray(new String[names.size()]));
		
		int index;
		for (index = 0; index < currentModelBP.size(); index++) {
			if (currentModelBP.get(index).getId().equals(currentProcess.getId())) {
				combo.select(index);
				return;
			}
		}
		combo.select(0);
    }
    
	@Override
	public void setFocus() {
		if (text != null) {
			text.setFocus();
		}
	}
	
	private void choosedBP() {
		String id = currentProcess.getId();
    	if (!ScriptsHandler.processExist(id))
    		ScriptsHandler.addProcess(new HLProcess(currentProcess));
    	
    	text.setEditable(true);
    	text.setEnabled(true);
    	text.setText(ScriptsHandler.getProcess(id).getHLView());
    }
	
	private void unchoosedBP() {
		currentProcess = null;
		text.setEditable(false);
		text.setEnabled(false);
		text.setText("");
		setTitle("...");
	}
	
}
