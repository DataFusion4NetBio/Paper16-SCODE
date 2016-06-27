package edu.virginia.uvacluster.internal;

import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

public class TrainingTask extends AbstractNetworkTask{
	
	//Class field
	public Model clusterModel = null;
	private CyRootNetwork rootNetwork;
	private InputTask userInput;
	
	public TrainingTask(CyNetwork network, InputTask userInput){
			super(network);

			rootNetwork = ((CySubNetwork)network).getRootNetwork();
			this.userInput = userInput;
	}
	
	@Override
	public void run(final TaskMonitor taskMonitor) throws Exception{
		CyNetwork outputNet = null, trainedNet = null, modelNetwork = null;
		taskMonitor.setTitle("SCODE Progress");
		try {
			
			if (userInput.trainNewModel){
				// Train a template
				if (userInput.customModel) {
					// Load custom template
					modelNetwork = getNetwork(userInput.bayesModel.getSelectedValue());
				} else {
					// Load built-in template
					modelNetwork = ClusterUtil.getDefaultModel(userInput.weightName.getSelectedValue(), SavePolicy.DO_NOT_SAVE);
				}
				// Create empty network for saving state of model
				outputNet = CyActivator.networkFactory.createNetwork();
				outputNet.getRow(outputNet).set(CyNetwork.NAME, CyActivator.networkNaming.getSuggestedNetworkTitle("Supervised Complex Model"));
				
				// Set task monitor progress
				taskMonitor.setProgress(0.1);
				taskMonitor.setStatusMessage("Training...");
				clusterModel = new SupervisedModel(rootNetwork, modelNetwork,outputNet, userInput);
				CyActivator.networkManager.addNetwork(outputNet); 
				taskMonitor.setStatusMessage("Training is done.");
				taskMonitor.setProgress(1.0);
			}
			else {
				// Use an existing model
				taskMonitor.setProgress(0.1);
				taskMonitor.setStatusMessage("Loading model...");
				trainedNet = getNetwork(userInput.existingModel.getSelectedValue());
				clusterModel = new SupervisedModel(rootNetwork, trainedNet, userInput);
				
				taskMonitor.setStatusMessage("Model loaded.");
				taskMonitor.setProgress(1.0);
			}
		
		} catch ( Exception e){
			e.printStackTrace();
			throw new Exception("Training didn't work out so well..." + e.getMessage());
		}
    
	}
	
	//Method uses CyActivator's CyNetworkManager to get network using its name
	private CyNetwork getNetwork(String networkName) {
		CyNetwork resultNetwork = null;
		Set<CyNetwork> networks = CyActivator.networkManager.getNetworkSet();
		
		for (CyNetwork network: networks) {
			if (networkName.equals(network.getRow(network).get(CyNetwork.NAME, String.class))) {
				resultNetwork = network;
				break;
			}
		}
		
		return resultNetwork;
	}
}
