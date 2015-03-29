package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class Degree extends FeatureSet {

	public Degree(List<Statistic> statistics) {
		super("degree", statistics);
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> result = new ArrayList<Double>();
	    List<CyNode> nodes = cluster.getNodes();
		for (CyNode node: nodes)
            result.add((double) cluster.getNeighbors(node).size());
		return result;
	}

}
