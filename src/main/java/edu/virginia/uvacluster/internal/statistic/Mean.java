package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

public class Mean extends Statistic {

	public Mean(StatisticRange range) {
		super(range, "Mean");
	}

	public double transform(List<Double> values) {
		double mean = 0;
	    double sum = 0;
	    double numElements = values.size();

	    for (Double value: values)
	    {
	      sum += value;
	    }

	    if (numElements > 0)
	      mean = sum/numElements;
	    
	    return mean;
	}
}
