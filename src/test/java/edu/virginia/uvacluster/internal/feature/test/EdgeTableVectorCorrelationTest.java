package edu.virginia.uvacluster.internal.feature.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.feature.EdgeTableVectorCorrelation;
import edu.virginia.uvacluster.internal.test.TestNetwork;

public class EdgeTableVectorCorrelationTest extends TestNetwork {

	@Test
	public void shouldCalculateCorreationBetweenColumns() {
		List<Double> result;
		FeatureSet correlation = new EdgeTableVectorCorrelation(null, 
									Arrays.asList("weight","testA","testB"));
		Cluster cluster = new Cluster(Arrays.asList(correlation), getCorrelationSubNetwork());
		result = correlation.computeInputs(cluster);
		Collections.sort(result);
		
		assertEquals("Shuold compute correlation between weight and test",0.62,result.get(0),0.001);
	}
}
