package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

import edu.virginia.uvacluster.internal.Cluster;

public class Variance extends Statistic {
	
	public Variance(StatisticRange range) {
		super(range, "variance");
	}

	public double transform(List<Double> values, Cluster cluster) {
		return transform(values, new Mean(null).transform(values, cluster)); 
	}
	
	public double transform(List<Double> values, double mean)
	{
	  double variance = 0;
	  int numElements = values.size();
	
	  for (int i=0; i < numElements; i++) {
	    variance += (values.get(i) - mean) * (values.get(i) - mean);
	  }
	
	  if (numElements > 0)
	    variance = variance/numElements;
	    
	  return variance;
	}
}
