package edu.virginia.uvacluster.internal;

import java.awt.Component;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class OpenTaskFactory implements TaskFactory{
	
	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final CyApplicationManager appManager;
	
	public OpenTaskFactory(final CySwingApplication swingApplication, final CyServiceRegistrar registrar, final CyApplicationManager appManager) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
		this.appManager = appManager;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new OpenTask(swingApplication, registrar, appManager));
	}

	@Override
	public boolean isReady() {
		if (!isOpen()) {
			return true;
		} else {
			return false;
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

}
