package com.asgar.myexport.js;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;

import com.archimatetool.model.FolderType;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateFactory;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IMetadata;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateFactory;
import com.archimatetool.model.impl.BusinessProcess;
import com.hyperledger.export.utils.ScriptsHandler;

public class JSEditorView extends ViewPart implements ISelectionListener {
	
	public static String ID = "com.asgar.myexport.views.JSEditorView";
	
	private IArchimateModel currentModel;
	
	private Composite parent;
	
	private IWorkbenchPart lastPart;
	
	private JSEditorLinkButton linkButton;
	private JSEditor editor;
	private JSEditorCombo combo;
	private JSEditorMenu menu;
	
	private IPropertyListener propChanged = new IPropertyListener() {
		@Override
		public void propertyChanged(Object source, int propId) {
			if (combo != null)
				combo.updateBPList();
		}
	};
	
	public JSEditorView() {
		System.out.println("JSEditorView : constructor");
	}
	@Override
	public void dispose() {
		getSite().getWorkbenchWindow().getSelectionService().removeSelectionListener(this);
		if (lastPart != null)
			lastPart.removePropertyListener(propChanged);
		super.dispose();
	}
	
	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
	
		initLayout(parent);
		menu = new JSEditorMenu(this);
		combo = new JSEditorCombo(this);
		linkButton = new JSEditorLinkButton(this);
		editor = new JSEditor(this);
		
		getSite().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		
		System.out.println("JSEditorView : createPartControl");
	}

	private void initLayout(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		GridData gridData = new GridData(GridData.FILL_BOTH);
		parent.setLayout(layout);
		parent.setLayoutData(gridData);		
	}
	
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
    	if (part == this) {
    		return;
    	}
    	
    	IArchimateModel choosed = part.getAdapter(IArchimateModel.class);
    	
    	if (choosed != null && (choosed != currentModel))  {
    		currentModel = choosed;
    		combo.updateBPList();
    	} else if (choosed != null) {
    		combo.updateBPList();
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
		    	combo.selectBP((BusinessProcess) concept);
		       
		    	if (lastPart != null) {
		    		lastPart.removePropertyListener(propChanged);
		    	}
		    	
			    part.removePropertyListener(propChanged);
			    part.addPropertyListener(propChanged);
			    lastPart = part;
		       
		        editor.updateScript(findScript(combo.getSelectedBP()));
		    }
		}
		
		combo.updateBPList();
    }

    public File findScript(BusinessProcess process) {
    	File script = null;
    	if (currentModel != null && process != null) {
    		Optional<IProperty> prop = currentModel.getMetadata().getEntries()
    			.stream()
    			.filter(x -> x.getKey().equals(process.getId()))
    			.findFirst();
    		
    		if (prop.isPresent())
    			script = new File(prop.get().getValue());
    		else 
    			script = null;
    	}
    	
    	return script;
    }
    
	@Override
	public void setFocus() {
		editor.setFocus();
	}
			
	public void showError(String title, String message) {
		parent.getShell().getDisplay().asyncExec
	    (new Runnable() {
	        public void run() {
	            MessageDialog.openWarning(parent.getShell(), title, message);
	        }
	    });
	}
	
	public Composite getParent() {
		return parent;
	}
	
	public void setScriptTitle(String title) {
		setTitle(title);
	}
	
	public BusinessProcess getSelectedBP() {
		return combo.getSelectedBP();
	}
	
	public JSEditor getEditor() {
		return editor;
	}
	
	public IArchimateModel getCurrentModel() {
		return currentModel;
	}
}
