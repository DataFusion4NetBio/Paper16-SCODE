package edu.virginia.uvacluster.internal.feature.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.feature.SingularValue;
import edu.virginia.uvacluster.internal.test.TestNetwork;

public class SingularValueTest extends TestNetwork {

	@Test
	public void shouldCalculateSingularValues() {
		List<Double> starResult, cliqueResult;
		FeatureSet sv = new SingularValue(null);
		Cluster cliqueCluster = new Cluster(Arrays.asList(sv), getCliqueSubNetwork());
		Cluster starCluster = new Cluster(Arrays.asList(sv), getStarSubNetwork());
		starResult = sv.computeInputs(starCluster);
		cliqueResult = sv.computeInputs(cliqueCluster);
		
		assertEquals("Should return the correct 1st singular value",2.0,cliqueResult.get(0),0.1);
		assertEquals("Should return the correct 2nd singular value",1.0,cliqueResult.get(1),0.1);
		assertEquals("Should return the correct 3rd singular value",1.0,cliqueResult.get(2),0.1);

		assertEquals("Should return the correct 1st singular value",1.732,starResult.get(0),0.001);
		assertEquals("Should return the correct 2nd singular value",1.732,starResult.get(1),0.001);
		assertEquals("Should return the correct 3rd singular value",0.0,starResult.get(2),0.1);
	}
}
