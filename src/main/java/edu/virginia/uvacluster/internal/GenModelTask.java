package edu.virginia.uvacluster.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.view.model.CyNetworkView;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;

public class GenModelTask extends AbstractTask {

	@Override
	public void run(TaskMonitor arg0) throws Exception {
		CyNetwork network = ClusterUtil.getDefaultModel("weight", SavePolicy.SESSION_FILE);
		CyActivator.networkManager.addNetwork(network);
		network.getRow(network).set(CyNetwork.NAME, CyActivator.networkNaming.getSuggestedNetworkTitle("Default Bayesian Model"));
		CyNetworkView view = CyActivator.networkViewFactory.createNetworkView(network);
		CyActivator.networkViewManager.addNetworkView(view);
	}
}
