package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;
import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class ClusterCoefficient extends FeatureSet {

	public ClusterCoefficient(List<Statistic> statistics) {
		super("clustering coefficient", statistics);
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> clusteringCoefficients = new ArrayList<Double>();
	    List<CyNode> nodes = cluster.getNodes();
	    List<CyNode> neighbors;
	    double clusteringCoefficient, numNeighbors, links;
	    
	    for (CyNode node: nodes) {
	    	links = 0;
	    	clusteringCoefficient = 0;
	    	neighbors = cluster.getNeighbors(node);
	  		numNeighbors = neighbors.size();
	      
	  		for (CyNode x: neighbors) {
	  			for (CyNode y: neighbors) {
	  				if (cluster.containsEdge(x, y)) links++;
	  			}
	  		}
	  		
	  	  links /= 2.0; //each link/edge is found twice in the for loops
	  	  if (numNeighbors>1) 
	  		  clusteringCoefficient = (2 * links) / (numNeighbors * (numNeighbors - 1));
	      clusteringCoefficients.add(clusteringCoefficient);
	    }
	    
	    return clusteringCoefficients;
	}

}
