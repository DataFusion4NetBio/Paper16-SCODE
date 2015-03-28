package edu.virginia.uvacluster.internal.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Max extends Statistic {

	public Max(StatisticRange range) {
		super(range, "Max");
	}

	public double transform(List<Double> values) {
		ArrayList<Double> copy = new ArrayList<Double>(values);
	    double max = 0;
	    int numElements = copy.size();
	    Collections.sort(copy);
	    
	    if (numElements > 0)
	      max = copy.get(numElements-1);

	    return max;
	}
}
