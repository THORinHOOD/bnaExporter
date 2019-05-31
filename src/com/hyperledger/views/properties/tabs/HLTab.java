package com.hyperledger.views.properties.tabs;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;

import com.archimatetool.model.IArchimateConcept;

/**
 * Класс предок вкладки
 */
public abstract class HLTab {
	// Вкладка
	private CTabItem tab;
	// Заголовок
	private String label;
	// Папка, в которой находится вкладка
	private CTabFolder folder;
	// Открыта ли вкладка
	private Boolean isOpened;
	
	public HLTab(CTabFolder folder, String label) {
		this.folder = folder;
		this.label = label;
		isOpened = false;
	}
			
	/**
	 * Открытие вкладки
	 * @param control
	 */
	protected void openTab(Composite control) {
		if (!isOpened) {
			tab = new CTabItem(folder, SWT.NONE);
			setLabel(this.label);
			tab.setControl(control);
			tab.setShowClose(true);
			isOpened = true;
		}
	}
	
	/**
	 * Закрытие вкладки
	 */
	protected void closeTab() {
		if (isOpened) {
			tab.dispose();
			isOpened = false;
		}
	}
	
	/**
	 * Установление текста заголовка
	 * @param label
	 */
	public void setLabel(String label) {
		if (label.trim().equals("")) {
			label = "(Unknown)";
		}
		this.label = label;
		if (tab != null && !tab.isDisposed())
			tab.setText(label);
	}
	
	/**
	 * Получить текст заголовка
	 * @return
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Получить вкладку
	 * @return
	 */
	public CTabItem getTab() {
		return tab;
	}
	
	/**
	 * Получить папку
	 * @return
	 */
	public CTabFolder getFolder() {
		return folder;
	}
	
	/**
	 * Уничтожение вкладки
	 */
	public void dispose() {
		tab.dispose();
	}
	
	/**
	 * Показать пользователю сообщение об ошибке
	 * @param title
	 * @param message
	 */
	public void showError(String title, String message) {
		folder.getDisplay().asyncExec
	    (new Runnable() {
	        public void run() {
	            MessageDialog.openWarning(folder.getShell(), title, message);
	        }
	    });
	}
}
