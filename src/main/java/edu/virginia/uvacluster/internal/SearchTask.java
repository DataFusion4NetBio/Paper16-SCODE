package edu.virginia.uvacluster.internal;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
	private ArrayList<Cluster> resultComplexes;
	public long elapsedTime;
	
	public SearchTask(CyNetwork network, InputTask userInput, TrainingTask training) {
		super(network);
		rootNetwork = ((CySubNetwork)network).getRootNetwork();
		this.userInput = userInput;
		this.training = training;
	}

	public ArrayList<Cluster> getResults() {
		return resultComplexes;
	}
	
	@Override
	public void cancel() {
		selectedSearch.cancel();
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception { 
		List<Cluster> results = null;
		List<CyNode> seeds;
		int i = 1;
		
		model = training.clusterModel;
		
		try {
			
			// Monitor execution time of training task
			long startTime = System.currentTimeMillis();
			
			taskMonitor.setTitle("Searching Network");
			System.out.println("Selecting search algorithm...");
			//Perform selected search
				taskMonitor.setProgress(0.1);
				taskMonitor.setStatusMessage("Gathering Seeds...");
				
				// Either get the nodes the user selected, or choose the highest degree nodes from the network
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
//			System.out.println("Adding " + results.size() + " Result Networks via manager...");
			resultComplexes = new ArrayList<Cluster>();
			for (Cluster result: results) {	
					resultComplexes.add(result);	
			}
			
			// Sort the results by their score
			Collections.sort(resultComplexes, new Comparator<Cluster>() {
			    @Override
			    public int compare(Cluster c1, Cluster c2) {
			        try {
						return ~Double.compare(model.score(c1), model.score(c2));
					} catch (Exception e) {
						return 0;
					}
			    }
			});
			
			// Get the top N results sorted by score
			ArrayList<Cluster> networkResults = new ArrayList<Cluster>(userInput.numResults);
			if (resultComplexes.size() < userInput.numResults) {
				networkResults.addAll(resultComplexes);
			} else {
				networkResults.addAll(resultComplexes.subList(0, userInput.numResults));
			}
			
			// Create networks for the top N results
			for (Cluster network : networkResults) {
				CySubNetwork result = network.getSubNetwork();
				CyActivator.networkManager.addNetwork(result);
				
				double score = userInput.supervisedLearning ? model.score(new Cluster(model.getFeatures(),result)) :
					ClusterScore.score(result, null);
				result.getRow(result).set(CyNetwork.NAME, CyActivator.networkNaming
						.getSuggestedNetworkTitle(
								"Complex #" + i + " (Score: " + score + ")"));
				i++;
			}
			
			// Output ALL results to file
//			if (userInput.resultFile != null)
//			outputResultsToFile(results, userInput.resultFile);
			
			
			System.out.println("Search Complete.");
			
			// Monitor execution time of training task
			long stopTime = System.currentTimeMillis();
			elapsedTime = stopTime - startTime;
			elapsedTime = elapsedTime % 1000;
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Searching didn't go so well... " + e.getMessage());
		}
	}
	
	
	private void outputResultsToFile(List<Cluster> results, File file) throws IOException {
		if (! file.exists()) 
			file.createNewFile();
		
		FileWriter writer = new FileWriter(file);
		String fileContents = "";
		List<CyNode> nodes;
		int i  = 1;
		
		for (Cluster subNetwork: results) {
			CySubNetwork result = subNetwork.getSubNetwork();
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
//		return CyTableUtil.getNodesInState(rootNetwork,"selected",true); // -- Gets nodes selected by mouse-click
		
		File seedFile = userInput.selectedSeedFile;
		System.out.println("The selected seed file is: " + seedFile.getAbsolutePath());
		ArrayList<String> seedNames = new ArrayList<String>();
		try (BufferedReader br = new BufferedReader(new FileReader(seedFile))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       seedNames.add(line);
		    }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		ArrayList<CyNode> nodeList = new ArrayList<CyNode>();
		for (CyNode node: network.getNodeList()) {
			String cyNodeName = network.getRow(node).get("name", String.class);
			if (seedNames.contains(cyNodeName)) {
				System.out.println("Network contains protein: " + cyNodeName);
				nodeList.add(node);
			}
//			System.out.println("Network does not contain protein: " + cyNodeName);
		}
		return nodeList;
	}
	

}

