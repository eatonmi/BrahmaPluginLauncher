package plugin;

import javax.swing.JPanel;

public abstract class ActivityPlugin extends Plugin {
	
	public ActivityPlugin(String id)
	{
		super(id);
	}
	
	// Callback method
	public abstract void layout(JPanel panel);
	public abstract void start();
	public abstract void stop();
}
