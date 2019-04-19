/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.example;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import com.archimatetool.editor.model.IModelExporter;
import com.archimatetool.editor.ui.components.ExtendedWizardDialog;
import com.archimatetool.example.window.ExportAsBNAWizard;
import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IArchimateModel;
import com.archimatetool.model.IFolder;
import com.archimatetool.model.IProperty;
import com.archimatetool.model.impl.ArchimateDiagramModel;
import com.archimatetool.model.impl.BusinessRole;

public class MyExporter implements IModelExporter {
    
    public MyExporter() {
    }
        
    @Override
    public void export(IArchimateModel model) throws IOException, IllegalArgumentException {
    	BNAExporter exporter = new BNAExporter(model);
    	ExportAsBNAWizard wizard = new ExportAsBNAWizard(exporter);
    	WizardDialog dialog = new ExtendedWizardDialog(Display.getCurrent().getActiveShell(), new ExportAsBNAWizard(new BNAExporter(model)), "ExportAsBNAWizard");
    	dialog.setPageSize(400, 400);
    	dialog.setTitle(Messages.Wizard_Title);
    	dialog.setMinimumPageSize(400, 400);
        dialog.open();
    }
    
}
