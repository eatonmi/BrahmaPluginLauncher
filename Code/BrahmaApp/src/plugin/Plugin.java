package plugin;

import java.util.List;

import javax.swing.JPanel;

public abstract class Plugin {
	private String id;
	private List<String> dependencies;

	public Plugin(String id) {
		this.id = id;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public List<String> getDependencies()
	{
		return this.dependencies;
	}

	// Callback method
	public abstract void layout(JPanel panel);
	public abstract void start();
	public abstract void stop();
}
