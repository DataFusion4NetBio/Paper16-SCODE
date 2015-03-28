package edu.virginia.uvacluster.internal.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Median extends Statistic {

	public Median(StatisticRange range) {
		super(range, "Median");
	}

	public double transform(List<Double> values) {
		ArrayList<Double> copy = new ArrayList<Double>(values);
	    double median = 0;
	    int numElements = copy.size();
	    Collections.sort(copy);

	    if (numElements > 0) {
	      if ((numElements % 2) == 1) //odd number of elements
	        median = copy.get((numElements-1)/2);
	      else //even number of elements
	        median = (copy.get(numElements/2) + copy.get((numElements/2)-1))/2;
	    }

	    return median;
	}
}
