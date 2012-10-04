package foundation;

import java.util.ArrayList;
import java.util.List;

import plugin.Plugin;

public class DependencyManager {
	
	List<String> loadedPluginIds;

	public DependencyManager()
	{
		this.loadedPluginIds = new ArrayList<String>();
	}
	
	public void addPluginToLoadedPlugins(Plugin add)
	{
		this.loadedPluginIds.add(add.getId());
	}
	
	public void removePluginFromList(String id)
	{
		this.loadedPluginIds.remove(id);
	}
	
	public boolean areDependenciesResolved(Plugin check)
	{
		for(String dep : check.getDependencies())
		{
			if(!this.loadedPluginIds.contains(dep))
				return false;
		}
		
		return true;
	}
	
	
}
