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
				destinationLevel.addAll(network.getNeighborList(origin, Type.OUTGOING));
                for (CyNode destination: destinationLevel) {
                	
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
						nodesPerBin = newNodes/toNodeBins; 
						for (int i = 0; i < newNodes; i++) {
							//Order of bins in list is important, note integer division
							Node currentNode = new Node(featureDesc, new Bin((i/nodesPerBin)+1,toNodeBins));
							nodeMap.get(destination).add(new Node(featureDesc, new Bin((i/nodesPerBin)+1,toNodeBins)));
						}
					}
					
					//create edges for internal origin nodes
					fromNodes = nodeMap.get(origin);
					toNodes = nodeMap.get(destination);
					
					for (Node fromNode : fromNodes) {
						for (Node toNode : toNodes) {
							fromNode.addChild(toNode);
						}
					}

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
	
	public void trainBins(List<Cluster> clusters) {
		// trainBinning() gets the range of each feature
		for (Cluster cluster: clusters) {
			cluster.trainBinning();
		}
	}
	
	public void trainOn(List<Cluster> clusters) {
		Map<String, Bin> featureMap;
		List<Child> currentLevel = new ArrayList<Child>();
		List<Child> nextLevel = new ArrayList<Child>();
		
		for (Cluster cluster: clusters) {
			featureMap = cluster.getBinMap();
			nextLevel.addAll(root.getChildren());

			for (Child child : getRootFeatures()) {
				System.out.println(child.getName());
				child.addTo(featureMap.get(child.getName()).number);
			}
		}
	}
	
	public double score(Cluster cluster) {
		double score = 1;
		Map<String, Bin> features = cluster.getBinMap();
		
		scanGraph(features);
		for (Child child : getRootFeatures()) {
			if (features.get(child.getName()).number < 0) {
				// Value for the feature is outside the bins
				score *= child.scoreValOutsideRange();
			} else {
				score *= child.score(features.get(child.getName()).number);
			}
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
	}
	
	public void printChild(Child c) {
		System.out.println("\t\t" + c.getName());	
	}
	
	private void resetGraph() {root.reset();}
	//Activates all the nodes that should be active, making many-to-one nodes easy to train/score
	private void scanGraph(Map<String, Bin> features) {
		List<Child> currentLevel = new ArrayList<Child>();
		List<Child> nextLevel = new ArrayList<Child>();
		nextLevel.addAll(root.getChildren());
		resetGraph();
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
	
	private class Node {
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
			for (Child child: children) {
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
	public class Child {
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
					count++;
				} 
				totalSamples++;
				probability = ((double)count + 1.0) / ((double)totalSamples + (double)node.getTotalBins());
//			}

		}
		public double score(int bin) {
			double score = 1;
			if (node.parentsActive()) {
				if(node.getBin() == bin) {
					score = probability;
				}
			}
			return score;
		}
		
		public double scoreValOutsideRange() {
			totalSamples++;
			return 1.0 / ((double)totalSamples + (double)node.getTotalBins());
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
