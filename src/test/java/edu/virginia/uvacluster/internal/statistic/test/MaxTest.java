package edu.virginia.uvacluster.internal.statistic.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.statistic.Max;
import edu.virginia.uvacluster.internal.statistic.StatisticRange;

public class MaxTest {
	private List<Double> values = Arrays.asList(0.0,1.0,2.0,-8.0,-5.0,3.0,4.0,11.0);
	
	@Test
	public void shouldReturnMax() {
		assertEquals("Max should be 11", 11, new Max(new StatisticRange(3)).transform(values), 0.01);
	}
}
