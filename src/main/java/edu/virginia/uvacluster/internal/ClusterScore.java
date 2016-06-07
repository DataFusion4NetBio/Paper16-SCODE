package edu.virginia.uvacluster.internal;

import java.util.ArrayList;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;

public class ClusterScore {
	public static double score(Cluster complex) {
		java.util.List<CyNode> nodes = complex.getNodes();
		java.util.List<CyEdge> edges = complex.getEdges();
	
		/* WITH NORMALIZATION */
//		ArrayList<Double> edgeWeights = new ArrayList<Double>();
//		double norm = 0;
//		for (CyEdge edge : edges) {
//			Double weightVal = 0.0;
//			try {
//				weightVal = complex.getRootNetwork().getDefaultEdgeTable().getRow(edge.getSUID()).get("weight", Double.class);
//			} catch (Exception e) {
//				// TODO 
//			}
//			edgeWeights.add(weightVal);
//			norm += weightVal * weightVal;
//		}
//		norm = Math.sqrt(norm);
//		
//		double score = 0.0;
//		for (Double d : edgeWeights) {
//			score += d / norm ;
//		}
//		score = score / edges.size();
//
//		return score;
		
		/* WITHOUT NORMALIZATION */
		Double sum = 0.0;
		for (CyEdge edge : edges) {
			sum += complex.getRootNetwork().getDefaultEdgeTable().getRow(edge.getSUID()).get("weight", Double.class);			
		}
		System.out.println("In ClusterScore: " + (sum / edges.size()) );
		
		return sum / edges.size();

	}
	
	public static double score(CySubNetwork complex) {
		Cluster cluster = new Cluster(null, complex);
		return score(cluster);
	}
}
