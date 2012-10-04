package control;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import plugin.IPluginDetails;
import plugin.Plugin;
import plugin.PluginConstants;

import foundation.DependencyManager;
import foundation.ConfigurationManager;
import foundation.DependencyRetreiver;
import foundation.WatchDir;

public class Launcher implements Runnable {
	private PluginCore core;
	private WatchDir watchDir;
	private HashMap<Path, Plugin> pathToPlugin;
	private DependencyManager depManager;
	private ConfigurationManager confManager;

	public Launcher(PluginCore core) throws IOException {
		this.core = core;
		this.pathToPlugin = new HashMap<Path, Plugin>();
		watchDir = new WatchDir(this, FileSystems.getDefault().getPath("plugins"), false);
		this.depManager = new DependencyManager();
		this.confManager = ConfigurationManager.getInstance();
		@SuppressWarnings("unused")
		DependencyRetreiver baseRet =  new DependencyRetreiver();
	}

	@Override
	public void run() {
		// First load existing plugins if any
		try {
			Path pluginDir = FileSystems.getDefault().getPath(this.confManager.getPluginPath());
			File pluginFolder = pluginDir.toFile();
			File[] files = pluginFolder.listFiles();
			if(files != null) {
				for(File f : files) {
					this.loadBundle(f.toPath());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		// Listen for newly added plugins
		watchDir.processEvents();
	}

	public void loadBundle(Path bundlePath) throws Exception {
		// Get hold of the jar file
		File jarBundle = bundlePath.toFile();
		JarFile jarFile = new JarFile(jarBundle);
		
		// Get the manifest file in the jar file
		Manifest mf = jarFile.getManifest();
        Attributes mainAttribs = mf.getMainAttributes();
        
        // Get hold of the Plugin-Class attribute and load the class
        URL[] urls = new URL[]{bundlePath.toUri().toURL()};
        List<Plugin> imported = this.getAllPresentPlugins(mainAttribs, urls);
       
        for(Plugin p : imported)
        {
        	depManager.addPluginToLoadedPlugins(p);
        }
        List<Plugin> toLoad = removeAllLaunchedPlugins(depManager.getResolvedPlugins()); 
        // Load any plugins whose dependencies are now resolved (including the freshly loaded one)
        
        for(Plugin load : toLoad)
        {
        	this.core.addPlugin(load);
        }
        
        for(Plugin p : imported)
        {
        	this.pathToPlugin.put(bundlePath, p);
        }

        // Release the jar resources
        jarFile.close();
	}
	
	public void unloadBundle(Path bundlePath) {
		Plugin plugin = this.pathToPlugin.remove(bundlePath);
		if(plugin != null) {
			this.core.removePlugin(plugin.getId());
			DependencyRetreiver.getManager().removePluginFromList(plugin.getId());
		}
	}
	
	private List<Plugin> removeAllLaunchedPlugins(List<Plugin> checkAgainst)
	{
		List<Plugin> retVal = new ArrayList<Plugin>();
		for(Plugin check : checkAgainst)
		{
			if(!this.core.getPluginIds().contains(check.getId()))
				retVal.add(check);
		}
		return retVal;
	}
	
	private List<Plugin> getAllPresentPlugins(Attributes attribs, URL[] urls)
	{
		List<Plugin> retVal = new ArrayList<Plugin>();
		
        String activityName = attribs.getValue("Activity-Class");
        String activityDetails = attribs.getValue("Activity-Details");
        String configName = attribs.getValue("Config-Class");
        String configDetails = attribs.getValue("Config-Details");
        String panelName = attribs.getValue("Panel-Class");
        String panelDetails = attribs.getValue("Panel-Details");
        
        System.out.println("AN: " + activityName);
        System.out.println("AD: " + activityDetails);
        System.out.println("CN: " + configName);
        System.out.println("CD: " + configDetails);
        System.out.println("PN: " + panelName);
        System.out.println("PD: " + panelDetails);
        
        try
        {
        	@SuppressWarnings("resource")
			ClassLoader classLoader = new URLClassLoader(urls);
	        if(activityName != null && activityDetails != null)
	        {
	        	Class<?> activityClass = classLoader.loadClass(activityName);
	        	Plugin p = ((Plugin)activityClass.newInstance());
	        	Class<?> activityDetailClass = classLoader.loadClass(activityDetails);
	        	IPluginDetails activityDetailCast = (IPluginDetails)activityDetailClass.newInstance();
	        	p.setPluginType(PluginConstants.ACTIVITY_PLUGIN);
	        	p.setId(activityDetailCast.getId());
	        	retVal.add(p);
	        }
	        if(configName != null && configDetails != null)
	        {
	        	Class<?> configClass = classLoader.loadClass(configName);
	        	Class<?> configDetailClass = classLoader.loadClass(configDetails);
	        	IPluginDetails configDetailCast = (IPluginDetails)configDetailClass.newInstance();
	        	Plugin p = ((Plugin)configClass.newInstance());
	        	p.setPluginType(PluginConstants.CONFIGURATION_PLUGIN);
	        	p.setId(configDetailCast.getId());
	        	retVal.add((Plugin)configClass.newInstance());
	        }
	        if(panelName != null && panelDetails != null)
	        {
	        	Class<?> panelClass = classLoader.loadClass(panelName);
	        	Plugin p = ((Plugin)panelClass.newInstance());
	        	Class<?> panelDetailClass = classLoader.loadClass(panelDetails);
	        	IPluginDetails panelDetailCast = (IPluginDetails)panelDetailClass.newInstance();
	        	p.setPluginType(PluginConstants.PANEL_PLUGIN);
	        	p.setId(panelDetailCast.getId());
	        	retVal.add((Plugin)panelClass.newInstance());
	        }
        }
        catch(Exception e)
        {
        	System.out.print(e.getMessage());
        }
        
        return retVal;
	}
}
