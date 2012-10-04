package foundation;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

public class ConfigurationManager {
	public Color defaultColor;
	private static ConfigurationManager instance = null;
	final static String PROPERTIES_NAME = "config.properties";
	final static String PLUGIN_FOLDER_PATH = "PlugInFolderPath";
	final static String PLUGIN_FOLDER_DEFAULT = "plugins";
	final static String PLUGIN_BACKGROUND_RED = "BackgroundColor-Red";
	final static String PLUGIN_BACKGROUND_RED_DEFAULT = "225";
	final static String PLUGIN_BACKGROUND_GREEN = "BackgroundColor-Green";
	final static String PLUGIN_BACKGROUND_GREEN_DEFAULT = "225";
	final static String PLUGIN_BACKGROUND_BLUE = "BackgroundColor-Blue";
	final static String PLUGIN_BACKGROUND_BLUE_DEFAULT = "225";
	
	
	private HashMap<String, String> properties;
	private ArrayList<String> propertyNames;
	
	protected ConfigurationManager() {
	      // Exists only to defeat instantiation.
	}
	public static ConfigurationManager getInstance() {
		if(instance == null) {
			instance = new ConfigurationManager();
			Properties propFile = new Properties();
			instance.properties = new HashMap<String, String>();
			instance.propertyNames = new ArrayList<String>();
			instance.defaultColor = new Color(Integer.parseInt(PLUGIN_BACKGROUND_RED_DEFAULT),
					Integer.parseInt(PLUGIN_BACKGROUND_GREEN_DEFAULT),
					Integer.parseInt(PLUGIN_BACKGROUND_BLUE_DEFAULT));
			try 
			{
				propFile.load(new FileInputStream(PROPERTIES_NAME));
			
				instance.properties.put(PLUGIN_FOLDER_PATH, propFile.getProperty(PLUGIN_FOLDER_PATH));
				instance.properties.put(PLUGIN_BACKGROUND_RED, propFile.getProperty(PLUGIN_BACKGROUND_RED));
				instance.properties.put(PLUGIN_BACKGROUND_GREEN, propFile.getProperty(PLUGIN_BACKGROUND_GREEN));
				instance.properties.put(PLUGIN_BACKGROUND_BLUE, propFile.getProperty(PLUGIN_BACKGROUND_BLUE));
				instance.propertyNames.add(PLUGIN_FOLDER_PATH);
				instance.propertyNames.add(PLUGIN_BACKGROUND_RED);
				instance.propertyNames.add(PLUGIN_BACKGROUND_GREEN);
				instance.propertyNames.add(PLUGIN_BACKGROUND_BLUE);
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
	    return instance;
	}
	
	public Path getPluginFolderPath()
	{
		return FileSystems.getDefault().getPath(this.getPropertyValue(PLUGIN_FOLDER_PATH));
	}
	
	public int getBackgroundRed()
	{
		return Integer.parseInt(this.getPropertyValue(PLUGIN_BACKGROUND_RED));
	}
	
	public int getBackgroundGreen()
	{
		return Integer.parseInt(this.getPropertyValue(PLUGIN_BACKGROUND_GREEN));
	}
	
	public int getBackgroundBlue()
	{
		return Integer.parseInt(this.getPropertyValue(PLUGIN_BACKGROUND_BLUE));
	}
	
	public ArrayList<String> getAllPropertyNames()
	{
		return this.propertyNames;
	}
	
	public void setProperty(String propertyName, String propertyValue)
	{
		this.properties.put(propertyName, propertyValue);
	}
	
	public void resetColor()
	{
		instance.properties.put(PLUGIN_BACKGROUND_RED, PLUGIN_BACKGROUND_RED_DEFAULT);
		instance.properties.put(PLUGIN_BACKGROUND_GREEN, PLUGIN_BACKGROUND_GREEN_DEFAULT);
		instance.properties.put(PLUGIN_BACKGROUND_BLUE, PLUGIN_BACKGROUND_BLUE_DEFAULT);
	}
	
	public String getPropertyValue(String propertyName)
	{
		String customVal = this.properties.get(propertyName);
		if (customVal == null)
		{
			switch (propertyName){
			case PLUGIN_FOLDER_PATH:
				customVal = PLUGIN_FOLDER_DEFAULT;
				break;
			case PLUGIN_BACKGROUND_RED:
				customVal = PLUGIN_BACKGROUND_RED_DEFAULT;
				break;
			case PLUGIN_BACKGROUND_BLUE:
				customVal = PLUGIN_BACKGROUND_BLUE_DEFAULT;
				break;
			case PLUGIN_BACKGROUND_GREEN:
				customVal = PLUGIN_BACKGROUND_GREEN_DEFAULT;
				break;
			default:
				break;
			}
		}	
		return customVal;
	}
	
	public void savePropertyFile()
	{
		try {
			Properties propFile = new Properties();
			for (String key : this.properties.keySet()) {
				propFile.setProperty(key, this.properties.get(key));
			}
			propFile.store(new FileOutputStream(PROPERTIES_NAME), null);
		}
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	
	private Boolean isValidColorVal(int v)
	{
		return v >=0 && v<256;
	}
	
}
