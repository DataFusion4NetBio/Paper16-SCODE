package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class EdgeTableVectorCorrelation extends NodeTableVectorCorrelation{
	
	public EdgeTableVectorCorrelation(List<Statistic> statistics, List<String> propertyNames) {
		super(statistics, propertyNames);
		description = "edge{" + join(propertyNames) + "}";
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<List<Double>> vectors = new ArrayList<List<Double>>();
		List<Double> vec;
		List<CyEdge> edges = cluster.getEdges();
		CyTable table = cluster.getRootNetwork().getSharedEdgeTable();

		for (CyEdge edge: edges) {
			vec = new ArrayList<Double>();
			for (String prop: propertyNames) 
				vec.add(table.getRow(edge.getSUID()).get(prop, Double.class));
			vectors.add(vec);
		}
		
		return computeDistances(vectors);
	}
}
