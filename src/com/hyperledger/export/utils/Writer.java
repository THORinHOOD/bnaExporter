package com.hyperledger.export.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.json.JSONArray;
import org.json.JSONObject;

import com.archimatetool.model.IArchimateModel;
import com.hyperledger.export.Messages;
import com.hyperledger.export.models.HLModel;
import com.hyperledger.export.models.HLObject;
import com.hyperledger.export.models.Transaction;
import com.hyperledger.export.rules.HLPermRule;

/**
 * Класс для работы с файлами
 */
public class Writer {
	
	//Расширение артефакта
    private String MY_EXTENSION = ".bna";
    //Regexp для артефактов
    private String MY_EXTENSION_WILDCARD = "*.bna"; 
    
    //Поток вывода
    private FileOutputStream fos;
    //Поток вывода для архива
    private ZipOutputStream zipOut;
    
    // Символ сепаратора системы
    private String ps = File.separatorChar + "";
    
    //Дополнительные данные сети
	private Data data;
	//Модель, которую требуется экспортировать
	private IArchimateModel model;
	
	public Writer(IArchimateModel model, Data data) {
		this.data = data;
		this.model = model;
	}
	
	/**
	 * Запись README.md файла
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void writeReadme() 
			throws FileNotFoundException, IOException {
		File file = new File("README.md");
		
		if (file.exists())
			file.delete();
		
		if (file.createNewFile()) {
			ArrayList<String> lines = new ArrayList<String>();
			lines.add("# " + data.getStringValue(Data.BUSINESS_NETWORK_NAME) + "\n");
			lines.add(data.getStringValue(Data.DESCRIPTION));
			writeFile(file, file.getName(), lines);
		}
		
		if (file.exists())
			file.delete();
	}
	
	/**
	 * Запись package.json файла
	 * @throws IOException
	 */
	public void writePackageJSON() throws IOException {
		File file = new File("package.json");
		
		if (file.exists())
			file.delete();
		
		if (file.createNewFile()) {
			JSONObject composer = new JSONObject()
									.put("composer", "^0.19.20");
			JSONObject scripts = new JSONObject()
									.put("prepublish", "mkdirp ./dist && composer archive create --sourceType dir --sourceName . -a ./dist/tutorial-network.bna")
									.put("pretest", "npm run lint")
									.put("lint", "eslint .")
									.put("test", "nyc mocha -t 0 test/*.js && cucumber-js");
			JSONObject devDependencies = new JSONObject()
									.put("composer-admin","^0.19.20")
									.put("composer-cli","^0.19.20")
									.put("composer-client","^0.19.20")
									.put("composer-common","^0.19.20")
									.put("composer-connector-embedded","^0.19.20")
									.put("composer-cucumber-steps","^0.19.20")
									.put("chai","latest")
									.put("chai-as-promised","latest")
									.put("cucumber","^2.2.0")
									.put("eslint","latest")
									.put("nyc","latest")
									.put("mkdirp","latest")
									.put("mocha","latest");
			
			JSONObject packageJson= new JSONObject()
									.put("engines", composer)
									.put("name", data.getStringValue(Data.BUSINESS_NETWORK_NAME))
									.put("version", "0.0.1")
									.put("description", data.getStringValue(Data.DESCRIPTION))
									.put("scripts", scripts)
									.put("keywords", new JSONArray().put("composer").put("composer-network"))
									.put("author", data.getStringValue(Data.AUTHOR_NAME))
									.put("email", data.getStringValue(Data.AUTHOR_EMAIL))
									.put("license", data.getStringValue(Data.LICENSE))
									.put("devDependencies", devDependencies);
						
			ArrayList<String> lines = new ArrayList<String>();
			lines.add(packageJson.toString());
			writeFile(file, file.getName(), lines);
		}
		
		if (file.exists())
			file.delete();
	}
	
	/**
	 * Запись прав доступа (файла premissions.acl)
	 * @param rules
	 * @throws IOException
	 */
	public void writePermissions(List<HLPermRule> rules) throws IOException {
		File file = new File("permissions.acl");
		
		if (file.exists())
			file.delete();
		
		if (file.createNewFile()) {
			ArrayList<String> lines = new ArrayList<String>();
			for (HLPermRule rule : rules)
				lines.add(rule.getHLView());
			writeFile(file, file.getName(), lines);
		}
		
		if (file.exists())
			file.delete();
	}
	
	/**
	 * Запись скриптов в файл logic.js
	 * @param transactions
	 * @throws IOException
	 */
	public void writeScripts(List<Transaction> transactions) throws IOException {
		File dir = new File("lib");
		deleteDir(dir);
		if (dir.mkdir()) {
			File file = new File(dir, "logic.js");
			
			if (file.exists())
				file.delete();
			
			if (file.createNewFile()) {
				writeFile(file, dir.getName() + ps + file.getName(), ScriptsHandler.getTextFromScripts(transactions));
			}
		}
		deleteDir(dir);
	}
	
	/**
	 * Запись моделей в файл <namespace>.cto
	 * @param models
	 * @throws IOException
	 * @throws ParseException
	 */
	public void writeModels(Collection<HLModel> models) throws IOException, ParseException {
    	File dir = new File("models");
    	deleteDir(dir);
    	if (dir.mkdir()) {
			File file = new File(dir, data.getStringValue(Data.NAMESPACE) + ".cto");
			
			if (file.exists())
				file.delete();
			
			if (file.createNewFile()) {
				HLModel[] sortedModels = new HLModel[models.size()];
				sortedModels = models.toArray(sortedModels);
				Arrays.sort(sortedModels, (fmodel, smodel) -> ((HLModel)fmodel).getRank() - ((HLModel) smodel).getRank());
				
				ArrayList<String> lines = new ArrayList<String>();
				lines.add("\n" + "namespace " + data.getStringValue(Data.NAMESPACE) + "\n");
				for (HLObject obj : sortedModels)
	    			lines.add(obj.getHLView() + "\n");
				
				writeFile(file, dir.getName() + ps + file.getName(), lines);
			}
    	}
    	
    	deleteDir(dir);
    }
	
	/**
	 * Запись файла
	 * @param file
	 * @param pathInZip
	 * @param lines
	 * @throws IOException
	 */
	private void writeFile(File file, String pathInZip, List<String> lines) throws IOException {
		byte[] buffer = new byte[1024];
		
		try (PrintWriter pw = new PrintWriter(file)) {
			for (String line : lines)
				pw.println(line);
		}
		
		try (FileInputStream fis = new FileInputStream(pathInZip)) {
			zipOut.putNextEntry(new ZipEntry(pathInZip));
			int length;
			while ((length = fis.read(buffer)) > 0)
				zipOut.write(buffer, 0, length);
			zipOut.closeEntry();
		}
	}
	
	/**
	 * Удаление папки
	 * @param dir
	 * @return
	 */
    private static boolean deleteDir(File dir) {
    	if (!dir.exists() || !dir.isDirectory())
    		return dir.delete();
    	
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                if (!deleteDir(new File(dir, child)))
                    return false;
            }
        }

        return dir.delete(); // The directory is empty now and can be deleted.
    }
    
    /**
     * Начало записи
     * @throws FileNotFoundException
     */
    public void start() throws FileNotFoundException {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.SAVE);
        dialog.setText(Messages.MyExporter_0);
        dialog.setFilterExtensions(new String[] { MY_EXTENSION_WILDCARD, "*.*" } );
        String path = dialog.open();
        if(path == null) {
            return; //null;
        }
        
        // Only Windows adds the extension by default
        if(dialog.getFilterIndex() == 0 && !path.endsWith(MY_EXTENSION)) {
            path += MY_EXTENSION;
        }
        
        fos = new FileOutputStream(path);
        zipOut = new ZipOutputStream(fos);
        
        return;
    }
    
    /**
     * Завершение записи
     */
    public void close() {
    	try {
	    	zipOut.close();
	    	fos.close();
    	} catch(IOException ex) {
    	}
    }
}
