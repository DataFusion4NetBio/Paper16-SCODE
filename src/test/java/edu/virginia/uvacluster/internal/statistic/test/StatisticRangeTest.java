package edu.virginia.uvacluster.internal.statistic.test;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.virginia.uvacluster.internal.statistic.StatisticRange;

public class StatisticRangeTest {
	private StatisticRange range;
	
	public StatisticRangeTest() {
		range = new StatisticRange(4);
	}
	
	@Test
	public void test() {
		range.train(-11);
		range.train(9);
		range.train(4);
		range.train(-8);
		assertEquals("-20 is in bin 1", 1, range.bin(-20));
		assertEquals("-10 is in 1 bin", 1, range.bin(-10));
		assertEquals("-3 is in 2 bin", 2, range.bin(-3));
		assertEquals("0 is in 3 bin", 3, range.bin(0));
		assertEquals("3 is in 3 bin", 3, range.bin(3));
		assertEquals("7 is in 4 bin", 4, range.bin(7));
		assertEquals("80 is in 4 bin", 4, range.bin(80));
	}

}
