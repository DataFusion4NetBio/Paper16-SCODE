package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

import edu.virginia.uvacluster.internal.Cluster;

public class MeanPossible extends Statistic {

	public MeanPossible(StatisticRange range) {
		super(range, "mean");
	}
	
	public double transform(List<Double> values, Cluster cluster) {
	    double sum = 0;
	    int clusterSize = cluster.size();
	    double possibleEdges = clusterSize * (clusterSize - 1) / 2;
	    
	    for (Double value: values)
	    {
	      sum += value;
	    }
	    
	    return sum / possibleEdges;
	}

}
