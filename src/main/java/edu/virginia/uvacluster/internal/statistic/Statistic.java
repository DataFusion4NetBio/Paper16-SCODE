package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

import edu.virginia.uvacluster.internal.Cluster;

public abstract class Statistic {
	private StatisticRange range;
	protected String prefix = null;
	
	public Statistic(StatisticRange range, String prefix) {
		this.range = range;
		this.prefix = prefix; //TODO would also be better handled in featureutil
	}
	
	public abstract double transform(List<Double> values, Cluster cluster);	
	
	public StatisticRange getRange() {return range;}

	public int binTransform(List<Double> values, Cluster cluster) {
		return range.bin(transform(values, cluster));
	}

	public void train(List<Double> values, Cluster cluster) {
		range.train(transform(values, cluster));
	}
	
	public String getDescription(String name) {
		return prefix + " : " + name;
	}
	
	public boolean equals(Statistic s) {
		return (this.getClass() == s.getClass() && 
				range.getNumBins() == s.getRange().getNumBins());
	}
}
