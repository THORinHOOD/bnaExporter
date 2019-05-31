package com.hyperledger.export.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.archimatetool.model.IArchimateConcept;
import com.archimatetool.model.IProperty;
import com.hyperledger.export.models.Transaction;

/**
 * Класс для работы со скриптами моделей
 */
public class ScriptsHandler {
	
	/**
	 * Получение текстового представления скрипта
	 * @param script
	 * @return
	 */
	public static Optional<String> scriptToText(File script) {
		try(BufferedReader reader = new BufferedReader(new FileReader(script))) {
			String line;
			String res = "";
			while ((line = reader.readLine()) != null)
				res += line + "\n";
			if (res.equals("")) {
				return Optional.empty();
			}
			return Optional.of(res + "\n");
		} catch(IOException exception) {
			return Optional.empty();
		}
	}
	
	/**
	 * Получить текстовое представление скриптов из транзакций
	 * @param transactions
	 * @return
	 * @throws IOException
	 */
	public static List<String> getTextFromScripts(List<Transaction> transactions) throws IOException {
		return transactions
				.stream()
				.flatMap(tx -> getScripts(tx).stream())
				.filter(script -> script.exists())
				.map(script -> scriptToText(script))
				.filter(script -> script.isPresent())
				.map(script -> script.get())
				.collect(Collectors.toList());
	}
	
	/**
	 * Получить скрипты из транзакции
	 * @param tx
	 * @return
	 */
	private static List<File> getScripts(Transaction tx) {
		return tx.getConcepts()
					.stream()
					.map(concept -> getScriptsFromConcept(concept))
					.flatMap(List::stream)
					.map(property -> getScript(property))
					.filter(x -> x.isPresent())
					.map(x -> x.get())
					.collect(Collectors.toList());
	}
	
	/**
	 * Получить скрипт из свойства
	 * @param property
	 * @return
	 */
	private static Optional<File> getScript(IProperty property) {
		String path = property.getValue();
		File file = new File(path);
		if (file.exists()) {
			return Optional.of(file);
		} else {
			return Optional.empty();
		}
	}
	
	/**
	 * Получить скрипты из концепции
	 * @param concept
	 * @return
	 */
	private static List<IProperty> getScriptsFromConcept(IArchimateConcept concept) {
		return concept.getProperties().stream().filter(prop -> prop.getKey().trim().toUpperCase().equals("SCRIPT")).collect(Collectors.toList());
	}

}
