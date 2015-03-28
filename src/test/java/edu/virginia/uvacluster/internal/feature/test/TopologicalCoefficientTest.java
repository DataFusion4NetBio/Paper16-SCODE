package edu.virginia.uvacluster.internal.feature.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.feature.TopologicalCoefficient;
import edu.virginia.uvacluster.internal.test.TestNetwork;

public class TopologicalCoefficientTest extends TestNetwork{

	@Test
	public void shouldCalculateTopologicalCoefficients() {
		List<Double> result;
		FeatureSet tc = new TopologicalCoefficient(null);
		Cluster cluster = new Cluster(Arrays.asList(tc), getCompleteSubNetwork());
		result = tc.computeInputs(cluster);
		Collections.sort(result);
		
		assertEquals("Topological Co's should be correct",0.0,result.get(0),0.001);
		assertEquals("Topological Co's should be correct",0.0,result.get(1),0.001);
		assertEquals("Topological Co's should be correct",0.5,result.get(2),0.001);
		assertEquals("Topological Co's should be correct",0.75,result.get(3),0.001);
		assertEquals("Topological Co's should be correct",0.75,result.get(4),0.001);
		assertEquals("Result should have 5 doubles",5,result.size());
	}

}
