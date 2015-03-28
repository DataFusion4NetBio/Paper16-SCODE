package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

public class Unit extends Statistic{

	public Unit(StatisticRange range) {
		super(range, "Unit");
	}

	@Override
	public double transform(List<Double> values) {
		return values.get(0);
	}

	@Override
	public String getDescription(String name) {
		return name;
	}

}
