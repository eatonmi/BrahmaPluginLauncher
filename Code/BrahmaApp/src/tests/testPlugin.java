package tests;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import plugin.Plugin;

public class testPlugin extends Plugin {

	private List<String> dependencies;

	public testPlugin(String id){
		super(id);
		this.dependencies = new ArrayList<String>();
	}
	
	@Override
	public void layout(JPanel panel) {
		
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
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

}
