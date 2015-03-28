package edu.virginia.uvacluster.internal.statistic;

public class StatisticRange {
	private double min, max, span;
	private int bins;
	
	public StatisticRange(int bins) {
		this.bins = bins;
		min = Double.MAX_VALUE; 
		max = Double.MIN_VALUE; 
		span = 0; 
	}
	
	public void train(double val) {
		if (val < min)
			min = val;
		else if (val > max)
			max = val;
		span = max - min;
	}
	
	public int bin(double val) {
		double segmentSize = span / bins;
		int bin = bins; //default possibility
		for (int i = 1; i < bins; i++) {
			if (val <= (min + segmentSize * i)) {
				bin = i; 
				break;
			}
		}
		return bin;
	}
	
	public int getNumBins() {return bins;}
}
