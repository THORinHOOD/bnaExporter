package com.asgar.myexport.js;

public abstract class JSEditorObject {
	protected JSEditorView view;
	
	public JSEditorObject(JSEditorView view) {
		this.view = view;
	}
	
	public abstract void dispose();
}
