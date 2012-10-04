package plugin;

import javax.swing.JComponent;

public interface IPlugin {
	public JComponent getPluginUI();
	public String getId();
	public void setId(String id);
}
