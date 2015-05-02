package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class NodeTableFeature extends FeatureSet {
	protected String propertyName = null;
	
	public NodeTableFeature(List<Statistic> statistics, String propertyName) {
		super("node{"+propertyName+"}", statistics);
		this.propertyName = propertyName;
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> result = new ArrayList<Double>();
		List<CyNode> nodes = cluster.getNodes();
		CyTable table = cluster.getRootNetwork().getSharedNodeTable();
		for (CyNode node: nodes)
			result.add(table.getRow(node.getSUID()).get(propertyName, Double.class));
		return result;
	}
	
	public String getPropertyName() {return propertyName;}
}
