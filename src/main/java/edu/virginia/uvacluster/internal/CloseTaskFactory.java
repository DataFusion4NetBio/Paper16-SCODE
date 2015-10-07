package edu.virginia.uvacluster.internal;

import java.awt.Component;
import java.util.Set;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedEvent;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class CloseTaskFactory implements TaskFactory {

	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	
	public CloseTaskFactory(final CySwingApplication swingApplication, final CyServiceRegistrar registrar) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		return new TaskIterator(new CloseTask(swingApplication, registrar));
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		if (!isOpen()) {
			return false;
		} else {
			return true;
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
