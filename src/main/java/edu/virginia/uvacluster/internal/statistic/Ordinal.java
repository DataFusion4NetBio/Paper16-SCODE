package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

import edu.virginia.uvacluster.internal.Cluster;

public class Ordinal extends Statistic {
	private Integer ordinalNum;
	
	public Ordinal(StatisticRange range, int ordinalNum) {
		super(range,"Ordinal");
		this.ordinalNum = ordinalNum;
	}

	@Override
	public double transform(List<Double> values, Cluster cluster) {
		return values.get(getIndex());
	}

	@Override
	public String getDescription(String name) {
		String prefix = ordinalNum.toString();
		switch(ordinalNum % 10) {
		case 1: prefix += "st";
				break;
		case 2: prefix += "nd";
				break;
		case 3: prefix += "rd";
				break;
        default: prefix += "th";
        		 break;
		}
		return prefix + " : " + name;
	}
	
	public int getIndex() {return ordinalNum-1;}
	
}
