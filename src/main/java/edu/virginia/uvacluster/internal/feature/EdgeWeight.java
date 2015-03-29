package edu.virginia.uvacluster.internal.feature;

import java.util.List;

import edu.virginia.uvacluster.internal.statistic.Statistic;

public class EdgeWeight extends EdgeTableFeature{

	public EdgeWeight(List<Statistic> statistics, String propertyName) {
			super(statistics, propertyName);
			this.description = "weight{" + propertyName + "}";
	}

}
