package edu.virginia.uvacluster.internal;

import java.util.Properties;

import org.cytoscape.model.CyNetworkFactory;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.session.CyNetworkNaming;
import org.cytoscape.task.NetworkTaskFactory;
import org.osgi.framework.BundleContext;

public class CyActivator extends AbstractCyActivator {
	public static CyNetworkManager networkManager;
	public static CyNetworkFactory networkFactory;
	public static CyNetworkNaming networkNaming;
	
	@Override
	public void start(BundleContext context) throws Exception {
		networkFactory = getService(context, CyNetworkFactory.class);
		networkManager = getService(context, CyNetworkManager.class);
		networkNaming = getService(context, CyNetworkNaming.class);
		
		SupervisedComplexTaskFactory clusterFactory= new SupervisedComplexTaskFactory();
		GenModelTaskFactory modelFactory = new GenModelTaskFactory();
		
		//Set service properties
		Properties clusterFactoryProperties = new Properties();
		clusterFactoryProperties.setProperty("preferredMenu", "Apps.Supervised Complex");
		clusterFactoryProperties.setProperty("title","Analyze Network");
		Properties genNetworkProperties = new Properties();
		genNetworkProperties.setProperty("preferredMenu", "Apps.Supervised Complex");
		genNetworkProperties.setProperty("title", "Generate Default Model");
		
		//register services
		registerService(context, clusterFactory, NetworkTaskFactory.class, clusterFactoryProperties);
		registerService(context, modelFactory, NetworkTaskFactory.class,genNetworkProperties);
	}
}
