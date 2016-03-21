package edu.virginia.uvacluster.internal;

import java.awt.Component;
import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class OpenTask implements Task {
	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final CyApplicationManager appManager;
	
	public OpenTask(CySwingApplication swingApplication, CyServiceRegistrar registrar, CyApplicationManager appManager) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
		this.appManager = appManager;
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		synchronized (this) {
			MyControlPanel mainPanel = null;
			
			// First we must make sure that the app is not already open
			if (!isOpen()) {
				mainPanel = new MyControlPanel(swingApplication, registrar, appManager);

				registrar.registerService(mainPanel, CytoPanelComponent.class, new Properties());
			} else {
				mainPanel = getMainPanel();
			}

			if (mainPanel != null) {
				CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.WEST);;
				int index = cytoPanel.indexOfComponent(mainPanel);
				cytoPanel.setSelectedIndex(index);
			}
		}
		
	}

	public boolean isOpen() {
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.WEST);;
		int count = cytoPanel.getCytoPanelComponentCount();
		
		Component c = null;
		for (int i = 0; i < count; i++) {
			final Component comp = cytoPanel.getComponentAt(i);
			
			if (comp instanceof MyControlPanel)
				c = comp;
		}
		
		if (c!=null) {
			return true;
		} else {
			return false;
		}
	}
	
	public MyControlPanel getMainPanel() {
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.WEST);;
		int count = cytoPanel.getCytoPanelComponentCount();
		MyControlPanel c = null;
		for (int i = 0; i < count; i++) {
			final Component comp = cytoPanel.getComponentAt(i);
			
			if (comp instanceof MyControlPanel)
				c = (MyControlPanel) comp;
		}
		return c;
	}
}
