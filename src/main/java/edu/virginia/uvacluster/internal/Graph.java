package edu.virginia.uvacluster.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;

import edu.virginia.uvacluster.internal.feature.Bin;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.feature.FeatureUtil;
import edu.virginia.uvacluster.internal.statistic.Statistic;


public class Graph {
	private Node root;
	private String category;
	private final static Pattern binPattern = Pattern.compile(".*\\(.*(\\d+).*/.*(\\d+).*\\).*");
	private final static Pattern numBinPattern = Pattern.compile(".*\\(.*(\\d+).*\\).*");
	//TODO right now this will break everything when 2 or more spaces are present
	//TODO fixed?
	private final static Pattern spacesPattern = Pattern.compile("\\p{Space}{2,}");
	private Double prior;

	public Graph(String category, Double prior) {
		this.category = category;
		root = new Node(getRootName(),null);
		this.prior = prior;
	}
	
	Node getRoot() {
		return root;
	}
	
	private String getRootName() {
		return "Root " + category;
	}
	
	//returns a set of node names in the form "Statistic: Feature (TotalBins)"
	public Set<String> loadModelFrom(CyNetwork network) {
		System.out.println("----------------LOAD MODEL FROM CYTOSCAPE NETWORK ----------------");
		Set<String> features = new HashSet<String>();
		Map<CyNode, List<Node>> nodeMap = new HashMap<CyNode, List<Node>>();
		List<CyNode> originLevel = new ArrayList<CyNode>(), 
					 destinationLevel = new ArrayList<CyNode>(),
					 nodesVisited = new ArrayList<CyNode>();
		List<Node> fromNodes, toNodes;
		List<Integer> parentNumNodes = new ArrayList<Integer>(),
					  parentNumNodesVisited = new ArrayList<Integer>();
		Matcher m;
		String nodeName = null, featureDesc = null;
		int toNodeBins, nodesPerBin, newNodes, reps = 1;
		
		for (CyNode node: network.getNodeList()) {
			if (network.getRow(node).get("name", String.class).equals("Root")) {
				destinationLevel.add(node);
				nodeMap.put(node, Arrays.asList(root));
				break;
			}
		}
		do {
			originLevel.clear();
			originLevel.addAll(destinationLevel);
			destinationLevel.clear();
			for (CyNode origin: originLevel) {
				nodesVisited.add(origin);
				destinationLevel.removeAll(network.getNeighborList(origin, Type.OUTGOING)); //to prevent duplicates
//				System.out.println("Size of destination level: " + destinationLevel.size());
				destinationLevel.addAll(network.getNeighborList(origin, Type.OUTGOING));
                for (CyNode destination: destinationLevel) {
                	
//                	System.out.println("Origin: " + origin.getSUID() + ", Destination: " + destination.getSUID());
					parentNumNodes.clear();
					parentNumNodesVisited.clear();
					for (CyNode parent: network.getNeighborList(destination, Type.INCOMING)) {
						parentNumNodes.add(getNodeCount(network,parent));
						if (nodesVisited.contains(parent)) parentNumNodesVisited.add(nodeMap.get(parent).size());
					}
					nodeName = network.getRow(destination).get("name", String.class);
					nodeName = spacesPattern.matcher(nodeName).replaceAll(" ").trim();
					m = numBinPattern.matcher(nodeName); 
					m.matches();
					toNodeBins = Integer.parseInt(m.group(1));
					
					//create nodes for internal graph if CyNode hasn't been visited yet
					if (nodeMap.get(destination) == null) {
						nodeMap.put(destination, new ArrayList<Node>());
						featureDesc = nodeName.replaceAll("\\(.*\\)","").trim();
						featureDesc = featureDesc.replaceAll(":", " : ");
						featureDesc = featureDesc.replaceAll("\\(", " \\( ");
						featureDesc = spacesPattern.matcher(featureDesc).replaceAll(" ").trim();
						features.add(nodeName);
						newNodes = toNodeBins;
//						for (Integer numNodes: parentNumNodes) {newNodes *= numNodes;}
						nodesPerBin = newNodes/toNodeBins; 
						for (int i = 0; i < newNodes; i++) {
//							System.out.println("newNodes: " + newNodes);
							//Order of bins in list is important, note integer division
//							System.out.println("Graph size before: " + this.getRoot().getChildren().size() + " -- node being added: " + featureDesc + " (" + (i/nodesPerBin) + "/" + toNodeBins + ")");
							
							Node currentNode = new Node(featureDesc, new Bin((i/nodesPerBin)+1,toNodeBins));
//							printNode(currentNode);
							nodeMap.get(destination).add(new Node(featureDesc, new Bin((i/nodesPerBin)+1,toNodeBins)));
//							System.out.println("Graph size after: " + this.getRoot().getChildren().size());
						}
					}
					
					//create edges for internal origin nodes
					fromNodes = nodeMap.get(origin);
					toNodes = nodeMap.get(destination);
					for (Integer parentNodesVisited: parentNumNodesVisited) {reps *= parentNodesVisited;}
//					System.out.println("Reps - 1: " + reps);
					reps /= fromNodes.size();
//					System.out.println("Reps - 2: " + reps);
					
					for (Node fromNode : fromNodes) {
						for (Node toNode : toNodes) {
							fromNode.addChild(toNode);
						}
					}
//					for (int toNode = 0, fromNode = 0, i = 0; toNode < toNodes.size(); toNode++) {
//						System.out.println("Reps - 3: " + reps);
//						System.out.println("FROM");
//						printNode(fromNodes.get(fromNode));
//						System.out.println("TO");
//						printNode(toNodes.get(toNode));
//							if (i == reps) {
//								fromNode = (fromNode + 1)%fromNodes.size();
//								i = 0;
//							}
//							fromNodes.get(fromNode).addChild(toNodes.get(toNode));
//							i++;
//					}
                }
			}
		} while (destinationLevel.size() > 0);
		return features;
	}
	
	private int getNodeCount(CyNetwork network, CyNode modelNode) {
		String nodeName = network.getRow(modelNode).get("name", String.class);
		Matcher m;
		
		nodeName = spacesPattern.matcher(nodeName).replaceAll(" ").trim();
		if (nodeName.equals("Root")) return 1;
		m = numBinPattern.matcher(nodeName);
		m.matches();
		int parentBins = Integer.parseInt(m.group(1));
		for (CyNode parent: network.getNeighborList(modelNode, Type.INCOMING)) {
			parentBins *= getNodeCount(network,parent);
		}
		return parentBins;
	}
		
	//returns a set of feature keys in the form "Statistic: Feature (TotalBins)"
	public List<FeatureSet> loadTrainedModelFrom(CyNetwork network) {
		List<FeatureSet> features;
		HashMap<String,Double> minMap = new HashMap<>(), maxMap = new HashMap<>();
		Set<String> featureNames = new HashSet<String>();
		Map<CyNode, Node> nodeMap = new HashMap<CyNode, Node>();
		List<CyNode> originLevel = new ArrayList<CyNode>(), 
					 nextOriginLevel = new ArrayList<CyNode>();
		CyEdge edge = null;
		Node modelNode = null, newNode = null; 
		Child child;
		Matcher m;
		String nodeName = null, featureDesc = null;
		double probability = 0;
		int binNumber, numBins;
		
		for (CyNode node: network.getNodeList()) {
			if (network.getRow(node).get("name", String.class).equals(getRootName())) {
				nextOriginLevel.add(node);
				nodeMap.put(node, root);
				break;
			}
		}
		
		do {
			originLevel.clear();
			originLevel.addAll(nextOriginLevel);
			nextOriginLevel.clear();
			for (CyNode origin: originLevel) {
				modelNode = nodeMap.get(origin);
				for (CyNode destination: network.getNeighborList(origin, Type.OUTGOING)) {
					nextOriginLevel.add(destination);
					newNode = nodeMap.get(destination);
					edge = network.getConnectingEdgeList(origin, destination, Type.ANY).get(0);
					if (newNode == null) {
						nodeName = network.getRow(destination).get("name", String.class);
						nodeName = spacesPattern.matcher(nodeName).replaceAll(" ").trim();
						m = binPattern.matcher(nodeName);
						m.matches();
						binNumber = Integer.parseInt(m.group(1));
						numBins = Integer.parseInt(m.group(2));
						featureDesc = nodeName.replaceAll("\\(.*\\)","").trim();
						featureNames.add(nodeName.replaceAll("\\(.*\\d+.*/","("));
						newNode = new Node(featureDesc, new Bin(binNumber,numBins));
						minMap.put(featureDesc, network.getRow(destination).get("min", Double.class));
						maxMap.put(featureDesc, network.getRow(destination).get("max", Double.class));
						nodeMap.put(destination, newNode);
					}
					
					child = modelNode.addChild(newNode);
					probability = network.getRow(edge).get("Probability", Double.class);
					System.out.println(probability);
					child.setProbability(probability);

				}
			}
		} while(nextOriginLevel.size() > 0);
		features = FeatureUtil.parse(featureNames);
		for (FeatureSet f: features) {
			for (Statistic s: f.getStatistics()) {
				s.getRange().setSpan(minMap.get(f.getDescriptor(s)), maxMap.get(f.getDescriptor(s)));
			}
		}
		return features;
	}
	
	public void saveTrainedModelTo(CyNetwork network, List<FeatureSet> features) {
		Map<String,Statistic> statsMap = new HashMap<String,Statistic>();
		System.out.println("The size of the feature set is : " + features.size());
		for (FeatureSet f: features) {statsMap.putAll(f.getStatisticMap());}

		List<Node> originLevel = new ArrayList<Node>(), nextOriginLevel = new ArrayList<Node>();
		Node destination = null;
		CyEdge edge = null;
		if (network.getDefaultNodeTable().getColumn("min") == null)
			network.getDefaultNodeTable().createColumn("min", Double.class, false);
		if (network.getDefaultNodeTable().getColumn("max") == null)
			network.getDefaultNodeTable().createColumn("max", Double.class, false);
		
		if (network.getDefaultEdgeTable().getColumn("Probability") == null)
			network.getDefaultEdgeTable().createColumn("Probability", Double.class, false);
		nextOriginLevel.add(root);
		do {
			originLevel.clear(); 
			originLevel.addAll(nextOriginLevel); 
			nextOriginLevel.clear();
			for (Node origin: originLevel) {
				for (Child child: origin.getChildren()) {
					nextOriginLevel.remove(child.getNode());
					nextOriginLevel.add(child.getNode());
						
					destination = child.getNode();
					if (origin.cyNode == null) {
						origin.cyNode = network.addNode(); 
						network.getRow(origin.cyNode).set("name", origin.getDisplayName());
					}
					if (destination.cyNode == null) {
						destination.cyNode = network.addNode(); 
						network.getRow(destination.cyNode).set("name", destination.getDisplayName());
						network.getRow(destination.cyNode).set("max", statsMap.get(destination.getName()).getRange().getMax());
						network.getRow(destination.cyNode).set("min", statsMap.get(destination.getName()).getRange().getMin());
					}
					edge = network.addEdge(origin.cyNode, destination.cyNode, true);
					network.getRow(edge).set("Probability", child.getProbability());
				}
			}
		} while(nextOriginLevel.size() > 0);
	}
	
	public String getBinnedName(Child c) {
		return c.getName() + "_" + c.node.getBin();
	}
	
	public ArrayList<Child> getRootFeatures() {
		HashSet<String> childNames = new HashSet<String>();
		List<Child> children = root.getChildren();
		ArrayList<Child> results = new ArrayList<Child>();
		for (Child c : children) {
			if (!childNames.contains(getBinnedName(c))) {
				results.add(c);
				childNames.add(getBinnedName(c));
				results.addAll(getChildFeatures(c, childNames));
			}
			childNames.add(getBinnedName(c));
		}
		return results;
	}
	
	public ArrayList<Child> getChildFeatures(Child c, HashSet<String> visitedChildren) {
		List<Child> grandChildren = c.getChildren();
		ArrayList<Child> results = new ArrayList<Child>();
		for (Child grandChild : grandChildren) {
			if (!visitedChildren.contains(getBinnedName(grandChild))) {
				results.add(grandChild);
				visitedChildren.add(getBinnedName(grandChild));
				results.addAll(getChildFeatures(grandChild, visitedChildren));
			}
		}
		return results;
	}
	
	
	public void trainOn(List<Cluster> clusters) {
		System.out.println("Start training on clusters");
//		System.out.println("----PRINTING FEATURE NETWORK------");
//		printNetwork();
		Map<String, Bin> featureMap;
		List<Child> currentLevel = new ArrayList<Child>();
		List<Child> nextLevel = new ArrayList<Child>();

		// trainBinning() gets the range of each feature
//		System.out.println("Before trainBinning: the size of the graph is: " + this.getRoot().getChildren().size());
		for (Cluster cluster: clusters) {
			cluster.trainBinning();
//			System.out.println("After trainBinning: the size of the graph is: " + this.getRoot().getChildren().size());
		}
		System.out.println("Cluster train binning complete");
		
		
		for (Cluster cluster: clusters) {
			featureMap = cluster.getBinMap();
//			for (String feature: featureMap.keySet()) {
//				System.out.println("For cluster " + cluster.getSUID() + ":");
//				System.out.println("\tFeature= " + feature + "\tBin=" + featureMap.get(feature).number);
//			}
			System.out.println("Cluster: " + cluster.getSUID());
//			featureMap = cluster.getNewBinMap();
//			System.out.println("In trainOn: the size of the featureMap for cluster " + cluster.getSUID() + " is: " + featureMap.size());
//			System.out.println("In trainOn: the size of the graph is: " + this.getRoot().getChildren().size());
			nextLevel.addAll(root.getChildren());

			
			for (Child child : getRootFeatures()) {
				child.addTo(featureMap.get(child.getName()).number);
			}
			
			
//			scanGraph(featureMap);
//			do {
//				currentLevel.clear();
//				currentLevel.addAll(nextLevel);
//				nextLevel.clear();
//				for (Child child: currentLevel) {
////					System.out.println("in child: currentLevel -- CHILD = " + child.getName());
//					nextLevel.removeAll(child.getChildren());
//					nextLevel.addAll(child.getChildren());
//					child.addTo(featureMap.get(child.getName()).number);
//				}
//			} while(nextLevel.size() > 0);
		}
		System.out.println("Done training on clusters");
	}
	
	public double score(Cluster cluster) {
		double score = 1;
		Map<String, Bin> features = cluster.getBinMap();
		
		scanGraph(features);
//		System.out.println("Scoring on cluster: " + cluster.getSUID());
//		for (Child child : getRootFeatures()) {
//			score *= child.score(features.get(child.getName()).number);
//		}
		for (Child child: root.getChildren()) {
			score *= child.score(features.get(child.getName()).number);
			score *= childScore(child, cluster);
		}
		return score;
	}
	
	public double childScore(Child child, Cluster cluster) {
		double score = 1;
		Map<String, Bin> features = cluster.getBinMap();
		for (Child grandChild : child.getChildren()) {
			score *= grandChild.score(features.get(grandChild.getName()).number);
			if (grandChild.getChildren().size() == 0) {
				return score;
			} else {
				score *= childScore(grandChild, cluster);
			}
		}
		return score;
	}
	
	public void printNode(Node n) {
		System.out.println("Children of the node: " + n.getDisplayName());
//		for (Child c : n.children) {
//			printChild(c);
//		}
	}
	
	public void printChild(Child c) {
		System.out.println("\t\t" + c.getName());	
//		for (Child c2 : c.getChildren()) {
//			System.out.print("Children of the child node: " + c.getName());
//			printChild(c2);
//		}
	}
	
	private void resetGraph() {root.reset();}
	//Activates all the nodes that should be active, making many-to-one nodes easy to train/score
	private void scanGraph(Map<String, Bin> features) {
		List<Child> currentLevel = new ArrayList<Child>();
		List<Child> nextLevel = new ArrayList<Child>();
		nextLevel.addAll(root.getChildren());
//		System.out.println("About to reset graph");
//		System.out.println("The name of the root node is: " + root.getDisplayName());
		resetGraph();
//		System.out.println("In scanGraph: reset graph done");
		root.activate();
		do {
			currentLevel.clear();
			currentLevel.addAll(nextLevel);
			nextLevel.clear();
			for (Child child: currentLevel) {
				nextLevel.addAll(child.getChildren());
				child.activate(features.get(child.getName()).number);
			}
		} while(nextLevel.size() > 0);
	}
	
	class Node {
		private List<Child> children;
		private List<Node> parents = new ArrayList<Node>();
		private String feature;
		private Bin bin;
		private boolean active = false;
		public CyNode cyNode = null;

		public Node(String feature, Bin bin) {
			children = new ArrayList<Child>();
			this.feature = feature.toLowerCase();
			this.bin = bin;
		}
		
		public int getBin() {return bin.number;}
		public String getName() {return feature;}
		public String getDisplayName() {
			if (bin == null) return getRootName();
			return feature + " (" + bin.number.toString() +"/"+ bin.total.toString() + ")";
		}
		public int getTotalBins() {return bin.total;}
		public boolean isActive() {return active;}
		public void activate() {active = true;}
		public List<Child> getChildren(){return children;}
		public void addParent(Node p){parents.add(p);}
		public Child addChild(Node n) {
			Child child = new Child(n);
			children.add(child);
			n.addParent(this);
			return child;
		}
		public void reset() {
			active = false;
//			System.out.println("The node " + getDisplayName() + " has size " + children.size());
			for (Child child: children) {
//				System.out.println("The child about to be reset is: " + child.getName());
				child.reset();
			}
		}
		public boolean parentsActive() {
			boolean result = true;
			for (Node parent: parents) {
				result &= parent.isActive(); 
				}
			return result;
		}
	}
	
	//Stores transition information
	private class Child {
		private Node node;
		private int count = 0;
		private int totalSamples = 0;
		private double probability;
		
		public Child(Node x) {
			node = x;
			probability = 1.0 / node.getTotalBins();
		}
		public Node getNode() {return node;}
		public double getProbability() {return probability;}
		public void setProbability(double r) {probability = r;}
		public List<Child> getChildren(){return node.getChildren();}
		public String getName() {return node.getName();}
		
		public void addTo(int bin) {
//			if (node.parentsActive()) {
				if (node.getBin() == bin) {
					System.out.println("\t\tJust added to " + this.getName() + " - bin # " + bin);
					count++;
				} 
				totalSamples++;
				probability = ((double)count + 1.0) / ((double)totalSamples + (double)node.getTotalBins());
//			}

		}
		public double score(int bin) {
			double score = 1;
//			if (node.parentsActive()) {
				if(node.getBin() == bin) {
//					System.out.println("\t\tChild.score-Scoring on " + this.getName() + " - bin #" + node.getBin() + ": score = score*" + probability);
					score = probability;
				}
//			}
			return score;
		}
		public void activate(int bin) {
			if (node.parentsActive()) {
				if (node.getBin() == bin) {
					node.activate();
				}  
			}
		}
		public void reset() {node.reset();}
	}
}
