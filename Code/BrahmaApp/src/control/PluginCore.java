package control;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import plugin.ActivityPlugin;
import foundation.ConfigurationManager;
import foundation.DependencyRetreiver;

import plugin.Plugin;
import plugin.PluginConstants;


public class PluginCore extends LayoutAccesser {
	final static String SETTINGS_BUTTON_LABEL = "Settings";
	private ConfigurationManager confManager;
	private Thread currentThread;
	ArrayList<JTextField> textFields;
	
	// GUI Widgets that we will need
	private JFrame frame;
	private JFrame settingsFrame;
	private JPanel contentPanel;
	private JPanel settingsPanel;
	private JLabel bottomLabel;
	@SuppressWarnings("rawtypes")
	private JList sideList;
	private DefaultListModel<String> listModel;
	private JPanel centerEnvelope;
	private JPanel bottom;
	JScrollPane scrollPane;
	
	// For holding registered plugin
	private HashMap<String, Plugin> idToPlugin;
	private ActivityPlugin currentPlugin;
	
	// Plugin manager
	Launcher pluginManager;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public PluginCore() {
		this.textFields = new ArrayList<JTextField>();
		this.confManager = ConfigurationManager.getInstance();
		idToPlugin = new HashMap<String, Plugin>();
		this.setupMainFrame();
		this.setupSettingsFrame();
		
		// Start the plugin manager now that the core is ready
		this.startThread();
	}
	
	private void startThread()
	{
		try {
			this.pluginManager = new Launcher(this);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(this.currentThread != null){
			this.currentThread.interrupt();
		}
		this.currentThread = new Thread(this.pluginManager);
		this.currentThread.start();
	}
	
	public void start() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	public void stop() {
		EventQueue.invokeLater(new Runnable() {
			public void run()
			{
				frame.setVisible(false);
			}
		});
	}
	
	public void removeAllPlugins()
	{
		for(int i=this.listModel.size()-1; i>=0; i--)
		{
			this.removePlugin(this.listModel.get(i));
		}
	}
	
	private void setupMainFrame()
	{
		// Lets create the elements that we will need
		Color color = this.confManager.getBackgroundColor();
		frame = new JFrame("Pluggable Board Application");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPanel = (JPanel)frame.getContentPane();
		contentPanel.setPreferredSize(new Dimension(700, 500));
		bottomLabel = new JLabel("No plugins registered yet!");
		
		listModel = new DefaultListModel<String>();
		sideList = new JList(listModel);
		sideList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sideList.setLayoutOrientation(JList.VERTICAL);
		sideList.setBackground(color);
		scrollPane = new JScrollPane(sideList);
		scrollPane.setPreferredSize(new Dimension(100, 50));
		scrollPane.setBackground(color);
		
		// Create center display area
		centerEnvelope = new JPanel(new BorderLayout());
		centerEnvelope.setBorder(BorderFactory.createLineBorder(Color.black, 5));
		
		bottom = new JPanel(new BorderLayout());
		bottom.setBackground(color);
		JButton btnSettings = new JButton(SETTINGS_BUTTON_LABEL);
		
		bottom.add(bottomLabel, BorderLayout.WEST);
		bottom.add(btnSettings, BorderLayout.EAST);
		
		// Lets lay them out, contentPane by default has BorderLayout as its layout manager
		contentPanel.add(centerEnvelope, BorderLayout.CENTER);
		contentPanel.add(scrollPane, BorderLayout.EAST);
		contentPanel.add(bottom, BorderLayout.SOUTH);
		
		// Add action listeners
		btnSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingsFrame.pack();
				settingsFrame.setVisible(true);
			}
		});
		
		sideList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// If the list is still updating, return
				if(e.getValueIsAdjusting())
					return;
				
				// List has finalized selection, let's process further
				int index = sideList.getSelectedIndex();
				String id = listModel.elementAt(index);
				ActivityPlugin plugin = (ActivityPlugin)idToPlugin.get(id);
				
				if(plugin == null || plugin.equals(currentPlugin))
					return;
				
				// Stop previously running plugin
				if(currentPlugin != null)
					currentPlugin.stop();
				
				// The newly selected plugin is our current plugin
				currentPlugin = plugin;
				
				// Clear previous working area
				centerEnvelope.removeAll();
				
				// Create new working area
				JPanel centerPanel = new JPanel();
				centerEnvelope.add(centerPanel, BorderLayout.CENTER); 
				
				// Ask plugin to layout the working area
				currentPlugin.layout(centerPanel);
				contentPanel.revalidate();
				contentPanel.repaint();
				
				// Start the plugin
				currentPlugin.start();
				
				bottomLabel.setText("The " + currentPlugin.getId() + " is running!");
			}
		});
	}
	
	private void setupSettingsFrame()
	{
		ArrayList<String> names = this.confManager.getAllPropertyNames();
		settingsFrame = new JFrame("Settings");
		settingsPanel = (JPanel)settingsFrame.getContentPane();
		settingsPanel.setPreferredSize(new Dimension(350,names.size()*60));
		settingsPanel.setBorder(new EmptyBorder(10, 10, 10, 10) );
		GridLayout layoutTop = new GridLayout(names.size()+1,2);
		settingsPanel.setLayout(layoutTop);	
		
		for(String propertyName:this.confManager.getAllPropertyNames()){
			JLabel label = new JLabel(propertyName);
			JTextField text = new JTextField(this.confManager.getPropertyValue(propertyName));
			this.textFields.add(text);
			settingsPanel.add(label);
			settingsPanel.add(text);
		}
		
		JButton btnSaveSettings = new JButton("Save");
		settingsPanel.add(btnSaveSettings);
		
		// Add action listener
		btnSaveSettings.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveProperties();
			}
		});
	}
	
	private void saveProperties()
	{
		for(int i=0; i<this.textFields.size(); i++)
		{
			String name = this.confManager.getAllPropertyNames().get(i);
			String value = this.textFields.get(i).getText();
			this.confManager.setProperty(name, value);
		}
		this.confManager.savePropertyFile();
		settingsFrame.setVisible(false);
		this.reloadPlugins();
		this.changeBackground();
	}
	
	public void addPlugin(Plugin plugin) {
		this.idToPlugin.put(plugin.getId(), plugin);
		
		switch(plugin.getPluginType())
		{
		case PluginConstants.ACTIVITY_PLUGIN:
			this.listModel.addElement(plugin.getId());
		}
		
		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently added!");
	}
	
	public void removePlugin(String id) {
		Plugin plugin = this.idToPlugin.remove(id);
		this.listModel.removeElement(id);
		
		// Stop the plugin if it is still running
		switch(plugin.getPluginType())
		{
		case PluginConstants.ACTIVITY_PLUGIN:
			((ActivityPlugin)plugin).stop();
		}

		this.bottomLabel.setText("The " + plugin.getId() + " plugin has been recently removed!");
	}
	
	private void reloadPlugins()
	{
		DependencyRetreiver.getManager().clearDependencies();
		this.removeAllPlugins();
		this.startThread();
	}
	
	private void changeBackground()
	{
		try{
			Color color = this.confManager.getBackgroundColor();
			bottom.setBackground(color);
			scrollPane.setBackground(color);
			sideList.setBackground(color);
		}catch(Exception e)
		{
			bottom.setBackground(this.confManager.defaultColor);
			scrollPane.setBackground(this.confManager.defaultColor);
			sideList.setBackground(this.confManager.defaultColor);
			for (int i = 1; i<this.textFields.size(); i++)
			{
				JTextField text = this.textFields.get(i);
				text.setText("255");
			}
			this.confManager.resetColor();
			this.confManager.savePropertyFile();
			JOptionPane.showMessageDialog(frame,
				    "Not a valid color, reset to default",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public Set<String> getPluginIds()
	{
		return this.idToPlugin.keySet();
	}

	@Override
	public JScrollPane getRightPanel() {
		return this.scrollPane;
	}

	@Override
	public void setRightPanel(JScrollPane rightPanel) {
		this.scrollPane = rightPanel;
	}

	@Override
	public JPanel getBottomPanel() {
		return this.bottom;
	}

	@Override
	public void setBottomPanel(JPanel bottomPanel) {
		this.bottom = bottomPanel;
	}
}
