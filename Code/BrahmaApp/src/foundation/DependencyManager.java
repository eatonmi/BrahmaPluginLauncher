package foundation;

import java.util.ArrayList;
import java.util.List;

import plugin.Plugin;

public class DependencyManager {
	
	List<Plugin> availablePlugins;

	public DependencyManager()
	{
		this.availablePlugins = new ArrayList<Plugin>();
	}
	
	public void addPluginToLoadedPlugins(Plugin add)
	{
		add.setDependenciesResolved(this.areDependenciesResolved(add));
		this.availablePlugins.add(add);
		this.recheckAllDependenciesAndGetNewlyResolved();
	}
	
	public void removePluginFromList(String id)
	{
		this.availablePlugins.remove(id);
	}
	
	public boolean areDependenciesResolved(Plugin check)
	{
		boolean retVal = true;
		
		for(String dep : check.getDependencies())
		{
			if(!retVal)
			{
				return retVal;
			}
			
			boolean available = false;
			
			for(Plugin current : this.availablePlugins)
			{
				if(current.getId() == dep && current.dependenciesResolved())
				{
					available = true;
					break;
				}
			}
			
			retVal = available;
		}
		if(!retVal)
		{
			System.out.println("Plugin " + check.getId() + " missing depenencies: ");
		}
		
		return retVal;
	}

	public List<Plugin> recheckAllDependenciesAndGetNewlyResolved() 
	{
		ArrayList<Plugin> retVal = new ArrayList<Plugin>();
		boolean rerun;
		do
		{
			rerun = false;
			for(Plugin recheck : this.availablePlugins)
			{
				if(!recheck.dependenciesResolved())
				{
					System.out.println("rechecking dependencies for: " + recheck.getId());
					printDependencies(recheck);
					boolean resolved = this.areDependenciesResolved(recheck);
					recheck.setDependenciesResolved(resolved);
					if(recheck.dependenciesResolved() && !retVal.contains(recheck))
					{
						System.out.println("Adding " + recheck.getId() + " to list");
						retVal.add(recheck);
						rerun = true;
					}
				}
			}
		} while(rerun);
		
		return retVal;
	}
	
	public void clearDependencies()
	{
		this.availablePlugins.clear();
	}
	
	private void printDependencies(Plugin toPrint)
	{
		System.out.println("Printing Dependencies for " + toPrint.getId() + ":");
		for(String dep : toPrint.getDependencies())
		{
			System.out.println(dep);
		}
	}
}
