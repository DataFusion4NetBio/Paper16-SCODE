package edu.virginia.uvacluster.internal.feature.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.feature.ClusterCoefficient;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.test.TestNetwork;

public class ClusterCoefficientTest extends TestNetwork {

	@Test
	public void shouldCalculateClusterCoefficients() {
		List<Double> result;
		FeatureSet cc = new ClusterCoefficient(null);
		Cluster cluster = new Cluster(Arrays.asList(cc), getCompleteSubNetwork());
		result = cc.computeInputs(cluster);
		Collections.sort(result);
		
		assertEquals("Results should match",0.0,result.get(0), 0.001);
		assertEquals("Results should match",0.0,result.get(1), 0.001);
		assertEquals("Results should match",0.1666,result.get(2), 0.001);
		assertEquals("Results should match",1.0,result.get(3), 0.001);
		assertEquals("Results should match",1.0,result.get(4), 0.001);
		assertEquals("Results length should match node count",5,result.size());
	}

}
