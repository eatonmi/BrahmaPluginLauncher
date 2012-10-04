package tests;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import plugin.Plugin;

public class testPlugin extends Plugin {

	private List<String> dependencies;
	public final int type = 3;

	public testPlugin(String id){
		super(id);
		this.dependencies = new ArrayList<String>();
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
