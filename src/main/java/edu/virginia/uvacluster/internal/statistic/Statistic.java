package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

public abstract class Statistic {
	private StatisticRange range;
	protected String prefix = null;
	
	public Statistic(StatisticRange range, String prefix) {
		this.range = range;
		this.prefix = prefix; //TODO would also be better handled in featureutil
	}
	
	public abstract double transform(List<Double> values);	
	
	public StatisticRange getRange() {return range;}

	public int binTransform(List<Double> values) {
		return range.bin(transform(values));
	}

	public void train(List<Double> values) {
		range.train(transform(values));
	}
	
	public String getDescription(String name) {
		return prefix + " : " + name;
	}
	
	public boolean equals(Statistic s) {
		return (this.getClass() == s.getClass() && 
				range.getNumBins() == s.getRange().getNumBins());
	}
}
