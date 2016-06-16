package edu.virginia.uvacluster.internal.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.virginia.uvacluster.internal.Cluster;

public class Max extends Statistic {

	public Max(StatisticRange range) {
		super(range, "max");
	}

	public double transform(List<Double> values, Cluster cluster) {
		ArrayList<Double> copy = new ArrayList<Double>(values);
	    double max = 0;
	    int numElements = copy.size();
	    Collections.sort(copy);
	    
	    if (numElements > 0)
	      max = copy.get(numElements-1);

	    return max;
	}
}
