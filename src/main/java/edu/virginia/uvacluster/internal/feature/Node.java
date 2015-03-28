package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class Node extends FeatureSet {

	public Node(List<Statistic> statistics) {
		super("Node", statistics);
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> result = new ArrayList<Double>();
	    List<CyNode> nodes = cluster.getNodes();
		for (int i = 0; i < nodes.size(); i++)
            result.add(1.0);
		return result;
	}
}
