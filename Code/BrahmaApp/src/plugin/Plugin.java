package plugin;

import java.util.List;

import javax.swing.JPanel;

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

	// Callback method
	public abstract void layout(JPanel panel);
	public abstract void start();
	public abstract void stop();
	
	//Dependency methods
	public abstract void addDependency(String id);
	public abstract List<String> getDependencies();
}
