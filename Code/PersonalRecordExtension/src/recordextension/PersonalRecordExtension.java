package recordextension;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;


import plugin.ActivityPlugin;
import plugin.Plugin;

public class PersonalRecordExtension extends ActivityPlugin {
	public static final String PLUGIN_ID = "Personal Record";
	public final int type = 0;
	
	private List<String> dependencies;
	
	JPanel panel;
	
	public PersonalRecordExtension() {
		super(PLUGIN_ID);
		this.dependencies = new ArrayList<String>();
	}

	@Override
	public void layout(JPanel parentPanel) {
		parentPanel.setLayout(new BorderLayout());
		panel = new PersonalRecordPanel();
		parentPanel.add(panel);
	}

	@Override
	public void start() {
		// Nothing to initialize
	}

	@Override
	public void stop() {
		// Nothing to finalize
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void addDependency(String id) {
		this.dependencies.add(id);
	}

	@Override
	public List<String> getDependencies() {
		return this.dependencies;
	}
	
	@Override
	public int getPluginType()
	{
		return type;
	}

}
