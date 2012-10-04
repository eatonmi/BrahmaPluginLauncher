package bouncingball;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import plugin.ActivityPlugin;

/**
 * An extension plugin.
 * 
 * @author Chandan R. Rupakheti (rupakhet@rose-hulman.edu)
 *
 */
public class BouncingBallPlugin extends ActivityPlugin {
	public static final String PLUGIN_ID = "Bouncing Ball";
	
	private List<String> dependencies;
	
	private BBPanel panel;
	
	public BouncingBallPlugin() {
		this.dependencies = new ArrayList<String>();
	}

	@Override
	public void layout(JPanel parentPanel) {
		parentPanel.setLayout(new BorderLayout());
		this.panel = new BBPanel();
		parentPanel.add(panel);
	}

	@Override
	public void start() {
		// Not much to do here
	}

	@Override
	public void stop() {
		// Not much to do here
	}
	
	// For now we need to declare dummy main method
	// to include in manifest file
	public static void main(String[] args) {
		
	}

	@Override
	public void addDependency(String id) {
		this.dependencies.add(id);
	}

	@Override
	public List<String> getDependencies() {
		return this.dependencies;
	}
	
}
