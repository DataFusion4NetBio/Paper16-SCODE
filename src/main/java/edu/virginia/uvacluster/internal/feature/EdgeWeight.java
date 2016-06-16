package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyTable;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class EdgeWeight extends EdgeTableFeature{

	public EdgeWeight(List<Statistic> statistics, String propertyName) {
			super(statistics, propertyName);
			this.description = "weight{" + propertyName + "}";
	}

}
