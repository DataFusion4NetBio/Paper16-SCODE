package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public abstract class FeatureSet {
	protected List<Statistic> statistics;
	protected String description;
	
	public FeatureSet(String description, List<Statistic> statistics) {
		this.statistics = statistics;
		this.description = description;
	}
	
	public List<Statistic> getStatistics() {return statistics;}
	
	public abstract List<Double> computeInputs(Cluster cluster);
	
	public List<Statistic> train(Cluster cluster) {
		for (Statistic statistic: statistics)
			statistic.train(computeInputs(cluster));
		return statistics;
	}
	
	public List<Double> getValues(Cluster cluster) {
		List<Double> result = new ArrayList<Double>();
		for(Statistic statistic: statistics)
			result.add(statistic.transform(computeInputs(cluster)));
		return result;
	}

	public List<Integer> getBinnedValues(Cluster cluster) {
		List<Integer> result = new ArrayList<Integer>();
		for(Statistic statistic: statistics)
			result.add(statistic.binTransform(computeInputs(cluster)));
		return result;
	}

	public List<String> getDescriptions() {
		List<String> result = new ArrayList<String>();
		for(Statistic statistic: statistics)
			result.add(statistic.getDescription(description));
		return result;
	}
	
	public Map<String, Bin> getFeatureMap(Cluster cluster) {
		HashMap<String, Bin> result = new HashMap<String, Bin>();
		Iterator<Integer> binIter = getBinnedValues(cluster).iterator();
		Iterator<Statistic> statIter = statistics.iterator();
		for (String name: getDescriptions()) {
			result.put(name, new Bin(binIter.next(),statIter.next().getRange().getNumBins()));
		}
		return result;
	}
}
