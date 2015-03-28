package edu.virginia.uvacluster.internal.statistic;

import java.util.List;

public class Ordinal extends Statistic {
	private Integer ordinalNum;
	
	public Ordinal(StatisticRange range, int ordinalNum) {
		super(range,"Ordinal");
		this.ordinalNum = ordinalNum;
	}

	@Override
	public double transform(List<Double> values) {
		return values.get(getIndex());
	}

	@Override
	public String getDescription(String name) {
		String result = ordinalNum.toString();
		switch(ordinalNum % 10) {
		case 1: result += "st";
				break;
		case 2: result += "nd";
				break;
		case 3: result += "rd";
				break;
        default: result += "th";
        		 break;
		}
		return result;
	}
	
	public int getIndex() {return ordinalNum-1;}
	
}
