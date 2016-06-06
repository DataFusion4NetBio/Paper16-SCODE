package edu.virginia.uvacluster.internal;

import java.util.*;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyEdge.*;
import org.cytoscape.model.subnetwork.*;

import edu.virginia.uvacluster.internal.feature.*;

/**
 * Used to keep track of information about clusters and their subgraphs.
 */
public class Cluster
{
  private CyRootNetwork rootNetwork;
  private CySubNetwork clusterNetwork;
  public boolean searchComplete = false;
  private List<FeatureSet> features;
  private EdgeWeight edgeWeights;
  

  //*************Constructors:
  public Cluster(List<FeatureSet> features, CySubNetwork network) {
	 this.features = features;
	 edgeWeights = getEdgeWeightFeature(features);
	 rootNetwork = network.getRootNetwork();
	 clusterNetwork = network;
  }
  
  //*************Destructor
  /**
   * Removes the cluster's CySubNetwork, hopefully freeing its memory.
   */
  public void destroy() {
    if (clusterNetwork != null ) {
    	rootNetwork.removeSubNetwork(clusterNetwork);
    }
  }

  //*************Public Methods:
  public int size() {return clusterNetwork.getNodeCount();}
  public List<CyNode> getNodes() {return clusterNetwork.getNodeList();}
  public List<CyEdge> getEdges() {return clusterNetwork.getEdgeList();}
  public List<CyNode> getNeighbors(CyNode node) {return clusterNetwork.getNeighborList(node, Type.ANY);}
  public long getSUID() {return clusterNetwork.getSUID();}
  public CySubNetwork getSubNetwork() {return clusterNetwork;}
  public CyRootNetwork getRootNetwork() {return rootNetwork;}
  public boolean containsEdge(CyNode a, CyNode b) {return clusterNetwork.containsEdge(a, b);}
  public List<Double> getEdgeWeights() {
	  if (edgeWeights != null) 
		  return edgeWeights.computeInputs(this);
	  else
		  return null;
  }
  
  
  /**
   * Returns integer array of property segments, used for calculating probabilities...
   */
  public List<Integer> getFeatureBins() {
	List<Integer> bins = new ArrayList<Integer>();
	for (FeatureSet feature: features) {
		bins.addAll(feature.getBinnedValues(this));
	}
	return bins;
  }
  
  public List<Double> getFeatureValues() {
	List<Double> values = new ArrayList<Double>();
	for (FeatureSet feature: features) {
		values.addAll(feature.getValues(this));
	}
	return values;
  }
  
  public Map<String, Bin> getBinMap() {
	HashMap<String, Bin> result = new HashMap<String, Bin>();
	for (FeatureSet feature: features) {
		result.putAll(feature.getBinMap(this));
	}
 	return result;
  }
  
  public void trainBinning() {
	  for (FeatureSet f: features) {
		  f.train(this);
	  }
  }
  
  /**
   * Returns list of CyNode's that neighbor the complex.
   * 
   * @return list of neighboring nodes to the complex.
   */
  public List<CyNode> getNeighborList() {
  	List<CyNode> results = new ArrayList<CyNode>(20);
  	List<CyNode> complexNodes = null;
	try {
		complexNodes = clusterNetwork.getNodeList();
	} catch (Exception e) {
		e.printStackTrace();
	}
  	List<CyNode> potentialNeighbors = null;
  
  	for (CyNode node: complexNodes) {
  		potentialNeighbors = rootNetwork.getNeighborList(node, Type.ANY);
  		
  		for (CyNode neighbor: potentialNeighbors) {
  			if ((! complexNodes.contains(neighbor)) && (! results.contains(neighbor))) {
  				results.add(neighbor);
  			}
  		}
  	}
  	
  	return results;
  }
  
  /** Adds a (neighboring) node and any edges joining that node to the complex subnetwork
   * 
   * @param node the (neighboring) node to be added
   * @return true if successful
   */
  public boolean add(CyNode node) throws Exception {
    List<CyNode> nodeList = clusterNetwork.getNodeList();
    List<CyEdge> connectingEdgeList = null;
    boolean successState = false;
    		
    for (CyNode complexNode: nodeList) {
    	connectingEdgeList = rootNetwork.getConnectingEdgeList(node, complexNode, Type.ANY);
    	
    	for (CyEdge edge: connectingEdgeList) {
    		clusterNetwork.addEdge(edge);
    	}
    }
    
    successState = clusterNetwork.addNode(node);
    
    return successState;
  }
  
  /** Removes a node and any edges joining that node to the complex subnetwork.
   * 
   * @param node that you want to remove
   * @return true if successful
   */
  public boolean remove(CyNode node) throws Exception {
    List<CyNode>	removeList = new ArrayList<CyNode>();
    List<CyNode> nodeList = clusterNetwork.getNodeList();
    List<CyEdge> connectingEdgeList = null;
    boolean successState = false;
    		
    for (CyNode complexNode: nodeList) {
    	connectingEdgeList = rootNetwork.getConnectingEdgeList(node, complexNode, Type.ANY);
    	clusterNetwork.removeEdges(connectingEdgeList);
    }
    
    removeList.add(node);
    successState = clusterNetwork.removeNodes(removeList);
    
    return successState;
  }
  
  private EdgeWeight getEdgeWeightFeature(List<FeatureSet> features) {
	  EdgeWeight result = null;
	  if (features != null) {
		  for (FeatureSet feature: features) {
			  if (feature instanceof EdgeWeight)
				  result = (EdgeWeight)feature;
		  }
	  }
	  return result;
  }
}