package edu.virginia.uvacluster.internal.statistic.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.statistic.Median;
import edu.virginia.uvacluster.internal.statistic.StatisticRange;

public class MedianTest {
	private List<Double> values = Arrays.asList(0.0,1.0,2.0,-8.0,-5.0,3.0,4.0,11.0);
	private List<Double> oddNumOfValues = Arrays.asList(1.0,2.0,-8.0,-5.0,3.0,4.0,11.0);
	
//	@Test
//	public void shouldReturnMedian() {
//		assertEquals("Median should be 1.5", 1.5, new Median(new StatisticRange(3)).transform(values), 0.01);
//		assertEquals("Median should be 2", 2, new Median(new StatisticRange(3)).transform(oddNumOfValues), 0.01);
//	}
}
