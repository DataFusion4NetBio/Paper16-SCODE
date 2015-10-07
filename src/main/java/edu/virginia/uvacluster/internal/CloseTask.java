package edu.virginia.uvacluster.internal;

import java.awt.Component;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class CloseTask implements Task{

	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	
	public CloseTask(CySwingApplication swingApplication, CyServiceRegistrar registrar) {
		this.swingApplication = swingApplication;
		this.registrar = registrar;
	}
	
	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		// TODO Auto-generated method stub
		MyControlPanel mainPanel = getMainPanel();
		if (mainPanel != null) {
			registrar.unregisterService(mainPanel, CytoPanelComponent.class);
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
