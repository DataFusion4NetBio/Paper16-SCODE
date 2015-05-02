package edu.virginia.uvacluster.internal.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.feature.Bin;
import edu.virginia.uvacluster.internal.feature.Degree;
import edu.virginia.uvacluster.internal.feature.EdgeWeight;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.statistic.Max;
import edu.virginia.uvacluster.internal.statistic.Mean;
import edu.virginia.uvacluster.internal.statistic.Median;
import edu.virginia.uvacluster.internal.statistic.StatisticRange;
import edu.virginia.uvacluster.internal.statistic.Unit;


public class ClusterTest extends TestNetwork {
	private Cluster cluster;
	
	public ClusterTest() {
		super();
	}
	
	//dispose will remove subnetwork
	@Before
	public void setup() {
		CySubNetwork subNetwork = null;
		List<FeatureSet> f = new ArrayList<FeatureSet>();
		f.add(new EdgeWeight(Arrays.asList(new Max(new StatisticRange(3)), 
										   new Mean(new StatisticRange(3))),"weight"));
		f.add(new Degree(Arrays.asList(new Median(new StatisticRange(4)), 
									   new Unit(new StatisticRange(4)))));
		try {
			subNetwork = getCliqueSubNetwork();
			cluster = new Cluster(f, subNetwork);
		} catch (Exception e) {
			e.printStackTrace();
			fail("Subnetwork constructor broke");
		}
	}
	
	@After
	public void cleanup() {
		cluster.destroy();
	}
	
	@Test
	public void shouldReturnNeighbors() {
		List<CyNode> neighbors = cluster.getNeighborList();
		assertEquals("Returns neighbor node a", true, neighbors.remove(a));
		assertEquals("Returns neighbor node c", true, neighbors.remove(c));
		assertEquals("No other neighbors", 0, neighbors.size());
	}
	
	@Test
	public void shouldAddNodeAndEdge() {
		assertEquals("a is not in cluster before add", false, cluster.getNodes().contains(a));
		assertEquals("ab is not in cluster before add", false, cluster.getEdges().contains(ab));
		try {
			cluster.add(a);
		} catch (Exception e) {
			e.printStackTrace();
			fail("add method threw exception");
		}
		assertEquals("Adds node a to cluster", true, cluster.getNodes().contains(a));
		assertEquals("Adds edge ab to cluster", true, cluster.getEdges().contains(ab));
	}
	
	@Test
	public void shouldRemoveNodeAndEdge() {
		assertEquals("e is in cluster before remove", true, cluster.getNodes().contains(e));
		assertEquals("be is in cluster before remove", true, cluster.getEdges().contains(be));
		assertEquals("de is in cluster before remove", true, cluster.getEdges().contains(de));
		try {
			cluster.remove(e);
		} catch (Exception e) {
			e.printStackTrace();
			fail("remove method threw exception");
		}
		assertEquals("e is removed", false, cluster.getNodes().contains(e));
		assertEquals("be is removed", false, cluster.getEdges().contains(be));
		assertEquals("de is removed", false, cluster.getEdges().contains(de));
	}
	
	@Test
	public void shouldReturnExpectedHash() {
		Map<String,Bin> featureMap = cluster.getBinMap();
		System.out.println(featureMap);
		assertEquals("Contains feature key",true,featureMap.containsKey("max : weight{weight}"));
		assertEquals("Contains feature key",true,featureMap.containsKey("mean : weight{weight}"));
		assertEquals("Contains feature key",true,featureMap.containsKey("median : degree"));
		assertEquals("Contains feature key",true,featureMap.containsKey("degree"));
		assertEquals("Contains four keys",4,featureMap.keySet().size());
	}
}
