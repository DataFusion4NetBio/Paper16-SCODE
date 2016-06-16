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
//		System.out.println("TRAINING STATISTIC RANGE on: " + val);
		if (val < min) {
//			System.out.println("Old min : " + min + "\nNew min : " + val);
			min = val;
		}
		if (val > max) {
//			System.out.println("Old max : " + max + "\nNew max : " + val);
			max = val;
			}
		span = max - min;
	}
	
	public int bin(double val) {
		double segmentSize = span / bins;
		int bin = bins; //default possibility
		for (int i = 1; i < bins; i++) {
//			if (val < min || val > max) {
//				bin = -1;
//				break;
//			}
			if (val <= (min + segmentSize * i)) {
				bin = i; 
				break;
			}
		}
		return bin;
	}
	public void setSpan(double min, double max) {
		this.min = min;
		this.max = max;
		span = max - min;
	}
	
	public double getMax() {return max;}
	public double getMin() {return min;}
	public int getNumBins() {return bins;}
}
