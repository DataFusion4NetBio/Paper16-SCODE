package edu.virginia.uvacluster.internal.feature.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.feature.DegreeCorrelation;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.test.TestNetwork;

public class DegreeCorrelationTest extends TestNetwork {

	@Test
	public void shouldCalculteDegreeCorrelation() {
		List<Double> result;
		FeatureSet dc = new DegreeCorrelation(null);
		Cluster cluster = new Cluster(Arrays.asList(dc), getCompleteSubNetwork());
		result = dc.computeInputs(cluster);
		Collections.sort(result);
		
		assertEquals("Should calculate degree correlation value",1.5,result.get(0),0.001);
		assertEquals("Should calculate degree correlation value",3.0,result.get(1),0.001);
		assertEquals("Should calculate degree correlation value",3.0,result.get(2),0.001);
		assertEquals("Should calculate degree correlation value",4.0,result.get(3),0.001);
		assertEquals("Should calculate degree correlation value",4.0,result.get(4),0.001);
	}

}
