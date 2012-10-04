package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import foundation.DependencyManager;
import foundation.DependencyRetreiver;

import plugin.Plugin;

public class dependencyTests {
	
	List<Plugin> basePlugins;
	Plugin testPlugin;
	DependencyManager depManager;
	
	@Before
	public void setUp()
	{
		DependencyRetreiver retreiver = new DependencyRetreiver();
		this.depManager = DependencyRetreiver.getManager();
		this.basePlugins = new ArrayList<Plugin>();
		this.depManager.addPluginToLoadedPlugins(new testPlugin("Test1"));
		this.depManager.addPluginToLoadedPlugins(new testPlugin("Test2"));
		this.depManager.addPluginToLoadedPlugins(new testPlugin("Test3"));
		this.depManager.addPluginToLoadedPlugins(new testPlugin("Test4"));
		this.testPlugin = new testPlugin("TestPlugin");
	}
	
	@After
	public void tearDown()
	{
		this.depManager.clearDependencies();
	}
	
	@Test
	public void testDependencySuccess() {
		this.testPlugin.addDependency("Test1");
		this.testPlugin.addDependency("Test2");
		Assert.assertTrue(this.depManager.areDependenciesResolved(this.testPlugin));
	}
	
	@Test
	public void testDependencyFail() {
		this.testPlugin.addDependency("ObviouslyNotPresent");
		Assert.assertFalse(this.depManager.areDependenciesResolved(this.testPlugin));
	}
	
	@Test
	public void testInitialFailSubsequentSuccess() {
		this.testPlugin.addDependency("InitiallyNotPresent");
		Assert.assertFalse(this.depManager.areDependenciesResolved(this.testPlugin));
		Plugin fixPlugin = new testPlugin("InitiallyNotPresent");
		this.depManager.addPluginToLoadedPlugins(fixPlugin);
		Assert.assertTrue(this.depManager.areDependenciesResolved(this.testPlugin));
	}
	
	@Test
	public void testChainDependency() {
		this.testPlugin.addDependency("ChainDependentMiddle");
		Plugin chainDependentMiddle = new testPlugin("ChainDependentMiddle");
		chainDependentMiddle.addDependency("ChainDependentTop");
		Plugin chainDependentTop = new testPlugin("ChainDependentTop");
		this.depManager.addPluginToLoadedPlugins(chainDependentMiddle);
		this.depManager.addPluginToLoadedPlugins(this.testPlugin);
		Assert.assertFalse(this.depManager.areDependenciesResolved(this.testPlugin));
		this.depManager.addPluginToLoadedPlugins(chainDependentTop);
		Assert.assertTrue(this.depManager.areDependenciesResolved(this.testPlugin));
	}

}
