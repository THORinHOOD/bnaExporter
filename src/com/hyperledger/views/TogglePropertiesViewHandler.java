package com.hyperledger.views;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.archimatetool.editor.ui.services.ViewManager;

/**
 * Класс обработчик нажатия кнопки расширения
 */
public class TogglePropertiesViewHandler extends AbstractHandler {

	/**
	 * Открытие или закрытие окна расширения
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ViewManager.toggleViewPart(HLView.ID, true);
		return null;
	}

}
