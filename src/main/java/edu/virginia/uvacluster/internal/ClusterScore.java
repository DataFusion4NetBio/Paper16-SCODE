package edu.virginia.uvacluster.internal;

import java.util.ArrayList;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;

public class ClusterScore {
	public static double score(Cluster complex) {
		java.util.List<CyNode> nodes = complex.getNodes();
		java.util.List<CyEdge> edges = complex.getEdges();
		
		ArrayList<Double> edgeWeights = new ArrayList<Double>();
		double norm = 0;
		for (CyEdge edge : edges) {
			Double weightVal = complex.getRootNetwork().getDefaultEdgeTable().getRow(edge.getSUID()).get("weight", Double.class);
			edgeWeights.add(weightVal);
			norm += weightVal * weightVal;
		}
		norm = Math.sqrt(norm);
		
		double score = 0.0;
		for (Double d : edgeWeights) {
//			System.out.println("Normalized edge weight: " + d / norm + " vs. original edge weight: " + d);
			score += d / norm ;
		}
//		System.out.println("Total score: " + score);
		
		return score;
		
		///////////////////////////////////////////////////////
//		ArrayList<Double> edgeWeights = new ArrayList<Double>();
//		double sumWeights = 0;
//		for (CyEdge edge : edges) {
//			Double weightVal = complex.getRootNetwork().getDefaultEdgeTable().getRow(edge.getSUID()).get("weight", Double.class);
//			edgeWeights.add(weightVal);
//			sumWeights += weightVal;
//		}
//		
//		double mean = sumWeights / edgeWeights.size();
//		double sumDiffsSquared = 0;
//		for (Double d : edgeWeights) {
//			sumDiffsSquared += Math.pow(Math.abs(d - mean), 2);
//		}
//		double stdDev = Math.sqrt(sumDiffsSquared / mean);
//		
//		double score = 0;
//		for (Double d : edgeWeights) {
//			double z = d - mean / stdDev;
//			score += z ; 
//		}
//		return score;
		
	}
	
	public static double score(CySubNetwork complex) {
		Cluster cluster = new Cluster(null, complex);
		return score(cluster);
	}
}
