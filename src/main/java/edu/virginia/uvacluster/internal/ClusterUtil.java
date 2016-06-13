package edu.virginia.uvacluster.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.SavePolicy;
import org.cytoscape.model.CyEdge.Type;

public class ClusterUtil {

	public static CyNetwork getDefaultModel(String weightFeatureName, SavePolicy policy) {
		CyNetwork modelNetwork = CyActivator.networkFactory.createNetwork(policy);
		CyNode root, size, node;
		double weightCutoffs[] = {1.0, 1.2,  1.5, 1.8, 2.2, 2.6, 3.0};
		String[] featureNames = {"Mean : Degree (4)",
		                         "Variance : Degree (4)",
		                         "Median : Degree (4)",
		                         "Max : Degree (4)",
		                         String.format("Mean : weight{%s} (4)",weightFeatureName),
		                         String.format("Variance : weight{%s} (4)",weightFeatureName),
		                         String.format("Density at cutoff %s (6)",weightCutoffs[0]),
		                         String.format("Density at cutoff %s (6)",weightCutoffs[1]),
		                         String.format("Density at cutoff %s (6)",weightCutoffs[2]),
		                         String.format("Density at cutoff %s (6)",weightCutoffs[3]),
		                         String.format("Density at cutoff %s (6)",weightCutoffs[4]),
		                         String.format("Density at cutoff %s (6)",weightCutoffs[5]),
		                         String.format("Density at cutoff %s (6)",weightCutoffs[6]),
		                         "Density (4)",
		                         "Mean : Degree Correlation (4)",
		                         "Variance : Degree Correlation (4)",
		                         "Max : Degree Correlation (4)",
		                         "Mean : Clustering Coefficient (3)",
		                         "Variance : Clustering Coefficient (3)",
		                         "Max : Clustering Coefficient (3)",
		                         "Mean : Topological Coefficient (3)",
		                         "Variance : Topological Coefficient (3)",
		                         "Max : Topological Coefficient (3)",
		                         "1st : Singular Value (4)",
		                         "2nd : Singular Value (2)",
		                         "3rd : Singular Value (2)"};
		
		modelNetwork.getDefaultEdgeTable().createColumn("Probability", Double.class, false);
		root = modelNetwork.addNode();
		modelNetwork.getRow(root).set("name", "Root");
		size = modelNetwork.addNode();
		modelNetwork.getRow(size).set("name", "Count : Node (5)");
		modelNetwork.addEdge(root, size, true);
			
		for (int i = 0; i < featureNames.length; i++) {
			node = modelNetwork.addNode();
			modelNetwork.getRow(node).set("name", featureNames[i]);
			modelNetwork.addEdge(root, node, true);
//			modelNetwork.addEdge(size, node, true);
		}
		return modelNetwork;
	}
	
	public static <T> List<List<T>> divideWork(List<T> work, int workers) {
		List<List<T>> result = new ArrayList<List<T>>();
		int i;
		
		for (i = 0; i < (workers < work.size() ? workers : work.size()); i++)
			result.add(new ArrayList<T>());	
		i = 0;
		for (T item: work) {
			result.get(i % workers).add(item);
			i++;
		}
		return result;
	}
	
	//Ordered highest degree first
	public static List<CyNode> getTopDegreeNodes(CyNetwork network, int numNodes) {
		List<CyNode> results = new ArrayList<CyNode>();
		List<CyNode> nodes = network.getNodeList();
		List<CyNode> unsortedNodes = new ArrayList<CyNode>(), sortedNodes = new ArrayList<CyNode>();
		int maxDegree = 0, degree = 0, seedCount = 0;
		
		for (CyNode node: nodes) {
			//add not unsorted list while we find max
			unsortedNodes.add(node);
			degree = network.getNeighborList(node, Type.ANY).size();
			
			if (degree > maxDegree)
				maxDegree = degree;
		}
		
		//assuming low degree nodes are more prevalent
		for (int stepDegree = 0; stepDegree <= maxDegree; stepDegree++) {
			for (int i = 0; i < unsortedNodes.size(); i++) {
				degree = network.getNeighborList(unsortedNodes.get(i), Type.ANY).size();
				if (degree == stepDegree) {
					sortedNodes.add(0, unsortedNodes.get(i));
					unsortedNodes.remove(i);
					i--; //don't advance the index to avoid skipping elements
				}
			}
		}
		
		while ((seedCount < numNodes) && (seedCount < sortedNodes.size())) {
			results.add(sortedNodes.get(seedCount));
			seedCount++;
		}
		
		return results;
	}
	
	//Descending order
	public static List<CyNode> sortByDegree(CyNetwork network, List<CyNode> nodes) {
		List<CyNode> sortedNodes = new ArrayList<CyNode>(nodes.size());
		int i, maxDegree = 0, degree;
		
		for (CyNode node: nodes) {
			degree = network.getNeighborList(node, Type.ANY).size();
			
			if (degree > maxDegree)
				maxDegree = degree;
		}
		
		i = maxDegree;
		while ((i >= 0) && (nodes.size() != sortedNodes.size())) {
			for (CyNode node: nodes) {
				degree = network.getNeighborList(node, Type.ANY).size();
				
				if (degree == i)
					sortedNodes.add(node);
			}
			
			i--;
		}
		
		return sortedNodes;
	}
	
	/**
   * Takes two lists of CyNodes and returns a list of the intersecting items.  
   * Uses local copies of parameters.
   * Note: Cytoscape has limited implementation of collection objects (no add/removeall methods supplied).
   *
   * @return a CyNode list of nodes in both parameter lists.
   */
  public static int sizeOfIntersection(List<CyNode> a, List<CyNode> b)
  {
  	int result = 0;
  	
  	for (CyNode nodeA: a) {
  		for (CyNode nodeB: b) {
  			if (nodeA.getSUID() == nodeB.getSUID()) {
  				result++;
  				break;
  			}
  		}
  	}
  	
  	return result;
  }
  
  public static int arrayMax(int[] property)
  {
  	int[] copy = property.clone();
    int max = 0;
    int numElements = copy.length;
    Arrays.sort(copy);
    
    if (numElements > 0)
      max = copy[numElements-1];

    return max;
  }
  
  public static int arrayMin(int[] property)
  {
  	int[] copy = property.clone();
    int min = 0;
    int numElements = copy.length;
    Arrays.sort(copy);
    
    if (numElements > 0)
      min = copy[0];

    return min;
  }
}
