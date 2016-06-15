package edu.virginia.uvacluster.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;

import edu.virginia.uvacluster.internal.Graph.Child;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.feature.FeatureUtil;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class SupervisedModel implements Model{
	//data members
	private CyRootNetwork rootNetwork;
	private Graph negBayesGraph, posBayesGraph; //used for internal representation of bayes net
	private List<Graph> bayesGraphs;
	private List<FeatureSet> features;
	private double complexPrior;
	private InputTask userInput;

	private void setup() {
		negBayesGraph = new Graph("NonCluster", (1 - complexPrior));
		posBayesGraph = new Graph("Cluster", complexPrior);
		bayesGraphs = Arrays.asList(negBayesGraph,posBayesGraph);
	}
	/**
	 * Constructs model from the current network and a file with clusters for training.
	 * 
	 * @param trainingNetwork CyNetwork with PPI data for analysis.
	 * @param trainingData Text file containing training clusters.
	 * @throws An exception from loading the training data file
	 */
	SupervisedModel(CyRootNetwork trainingNetwork, CyNetwork modelNetwork, CyNetwork outputBayesNet, InputTask userInput) throws Exception {
		Set<String> featureDescs = null;
		List<CySubNetwork> positiveExamples = null, negativeExamples = null;
		rootNetwork = trainingNetwork;
		this.complexPrior = userInput.clusterPrior;
		this.userInput = userInput;
		setup();
		for (Graph g: bayesGraphs) {
//			System.out.println("Before loading model: graph size is " + g.getRoot().getChildren().size());
//			System.out.println("The Cytoscape model network has size " + modelNetwork.getNodeCount());
			featureDescs = g.loadModelFrom(modelNetwork);
//			System.out.println("After loading model: graph size is " + g.getRoot().getChildren().size());
			}
		features = FeatureUtil.parse(featureDescs);
//		System.out.println("Features: ");
//		for(String feat : featureDescs) {
//			System.out.println("\t\t" + feat);
//		}
//		System.out.println("Cluster model init is finished.");
		positiveExamples = loadTrainingComplexes(userInput.trainingFile);
//		System.out.println("Trained on positive complexes: " + positiveExamples.size());
//		System.out.println("Positive examples are loaded.");
		negativeExamples = generateNegativeExamples(userInput.negativeExamples, positiveExamples);
//		System.out.println("Trained on negative complexes: " + negativeExamples.size());
//		System.out.println("Negative examples are generated.");
//		System.out.println("Dup");

		train(positiveExamples, negativeExamples);
		System.out.println("Model trained...");
		saveGraphicalBayesianNetwork(outputBayesNet, features);
		System.out.println("Model saved to network...");
	}
	
	/**
	 * This constructor allows for re-use of existing models stored as CyNetworks.
	 * 
	 * @param bayesianNetwork - reads model from existing Bayesian network stored as CyNetwork
	 */
	SupervisedModel(CyRootNetwork searchNetwork, CyNetwork bayesianNetwork, InputTask input) {
		setup();
		rootNetwork = searchNetwork;
		this.userInput = input;
		this.complexPrior = input.clusterPrior;
		for (Graph g: bayesGraphs) {features = g.loadTrainedModelFrom(bayesianNetwork);}
	}

	public List<FeatureSet> getFeatures() {return features;}
	
	/**
	 * This function constructs a Cytoscape representation of the model's Bayes net.
	 * The function does not actually save the session file, but allows the model to be 
	 * carried over between sessions.  Information about parameters is not explicitly stored.
	 * 
	 * @param emptyNetwork - an empty CyNetwork where the Bayesian net will be generated
	 */
	public void saveGraphicalBayesianNetwork(CyNetwork emptyNetwork, List<FeatureSet> features) {
		for(Graph g: bayesGraphs) {
			System.out.println("--------SAVING TRAINED MODEL-----------");
//			System.out.println("The size of the trained model is: " + g.getRoot().getChildren().size());
			g.saveTrainedModelTo(emptyNetwork,features);
		}
	}
	
	/**
	 * This function returns the probability of the complex occuring according to the model.
	 * 
	 * @param complex the complex in question
	 * @return
	 */
	public double score(Cluster complex) throws Exception{
		if (userInput.supervisedLearning) {
			double nonComplexPrior = 1 - complexPrior;
			double scorePos = posBayesGraph.score(complex);
			double scoreNeg = negBayesGraph.score(complex);
//			System.out.print("\nRoot Cluster: " + scorePos + "\n\t\tRoot Non-Cluster: " 
//			+ scoreNeg + "\n\t\tScore: " + Math.log((complexPrior * scorePos) / (nonComplexPrior*scoreNeg)) );

			return Math.log((complexPrior * scorePos) / (nonComplexPrior*scoreNeg));
		} else {
			return ClusterScore.score(complex, null);
		}
	}
	
	public double score(CySubNetwork complex) throws Exception {
		if (userInput.supervisedLearning) {
			Cluster cluster = new Cluster(features, complex);
			return score(cluster);
		} else {
			return ClusterScore.score(complex, null);
		}
	}
	
	/**
	 * Creates a list of negative examples from the model's network and a list of positive complex examples.
	 * Ensures that negative examples do not overlap by more than one node with positive examples.
	 * Generates a range of examples from size 3 to size 20
	 * 
	 * @param numExamples number of examples for the list to contain
	 * @param positiveExamples positive examples of complexes used by model
	 * @return a list of sub networks that we can use in the train function
	 */
	public List<CySubNetwork> generateNegativeExamples(int numExamples, List<CySubNetwork> positiveExamples) throws Exception{
		List<CySubNetwork> negativeExamples = new ArrayList<CySubNetwork>(numExamples);
		CySubNetwork example = null;
		int minSize = 3, maxSize = 20; //parameters for negative example generation
		int[] positiveExampleSizes = new int[positiveExamples.size()];
		double[] sizeDistributionValues = new double[maxSize - minSize + 1];
		double[] sizeDistributionRatios = new double[maxSize - minSize + 1];
		double sizeDistributionTotal = 0;
		double exponent;
		int i = 0;
		
		for(CySubNetwork positiveExample: positiveExamples) {
			positiveExampleSizes[i] = positiveExample.getNodeCount();
			i++;
		}
		
		exponent = getSizeDistributionExponent(positiveExampleSizes);
		System.out.println("Exp: " + exponent);
		
		for(i = 0; i < sizeDistributionValues.length; i++) {
			sizeDistributionValues[i] = (1/(Math.pow((i + minSize), exponent)));
			System.out.println("sizeDistributionValues[" + i + "] : " + sizeDistributionValues[i]);
			sizeDistributionTotal = sizeDistributionTotal + sizeDistributionValues[i];
		}
		
		for(i = 0; i < sizeDistributionValues.length; i++) {
			
			sizeDistributionRatios[i] = sizeDistributionValues[i]/sizeDistributionTotal;
		}
		
		System.out.println("Generating negative examples");
		for(i = 0; i < sizeDistributionRatios.length; i++) {
//			System.out.println("sizeDistributionRatios[" + i + "] : " + sizeDistributionRatios[i]);
			for (int x = 0; x < Math.round(sizeDistributionRatios[i] * numExamples); x++) {
				System.out.println("x: " + x);
				example = genNegativeExample(i + minSize, positiveExamples);
				if ((example != null) && (example.getNodeCount() >= (i + minSize))) {
					negativeExamples.add(example);
				}
				else {
					rootNetwork.removeSubNetwork(example);
					x--;
				}
			}
		}
		
		return negativeExamples;
	}
	
	private CySubNetwork genNegativeExample(int size, List<CySubNetwork> positiveExamples) {
		CySubNetwork complex;
		boolean valid = true;
		int maxOverlap = 1; //parameter for negative example generation
		int overlap = 0;
		double i = 0, x = 0, randIndex = 0;
		List<CyNode> complexNodes = null, neighbors = null, nodes = rootNetwork.getNodeList();
		List<CyEdge> candidateEdges = null;
		int neighborCount = 0, nodeCount = nodes.size();
		
			complex = rootNetwork.addSubNetwork(SavePolicy.DO_NOT_SAVE);
			
			//randomly select seed node and ensure it has more than one neighbor
			x = 0;
			do {
				i = 1;
				randIndex = Math.ceil(Math.random() * nodeCount);
				for (CyNode node: nodes) {
					if( i == randIndex) {
						complex.addNode(node);
						neighbors = rootNetwork.getNeighborList(node, Type.ANY);
						neighborCount = neighbors.size();
						break;
					}
					i++;
				}
				x++;
			} while ((neighborCount == 0) && (x < (nodeCount * nodeCount))); 
			//System.out.println("i: " + i + "  randIndex: " + randIndex + "  nodeCount: " + nodeCount);
			
			if (complex.getNodeCount() == 0)
				valid = false;
			
			//add random neighbors
			while ((complex.getNodeCount() < size) && (neighborCount > 0) && valid) {
				i = 1;
				randIndex = Math.ceil(Math.random() * neighborCount);
				for (CyNode node: neighbors) {
					if (i == randIndex) {
						complexNodes = complex.getNodeList();
						complex.addNode(node); //will not add duplicates (see questionable set of neighbors above)
						
						//need to add any connecting edge(s)
						for (CyNode complexNode: complexNodes) {
							candidateEdges = rootNetwork.getConnectingEdgeList(node, complexNode, Type.ANY);
							if (! candidateEdges.isEmpty()) { //if there are edges, add the first one in list of candidates
								complex.addEdge(candidateEdges.get(0)); 
								//System.out.println("Edge added to random example.");
							}
						}
						
						neighbors.removeAll(rootNetwork.getNeighborList(node, Type.ANY)); //guarantee that there are no duplicates
						neighbors.addAll(rootNetwork.getNeighborList(node, Type.ANY));
						neighbors.removeAll(complex.getNodeList()); //subtract nodes that are part of example
						neighborCount = neighbors.size();
						break;
					}
					i++;
				}
			}
			
			for (CySubNetwork positiveExample: positiveExamples) {
				for (CyNode node: complex.getNodeList()) {
					if (positiveExample.containsNode(node)) {
						overlap++;
					}
				}
				if (overlap > maxOverlap) {
					valid = false;
					break;
				}
				overlap = 0;
			}
			
			//return negative example if it is valid, otherwise null
			if(valid) {
				return complex;
			} else {
				return null;
			}
	}
	
	/**
	 * Convenience method for training networks on positive *and* negative examples
	 */
	public void train(List<CySubNetwork> positiveExamples, List<CySubNetwork> negativeExamples) {
		System.out.println("Entered TRAIN");
		List<Cluster> posExamples = new ArrayList<Cluster>(), negExamples = new ArrayList<Cluster>();
		for(CySubNetwork pos: positiveExamples) {posExamples.add(new Cluster(features, pos)); }
		System.out.println("Lists of pos and neg training examples created");
		
		posBayesGraph.trainBins(posExamples);
		// Min/Max update
		System.out.println("TRAINED BINS ON POSITIVE COMPLEXES:");
		for (FeatureSet feature : features) {
			List<String> statNames = feature.getDescriptions();
			Map<String, Statistic> statMap = feature.getStatisticMap();
			for (String statName : statNames) {
				Statistic stat = statMap.get(statName);
				Double min = stat.getRange().getMin();
				Double max = stat.getRange().getMax();
				System.out.println(statName + "\n\tMin: " + min + "\n\tMax: " + max);
			}
		}
	
		negBayesGraph.trainBins(negExamples);
		System.out.println("TRAINED BINS ON NEGATIVE COMPLEXES:");
		for (FeatureSet feature : features) {
			List<String> statNames = feature.getDescriptions();
			Map<String, Statistic> statMap = feature.getStatisticMap();
			for (String statName : statNames) {
				Statistic stat = statMap.get(statName);
				Double min = stat.getRange().getMin();
				Double max = stat.getRange().getMax();
				System.out.println(statName + "\n\tMin: " + min + "\n\tMax: " + max);
			}
		}
		// Min/max update
		posBayesGraph.trainOn(posExamples);
		// Min/max stable
		System.out.println("Model has finished training on " + positiveExamples.size() +  " positive Examples.");
		for(CySubNetwork neg: negativeExamples) {negExamples.add(new Cluster(features, neg));}
		
		negBayesGraph.trainOn(negExamples);
		// Min/max stable
		System.out.println("Model has finished training on " + negativeExamples.size() +  " negative Examples.");
	}
	

	
	public List<CySubNetwork> loadTrainingComplexes(File complexList) throws Exception{
		//perform initial checks
		if (! complexList.isFile()) {
			throw new Exception("This is not a file");
		}
		else if (! complexList.canRead()) {
			throw new Exception("This file is not readable");
		}
		
		//locals
		List<CySubNetwork> trainingComplexes = new ArrayList<CySubNetwork>();
		String proteinList, complexName, complexNumber;
		String[] proteins;
		String encoding = null; //for FileUtils.readlines, otherwise ambiguous method call
		CySubNetwork clusterNetwork;
		List<CyNode> nodeList;
		long proteinId = -1;
		
		List<String> clusterEntries = FileUtils.readLines(complexList, encoding);
		
		for (String entry: clusterEntries){
			entry = StringUtils.trim(entry);
			proteinList = StringUtils.substringAfterLast(entry,"\t");
			proteins = StringUtils.split(proteinList);
			entry = StringUtils.substringBeforeLast(entry,"\t"); //strip out protein list
			complexName = StringUtils.substringAfter(entry,"\t");
			complexNumber = StringUtils.substringBefore(entry, "\t");
			
			if(Pattern.matches("[0-9]+",complexNumber)) { //ie. if this line is not the header row...
				clusterNetwork = rootNetwork.addSubNetwork(SavePolicy.DO_NOT_SAVE);
				
				//find nodes and add
				for(int i = 0; i < proteins.length; i++) {
					proteinId = getIdFromName(proteins[i]);
					
					if (proteinId != -1)
						clusterNetwork.addNode(rootNetwork.getNode(proteinId));
					else if (! userInput.ignoreMissing)
						throw new Exception("Protein not found in network: " + proteins[i]);
					else
						System.out.println("Protein not found in network" + proteins[i]);
				}
				
				//test for edges and add to subnetwork
				nodeList = clusterNetwork.getNodeList();
				for (CyNode x: nodeList) {
					for (CyNode y: nodeList) {
						if (rootNetwork.containsEdge(x, y))
							clusterNetwork.addEdge(rootNetwork.getConnectingEdgeList(x, y, CyEdge.Type.ANY).get(0));
					}
				} 
				
				if (clusterNetwork.getNodeCount() > 2) { //do not admit complexes with less than 3 proteins
					clusterNetwork.getRow(clusterNetwork).set(CyNetwork.NAME, complexName);
					trainingComplexes.add(clusterNetwork);
				}
				else {
					rootNetwork.removeSubNetwork(clusterNetwork);
				}
			}
			
		}
		
		return trainingComplexes;
	}
	
	//returns SUID, which is the unique id for state objects in cytoscape model
	private long getIdFromName(String name) {
		long suid = -1;
		List<CyNode> nodes = rootNetwork.getNodeList();
		String lookupName;

		for (CyNode node: nodes) {
			lookupName = rootNetwork.getDefaultNodeTable().getRow(node.getSUID()).get("shared name", String.class);
			if (StringUtils.upperCase(lookupName).equals(StringUtils.upperCase(name))) {
				suid = node.getSUID();
				break;
			}
		}
		
		return suid;
	}
	
	//returns exponent for power law distribution of negative training example sizes
	private double getSizeDistributionExponent(int[] positiveExampleSizes) {
		double exponent = 0;
		double min = ClusterUtil.arrayMin(positiveExampleSizes);
		double max = ClusterUtil.arrayMax(positiveExampleSizes);
		double n = positiveExampleSizes.length;
		
		if (min != max) {
			for (int i = 0; i < n; i++) {
				exponent = exponent + Math.log(positiveExampleSizes[i]/min);
			}
			System.out.println("exponent: " + exponent);
			exponent = 1 + (n / exponent);
		} else {
			// Avoid dividing by zero
			exponent = 2;
		}
		return exponent;
	}
}
