package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class DegreeCorrelation extends FeatureSet {

	public DegreeCorrelation(List<Statistic> statistics) {
		super("DegreeCorrelation", statistics);
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> degreeCorrelations = new ArrayList<Double>();
	    List<CyNode> nodes = cluster.getNodes();
	    List<CyNode> neighbors;
	    double degreeCorrelation = 0;
	    
	    for (CyNode node: nodes) {
	      neighbors = cluster.getNeighbors(node);
	      for (CyNode neighbor: neighbors) {
	      	degreeCorrelation = degreeCorrelation + cluster.getNeighbors(neighbor).size();
	      }
	      degreeCorrelations.add(degreeCorrelation / neighbors.size());
	      degreeCorrelation = 0;
	    }
	    return degreeCorrelations;
	}
}
