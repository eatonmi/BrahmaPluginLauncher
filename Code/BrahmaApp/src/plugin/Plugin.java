package plugin;

import java.util.List;

public abstract class Plugin {

	private String id;
	private boolean dependenciesResolved;

	public Plugin(String id) {
		this.id = id;
	}
	
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

	//Dependency methods
	public abstract void addDependency(String id);
	public abstract List<String> getDependencies();
	
	//Typing methods
	public abstract int getPluginType();
}
