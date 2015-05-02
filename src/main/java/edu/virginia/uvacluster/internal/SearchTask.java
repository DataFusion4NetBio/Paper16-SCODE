package edu.virginia.uvacluster.internal;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTableUtil;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.task.AbstractNetworkTask;
import org.cytoscape.work.TaskMonitor;

public class SearchTask extends AbstractNetworkTask{
	CyRootNetwork rootNetwork;
	private Model model;
	private TrainingTask training = null;
	private InputTask userInput;
	private Search selectedSearch;
	
	public SearchTask(CyNetwork network, InputTask userInput, TrainingTask training) {
		super(network);
		rootNetwork = ((CySubNetwork)network).getRootNetwork();
		this.userInput = userInput;
		this.training = training;
	}

	@Override
	public void cancel() {
		selectedSearch.cancel();
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception { 
		List<CySubNetwork> results = null;
		List<CyNode> seeds;
		int i = 1;
		
		model = training.clusterModel;
		
		try {
			taskMonitor.setTitle("Searching Network");
			System.out.println("Selecting search algorithm...");
			//Perform selected search
				taskMonitor.setProgress(0.1);
				taskMonitor.setStatusMessage("Gathering Seeds...");
				
				seeds = userInput.useSelectedForSeeds ? getSelectedNodes() : 
														ClusterUtil.getTopDegreeNodes(rootNetwork, userInput.numSeeds);
				System.out.println("ISA Search Selected, seeding with " + seeds.size() + " nodes...");
				
				taskMonitor.setProgress(0.3);
				taskMonitor.setStatusMessage("Initializing Search...");
				selectedSearch = new IsaSearch(rootNetwork, model, seeds);
				
				taskMonitor.setProgress(0.6);
				taskMonitor.setStatusMessage("Executing Search...");
				results = selectedSearch.execute(userInput, taskMonitor);
			
			//TODO Order results with highest score first
			
			//Add results to session for display and presentation
			taskMonitor.setProgress(0.9);
			taskMonitor.setStatusMessage("Returning Results...");
			System.out.println("Adding " + results.size() + " Result Networks via manager...");
			for (CySubNetwork result: results) {
				CyActivator.networkManager.addNetwork(result);
				result.getRow(result).set(CyNetwork.NAME, CyActivator.networkNaming
						.getSuggestedNetworkTitle(
								"Complex #" + i + " (Score: " + model.score(new Cluster(model.getFeatures(),result)) + ")"));
				i++;
			}
			
			if (userInput.resultFile != null)
				outputResultsToFile(results, userInput.resultFile);
			
			System.out.println("Search Complete.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Searching didn't go so well... " + e.getMessage());
		}
	}
	
	
	private void outputResultsToFile(List<CySubNetwork> results, File file) throws IOException {
		if (! file.exists()) 
			file.createNewFile();
		
		FileWriter writer = new FileWriter(file);
		String fileContents = "";
		List<CyNode> nodes;
		int i  = 1;
		
		for (CySubNetwork result: results) {
			nodes = result.getNodeList();
			
			fileContents = fileContents + i +"\t" + result.getRow(result).get(CyNetwork.NAME, String.class) + "\t";
			for (CyNode node: nodes) {
				fileContents = fileContents + rootNetwork.getDefaultNodeTable().getRow(node.getSUID()).get("shared name", String.class) + " ";
			}
			fileContents = fileContents + "\n";
			i++;
		}
		
		writer.write(fileContents);
		writer.close();
	}
	
	//Use this for alternate seeding
	private List<CyNode> getSelectedNodes() {
		return CyTableUtil.getNodesInState(rootNetwork,"selected",true);
	}

}
