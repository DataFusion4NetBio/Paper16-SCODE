package edu.virginia.uvacluster.internal.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.cytoscape.model.CyNode;

import edu.virginia.uvacluster.internal.ClusterUtil;

public class ClusterUtilTest extends TestNetwork {

	private int[] intValues = {0,1,2,-8,-5,3,4,11};
	private List<CyNode> firstList, secondList, zeroList, sortableList;
	
	public ClusterUtilTest() {
		super();
		
		firstList = Arrays.asList(a,b,c,d);
		secondList = Arrays.asList(b,c,d,e);
		sortableList = Arrays.asList(a,b,d);
		zeroList = Arrays.asList(f);
	}
	
	@Test
	public void shouldReturnMax() {
		assertEquals("Max should be 11", 11, ClusterUtil.arrayMax(intValues));
	}
	
	@Test
	public void shouldReturnMin() {
		assertEquals("Min should be -8", -8, ClusterUtil.arrayMin(intValues));
	}
	
	@Test
	public void intersectionShouldBeZero() {
		assertEquals("There should be no intersection with first.", 
					 0, ClusterUtil.sizeOfIntersection(firstList, zeroList));
		assertEquals("There should be no intersection with second.", 
				 0, ClusterUtil.sizeOfIntersection(secondList, zeroList));
	}
	
	@Test
	public void intersectionShouldBeThree() {
		assertEquals("Intersection of first and second should be 3.", 
				 3, ClusterUtil.sizeOfIntersection(firstList, secondList));
	}
	
	@Test
	public void shouldReturnHighestDegreeNodes() {
		List<CyNode> result = ClusterUtil.getTopDegreeNodes(network, 3);
		assertEquals("List should contain b", true, result.contains(b));
		assertEquals("List should contain d", true, result.contains(d));
		assertEquals("List should contain e", true, result.contains(e));
		assertEquals("List should not contain a", false, result.contains(a));
		assertEquals("List should not contain c", false, result.contains(c));
		assertEquals("List should not contain f", false, result.contains(f));
	}
	
	@Test
	public void workShouldBeDividedEvenly() {
		List<CyNode> jobs = new ArrayList<CyNode>();
		jobs.addAll(firstList); jobs.addAll(secondList);
		List<List<CyNode>> result= ClusterUtil.divideWork(jobs, 4);
		assertEquals("All sublists should have length 2", true, (result.get(0).size() == 2) &&
																(result.get(0).size() == 2) &&
																(result.get(0).size() == 2) &&
																(result.get(0).size() == 2));
	}
	
	@Test
	public void nodesShouldBeSortedByDegree() {
		List<CyNode> result = ClusterUtil.sortByDegree(network, sortableList);
		assertEquals("First node is b", b, result.remove(0));
		assertEquals("First node is d", d, result.remove(0));
		assertEquals("First node is a", a, result.remove(0));
		assertEquals("No more nodes in list", 0, result.size());
	}
	
	@Test
	public void shouldRetrieveNodeOfGreatestDegree() {
		List<CyNode> result = ClusterUtil.getTopDegreeNodes(network, 3);
		assertEquals("First node is b", true, result.remove(b));
		assertEquals("First node is b", true, result.remove(d));
		assertEquals("First node is b", true, result.remove(e));
		assertEquals("No more nodes in list", 0, result.size());
	}
}
