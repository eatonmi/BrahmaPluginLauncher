package plugin;

import java.util.List;

public abstract class Plugin {

	private String id;
	private boolean dependenciesResolved;
	private int pluginType;
	
	public Plugin() {}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public boolean dependenciesResolved()
	{
		return this.dependenciesResolved;
	}
	
	public void setDependenciesResolved(boolean areResolved)
	{
		this.dependenciesResolved = areResolved;
	}

	public int getPluginType()
	{
		return this.pluginType;
	}
	
	public void setPluginType(int type)
	{
		this.pluginType = type;
	}
	
	//Dependency methods
	public abstract void addDependency(String id);
	public abstract List<String> getDependencies();
	
}
