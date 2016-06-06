package edu.virginia.uvacluster.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskMonitor;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

public class InputTask extends AbstractTask{
	//User input collected below. 
	//Instances of this class also act as parameter containers.  
	
	public String graphName = "";
	
//	@Tunable(description="Choose a variant", groups={"Search", "Variant"},
//			tooltip="")
	public ListSingleSelection<String> chooser = new ListSingleSelection<String>("Greedy ISA", "M ISA", "ISA");
	
//	@Tunable(description="Neighbors to Consider", groups={"Search", "Variant"}, dependsOn="chooser=M ISA", 
//			tooltip="When using M ISA, this is the number of nodes that are scanned in the neighborhoods of candidates at each search step.")
	public int checkNumNeighbors = 20;
//	
//	@Tunable(description="Use Selected Nodes as Seeds", groups={"Search"},
//			tooltip="")
	public boolean useSelectedForSeeds = false;
	
//	@Tunable(description="Number of Seeds", groups={"Search"}, dependsOn="useSelectedForSeeds=false",
//			tooltip="The number of seed nodes on which the search is performed.  Seed nodes are selected by greatest degree.")
	public int numSeeds = 10;
	
//	@Tunable(description="Search Limit", groups={"Search"},
//			tooltip="The maximum number of iterations that the iterative simualted annealing search will take.")
	public int searchLimit = 20;
	
//	@Tunable(description="Initial Temperature", groups={"Search"},
//			tooltip="The initial temperature of the search. A higher temperature means the search will continue for longer.")
	public double initTemp = 1.80;
	
//	@Tunable(description="Temperature Scaling Factor", groups={"Search"},
//			tooltip="The rate at which the temperature changes each iteration of the search.  "
//					+ "A higher temperature means the search is more likely to continue.")
	public double tempScalingFactor = 0.88;
	
//	@Tunable(description="Overlap Limit", groups={"Search"},
//			tooltip="The search will stop for candidates that overlap another candidate by this ratio.")
	public double overlapLimit = 0.75;
	
//	@Tunable(description="Minimum Complex Score", groups={"Search"},
//			tooltip="Candidates below this threshold are discarded at the end of the search.")
	public double minScoreThreshold = -400;
	
//	@Tunable(description="Minimum Complex Size", groups={"Search"},
//			tooltip="Candidates below this threshold are discarded at the end of the search.")
	public int minSize = 3;
	
//	@Tunable(description="Train New Model", groups={"Train Model"},
//			tooltip="This requires a list of training complexes.")
	public boolean trainNewModel = true;
	
//	@Tunable(description="Use Trained Model", groups={"Train Model"}, dependsOn="trainNewModel=false",
//			tooltip="")
	public ListSingleSelection<String> existingModel = new ListSingleSelection<String>(getNetworkNames());
	
//	@Tunable(description="Use Custom Bayesian Network", groups={"Train Model"}, dependsOn="trainNewModel=true",
//			tooltip="Please refer to the project's github page for instructions on creating custom bayesian networks.")
	public boolean customModel = false;
	
//	@Tunable(description="Custom Bayesian Network", groups={"Train Model"}, dependsOn="customModel=true",
//			tooltip="Please refer to the project's github page for instructions on creating custom bayesian networks.")
	public ListSingleSelection<String> bayesModel = new ListSingleSelection<String>(getNetworkNames());
	
//	@Tunable(description="Edge Weight Column", groups={"Train Model"}, dependsOn="trainNewModel=true", params="required=true",
//			tooltip="")
	public ListSingleSelection<String> weightName = new ListSingleSelection<String>(getEdgeColumnNames());  //chosen with cross validation
	
//	@Tunable(description="Cluster Probability Prior", groups={"Train Model"}, dependsOn="trainNewModel=true",
//			tooltip="")
	public double clusterPrior = 0.0001;  //chosen with cross validation
	
//	@Tunable(description="Generate # of Negative Examples", groups={"Train Model"}, dependsOn="trainNewModel=true",
//			tooltip="This dictates the number of random, non-complex examples generated for training.")
	public int negativeExamples = 2000;
	
//	@Tunable(description="Load Positive Training Data", groups={"Train Model"}, dependsOn="trainNewModel=true", params="input=true;fileCategory=unspecified",
//			tooltip="Please refer to the project's gitub page for the proper format of training examples.")
	public File trainingFile;
	
//	@Tunable(description="Ignore Missing Nodes", groups={"Train Model"}, dependsOn="trainNewModel=true", params="input=true",
//			tooltip="If selected, any training examples with nodes that are not in the active network are ignored.  "
//					+ "Otherwise, training will fail with a notification.")
	public boolean ignoreMissing = true;
	
//	@Tunable(description="Save Results to File (Optional)", groups={"Train Model"}, params="input=false;fileCategory=unspecified",
//			tooltip="Results are saved as tab separated values.")
	public File resultFile;
	
	public File selectedSeedFile;
	
	public boolean supervisedLearning = true;
	
	@Override //Adds other tasks to iterator based on user's input
	public void run(TaskMonitor arg0) throws Exception {
		System.out.println("Input is being collected...");
		if (getEdgeColumnNames().size() == 1 ) throw new Exception("Your network must contain a column for edge weights.");
		// let tunables do their thing...
	}
	
	/**
	 * Convenience method for helping the task factory issue selected search task.
	 * 
	 * @return true when the ISA Search is selected by user, false otherwise.
	 */
	public String getSelectedSearch() {
		return chooser.getSelectedValue();
	} 
	
	private List<String> getNetworkNames() {
		List<String> names = new ArrayList<String>();
		for (CyNetwork network: CyActivator.networkManager.getNetworkSet()) {
			names.add(network.getRow(network).get(CyNetwork.NAME, String.class));
		}
		return names;
	}
	
	private List<String> getEdgeColumnNames() {
		List<String> names = new ArrayList<String>();
		names.add("- Select Column -");
		for (CyNetwork network: CyActivator.networkManager.getNetworkSet()) {
			for (CyColumn col:  network.getDefaultEdgeTable().getColumns()) {
				if (!names.contains(col.getName()) &&  (!col.getName().equals("SUID")) &&
						((col.getType() == Double.class) || (col.getType() == Integer.class) || (col.getType() == Long.class))) 
					names.add(col.getName());
			}
		}
		return names;
	}
}
