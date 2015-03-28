package edu.virginia.uvacluster.internal;

import java.util.List;

import edu.virginia.uvacluster.internal.feature.FeatureSet;

public interface Model {
	
	public double score(Cluster complex) throws Exception;
	public List<FeatureSet> getFeatures();

}
