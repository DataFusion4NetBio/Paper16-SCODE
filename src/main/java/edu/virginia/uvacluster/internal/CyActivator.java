package edu.virginia.uvacluster.internal;

import java.util.Properties;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.NetworkAboutToBeDestroyedListener;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.view.model.CyNetworkViewFactory;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {
	public static CyNetworkManager networkManager;
	public static CyNetworkFactory networkFactory;
	public static CyNetworkNaming networkNaming;
	public static CyNetworkViewFactory networkViewFactory;
	public static CyNetworkViewManager networkViewManager;
	private CyServiceRegistrar serviceRegistrar;
	
	@Override
	public void start(BundleContext context) throws Exception {
		networkFactory = getService(context, CyNetworkFactory.class);
		networkManager = getService(context, CyNetworkManager.class);
		networkNaming = getService(context, CyNetworkNaming.class);
		networkViewFactory = getService(context, CyNetworkViewFactory.class);
		networkViewManager = getService(context, CyNetworkViewManager.class);
		serviceRegistrar = getService(context, CyServiceRegistrar.class);
		
		//Set up tabbed panel in Control Panel
		CySwingApplication cytoscapeDesktopService = getService(context,CySwingApplication.class);
		
		
		CyApplicationManager appManager = getService(context, CyApplicationManager.class);
		
		OpenTaskFactory openTaskFactory = new OpenTaskFactory(cytoscapeDesktopService, serviceRegistrar, appManager);
		Properties openTaskFactoryProps = new Properties();
		openTaskFactoryProps.setProperty("preferredMenu", "Apps.SCODE");
		openTaskFactoryProps.setProperty("title", "Open SCODE");
		openTaskFactoryProps.setProperty("menuGravity","1.0");
		
		registerService(context, openTaskFactory, TaskFactory.class, openTaskFactoryProps);
		
		CloseTaskFactory closeTaskFactory = new CloseTaskFactory(cytoscapeDesktopService, serviceRegistrar);
		Properties closeTaskFactoryProps = new Properties();
		closeTaskFactoryProps.setProperty("preferredMenu", "Apps.SCODE");
		closeTaskFactoryProps.setProperty("title", "Close SCODE");
		closeTaskFactoryProps.setProperty("menuGravity","2.0");
		
		registerService(context, closeTaskFactory, TaskFactory.class, closeTaskFactoryProps);

	}
}
