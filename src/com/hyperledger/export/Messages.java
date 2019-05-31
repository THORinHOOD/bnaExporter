package com.hyperledger.export;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	
	private static final String BUNDLE_NAME = "com.archimatetool.example.messages";
    
    public static String MyExporter_0;
    public static String MyExporter_1;
    public static String Wizard_Title; 
    
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
