package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

import edu.virginia.uvacluster.internal.Cluster;

public class Count extends Statistic {

	public Count(StatisticRange range) {
		super(range,"count");
	}

	@Override
	public double transform(List<Double> values, Cluster cluster) {
		return values.size();
	}
}
