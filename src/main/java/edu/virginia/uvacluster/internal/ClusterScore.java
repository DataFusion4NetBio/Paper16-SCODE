package edu.virginia.uvacluster.internal;

import java.util.ArrayList;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;

public class ClusterScore {
	public static double score(Cluster complex, Model model) throws Exception {
		if (model != null) {
			// Training
			return model.score(complex);
		} else {
			 // No training  - simple search
			java.util.List<CyNode> nodes = complex.getNodes();
			java.util.List<CyEdge> edges = complex.getEdges();
		
			/* WITH NORMALIZATION */
			ArrayList<Double> edgeWeights = new ArrayList<Double>();
			Double norm = 0.0;
			for (CyEdge edge : edges) {
				Double weightVal = complex.getRootNetwork().getDefaultEdgeTable().getRow(edge.getSUID()).get("weight", Double.class);
				if (weightVal == null) {
					// If the weight column is missing, set weight to 1.0
					weightVal = 1.0;
				}
	
				edgeWeights.add(weightVal);
				norm += weightVal * weightVal;
			}
			norm = Math.sqrt(norm);
			
			// Score: Sum of the normalized edges divided by the number of nodes (avg weight per node)
			Double score = 0.0;
			for (Double d : edgeWeights) {
				score += (d / norm) ;
			}
			score = score  / nodes.size();
	
			return score ;

		}
	}
	
	public static double score(CySubNetwork complex, Model model) throws Exception {
		Cluster cluster = new Cluster(null, complex);
		return score(cluster, model);
	}
}
