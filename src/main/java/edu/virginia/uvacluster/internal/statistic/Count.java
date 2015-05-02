package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

public class Count extends Statistic {

	public Count(StatisticRange range) {
		super(range,"count");
	}

	@Override
	public double transform(List<Double> values) {
		return values.size();
	}
}
