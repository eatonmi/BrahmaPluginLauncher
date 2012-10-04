package tests;

import java.util.ArrayList;
import java.util.List;

import plugin.Plugin;

public class testPlugin extends Plugin {

	private List<String> dependencies;

	public testPlugin(String id){
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
}
