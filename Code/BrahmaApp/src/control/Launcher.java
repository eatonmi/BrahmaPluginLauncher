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

import plugin.Plugin;

import foundation.DependencyManager;
import foundation.DependencyRetreiver;
import foundation.WatchDir;

public class Launcher implements Runnable {
	private PluginCore core;
	private WatchDir watchDir;
	private HashMap<Path, Plugin> pathToPlugin;

	public Launcher(PluginCore core) throws IOException {
		this.core = core;
		this.pathToPlugin = new HashMap<Path, Plugin>();
		watchDir = new WatchDir(this, FileSystems.getDefault().getPath("plugins"), false);
        @SuppressWarnings("unused")
		DependencyRetreiver baseRet =  new DependencyRetreiver();
	}

	@Override
	public void run() {
		// First load existing plugins if any
		try {
			Path pluginDir = FileSystems.getDefault().getPath("plugins");
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
        String className = mainAttribs.getValue("Plugin-Class");
        URL[] urls = new URL[]{bundlePath.toUri().toURL()};
        @SuppressWarnings("resource")
		ClassLoader classLoader = new URLClassLoader(urls);
        Class<?> pluginClass = classLoader.loadClass(className);
        
        // Create a new instance of the plugin class and add to the Dependency Manager
        Plugin plugin = (Plugin)pluginClass.newInstance();
        DependencyManager depManager = DependencyRetreiver.getManager();
        
        
        depManager.addPluginToLoadedPlugins(plugin);
        List<Plugin> toLoad = removeAllLaunchedPlugins(depManager.getResolvedPlugins()); 
        // Load any plugins whose dependencies are now resolved (including the freshly loaded one)
        
        for(Plugin load : toLoad)
        {
        	this.core.addPlugin(load);
        }
        this.pathToPlugin.put(bundlePath, plugin);

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
}
