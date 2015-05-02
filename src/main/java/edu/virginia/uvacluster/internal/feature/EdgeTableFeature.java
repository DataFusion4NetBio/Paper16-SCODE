package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class EdgeTableFeature extends FeatureSet {
	protected String propertyName = null;
	
	public EdgeTableFeature(List<Statistic> statistics, String propertyName) {
		super("edge{"+propertyName+"}", statistics);
		this.propertyName = propertyName;
	}
	
	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> result = new ArrayList<Double>();
		List<CyEdge> edges = cluster.getEdges();
		CyTable table = cluster.getRootNetwork().getSharedEdgeTable();
		for (CyEdge edge: edges)
			result.add(table.getRow(edge.getSUID()).get(propertyName, Double.class));
		return result;
	}

	public String getPropertyName() {return propertyName;}
}
