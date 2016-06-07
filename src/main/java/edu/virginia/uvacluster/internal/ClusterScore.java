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
		ArrayList<Double> edgeWeights = new ArrayList<Double>();
		Double norm = 0.0;
		for (CyEdge edge : edges) {
			Double weightVal = 0.0;
			try {
				weightVal = complex.getRootNetwork().getDefaultEdgeTable().getRow(edge.getSUID()).get("weight", Double.class);
			} catch (Exception e) {
				// TODO 
			}
			edgeWeights.add(weightVal);
			norm += weightVal * weightVal;
		}
		norm = Math.sqrt(norm);
		
		Double score = 0.0;
		for (Double d : edgeWeights) {
			score += (d / norm) ;
		}
		System.out.println("In ClusterScore: " + score);
		score = score  / nodes.size();
		System.out.println("In ClusterScore: " + score);
		if (score > 0.9) {
			System.out.println("\t\tEdge list size: " + edges.size());
			System.out.println("\t\tNode list size: " + nodes.size());
			System.out.println("\t\tNorm: " + norm);
		}
		return score * 10 ;
		/* WITHOUT NORMALIZATION */
//		Double sum = 0.0;
//		for (CyEdge edge : edges) {
//			sum += complex.getRootNetwork().getDefaultEdgeTable().getRow(edge.getSUID()).get("weight", Double.class);			
//		}
//		
//		return sum / edges.size();

	}
	
	public static double score(CySubNetwork complex) {
		Cluster cluster = new Cluster(null, complex);
		return score(cluster);
	}
}
