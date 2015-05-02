package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.ClusterUtil;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class TopologicalCoefficient extends FeatureSet {

	public TopologicalCoefficient(List<Statistic> statistics) {
		super("topological coefficient", statistics);
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> topologicalCoefficients = new ArrayList<Double>();
	    List<CyNode> nodes = cluster.getNodes(), otherNodes = new ArrayList<CyNode>();
	    List<CyNode> nodeNeighbors, otherNeighbors;
	    double topologicalCoefficient, intersection, numPartners;
	    
	    for (CyNode node: nodes) {
	    	otherNodes.clear();
	    	topologicalCoefficient = 0;
	    	numPartners = 0;
	    	otherNodes.addAll(nodes);
	    	otherNodes.remove(node);
	    	nodeNeighbors = cluster.getNeighbors(node);
	    	
	    	for (CyNode other: otherNodes) {
	    		intersection = 0;
	    		otherNeighbors = cluster.getNeighbors(other);
	    		intersection = ClusterUtil.sizeOfIntersection(nodeNeighbors, otherNeighbors);
	    		if (intersection > 0) {
	    			if (nodeNeighbors.contains(other)) intersection++;
	    			topologicalCoefficient += intersection;
	    			numPartners++;
	    		}
	    	}
	    	topologicalCoefficient /= numPartners;
	    	topologicalCoefficient /= cluster.getNeighbors(node).size();
	    	if (cluster.getNeighbors(node).size() <= 1) topologicalCoefficient = 0;
	    	topologicalCoefficients.add(topologicalCoefficient);
	    }
	    
	    return topologicalCoefficients;
	}
}
