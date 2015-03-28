package edu.virginia.uvacluster.internal.feature.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.feature.Density;
import edu.virginia.uvacluster.internal.feature.EdgeWeight;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.test.TestNetwork;

public class DensityTest extends TestNetwork {
	public EdgeWeight edgeWeight = new EdgeWeight(null, "weight");
	
	@Test
	public void shouldCalculateDensityWithCutoff() {
		List<Double> result;
		FeatureSet densityWithCutoff = new Density(null,1.0);
		Cluster clusterWithCutoff = 
				new Cluster(Arrays.asList(densityWithCutoff, edgeWeight), getCompleteSubNetwork());
		result = densityWithCutoff.computeInputs(clusterWithCutoff);
		assertEquals("Density with a cutoff should be calculated",0.2,result.get(0),0.001);
		assertEquals("There should be 1 density value",1,result.size());
	}
	
	@Test
	public void shouldCalculateDensity() {
		List<Double> result;
		FeatureSet density = new Density(null);
		Cluster cluster = new Cluster(Arrays.asList(density, edgeWeight), getCompleteSubNetwork());
		result = density.computeInputs(cluster);
		
		assertEquals("Density should be calculated",0.5,result.get(0),0.001);
		assertEquals("There should be 1 density value",1,result.size());
	}

}
