package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

public class Variance extends Statistic {
	
	public Variance(StatisticRange range) {
		super(range, "Variance");
	}

	public double transform(List<Double> values) {
		return transform(values, new Mean(null).transform(values)); 
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
