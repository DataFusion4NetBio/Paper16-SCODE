package edu.virginia.uvacluster.internal.statistic.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.statistic.StatisticRange;
import edu.virginia.uvacluster.internal.statistic.Variance;

public class VarianceTest {
	private List<Double> values = Arrays.asList(0.0,1.0,2.0,-8.0,-5.0,3.0,4.0,11.0);
	private List<Double> oddNumOfValues = Arrays.asList(1.0,2.0,-8.0,-5.0,3.0,4.0,11.0);
	
	@Test
	public void shouldReturnVariance() {
		assertEquals("Variance should be 29", 29, new Variance(new StatisticRange(3)).transform(values), 0.01);
		assertEquals("Variance should be 32.98", 32.98, new Variance(new StatisticRange(3)).transform(oddNumOfValues), 0.01);
		assertEquals("Variance should be 29 with manual mean", 29, new Variance(new StatisticRange(3)).transform(values, 1), 0.01);
	}
}
