package edu.virginia.uvacluster.internal.feature.test;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import edu.virginia.uvacluster.internal.feature.*;
import edu.virginia.uvacluster.internal.statistic.*;

public class FeatureUtilTest {
	
	@Test
	public void shouldParseFeatures() {
		FeatureSet feature;
		
		feature = FeatureUtil.parse(getFeatureDesc("Max:Clustering Coefficient (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof ClusterCoefficient);
		
		feature = FeatureUtil.parse(getFeatureDesc("Max :Degree (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof Degree);
		
		feature = FeatureUtil.parse(getFeatureDesc("Max: Degree Correlation (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof DegreeCorrelation);
		
		feature = FeatureUtil.parse(getFeatureDesc("Density (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof Density);
		
		feature = FeatureUtil.parse(getFeatureDesc("Density wrt cutoff of 1.2 (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof Density);
		assertEquals("Parses cutoff corectly", 1.2, ((Density)feature).getCutoff(), 0.001);
		
		feature = FeatureUtil.parse(getFeatureDesc("Max  : edge{colName} (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof EdgeTableFeature);
		assertEquals("Parses column name corectly", "colName", ((EdgeTableFeature)feature).getPropertyName());
		
		feature = FeatureUtil.parse(getFeatureDesc("Max : edge{col1,col2} (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof EdgeTableVectorCorrelation);
		assertEquals("Parses column names corectly", true, Arrays.asList("col1","col2").containsAll(
				((EdgeTableVectorCorrelation)feature).getPropertyNames()));
		
		feature = FeatureUtil.parse(getFeatureDesc("Max : weight{colName} (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof EdgeWeight);
		
		feature = FeatureUtil.parse(getFeatureDesc("Count : Node (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof Node);
		
		feature = FeatureUtil.parse(getFeatureDesc("Max : node{colName} (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof NodeTableFeature);
		
		feature = FeatureUtil.parse(getFeatureDesc("Max : node{col1,col2} (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof NodeTableVectorCorrelation);
		
		feature = FeatureUtil.parse(getFeatureDesc("1st : Singular Value (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof SingularValue);
		
		feature = FeatureUtil.parse(getFeatureDesc("Max : Topological Coefficient (1)")).get(0);
		assertEquals("Parses corectly", true, feature instanceof TopologicalCoefficient);
	}

	@Test
	public void shouldParseStatistics() {
		Statistic stat;
		
		stat = FeatureUtil.parse(getFeatureDesc("Density (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Unit);
		
		stat = FeatureUtil.parse(getFeatureDesc("Count: Node (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Count);
		
		stat = FeatureUtil.parse(getFeatureDesc("Max :Degree (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Max);
		
		stat = FeatureUtil.parse(getFeatureDesc("Mean  : Degree (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Mean);
		
		stat = FeatureUtil.parse(getFeatureDesc("Median : Degree (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Median);
		
		stat = FeatureUtil.parse(getFeatureDesc("Variance : Degree (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Variance);
		
		stat = FeatureUtil.parse(getFeatureDesc("1st : Singular Value (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Ordinal);
		assertEquals("Parses ordinal corectly", 0, ((Ordinal)stat).getIndex());
		stat = FeatureUtil.parse(getFeatureDesc("2nd : Singular Value (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Ordinal);
		assertEquals("Parses ordinal corectly", 1, ((Ordinal)stat).getIndex());
		stat = FeatureUtil.parse(getFeatureDesc("3rd : Singular Value (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Ordinal);
		assertEquals("Parses ordinal corectly", 2, ((Ordinal)stat).getIndex());
		stat = FeatureUtil.parse(getFeatureDesc("4th : Singular Value (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", true, stat instanceof Ordinal);
		assertEquals("Parses ordinal corectly", 3, ((Ordinal)stat).getIndex());
	}
	
	@Test
	public void shouldParseBins() {
		Statistic stat;
		
		stat = FeatureUtil.parse(getFeatureDesc("Density (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", 1, stat.getRange().getNumBins());
		
		stat = FeatureUtil.parse(getFeatureDesc("Density wrt cutoff 2 (1)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", 1, stat.getRange().getNumBins());
		
		stat = FeatureUtil.parse(getFeatureDesc("Mean  : Degree (3)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", 3, stat.getRange().getNumBins());
		
		stat = FeatureUtil.parse(getFeatureDesc("4th : Singular Value (5)")).get(0).getStatistics().get(0);
		assertEquals("Parses corectly", 5, stat.getRange().getNumBins());
	}
	
	private Set<String> getFeatureDesc(String descriptor) {
		Set<String> featureDesc = new HashSet<String>();
		featureDesc.add(descriptor);
		return featureDesc;
	}
}
