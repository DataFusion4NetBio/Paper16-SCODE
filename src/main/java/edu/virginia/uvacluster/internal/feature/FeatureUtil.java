package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.virginia.uvacluster.internal.statistic.*;

public class FeatureUtil {
	private static final Pattern statPattern = Pattern.compile("(.*):(.*)"); 
	private static final Pattern featurePattern = Pattern.compile("(.*)(\\(.*)"); 
	private static final Pattern binPattern = Pattern.compile("\\(([0-9]+/)?([0-9]+)\\)");

	private static final Pattern tableFeaturePattern = 
			Pattern.compile("(node|edge)\\{([^,]*)\\}",Pattern.CASE_INSENSITIVE);
	private static final Pattern weightFeaturePattern = 
			Pattern.compile("weight\\{(.*)\\}",Pattern.CASE_INSENSITIVE);
	private static final Pattern tableVectorFeaturePattern = 
			Pattern.compile("(node|edge)\\{(.*)\\}",Pattern.CASE_INSENSITIVE);
	private static final Pattern densityFeaturePattern = 
			Pattern.compile("density[^0-9.]*([0-9.]+)",Pattern.CASE_INSENSITIVE);
	
	//processes a set of feature keys in the form "Statistic: Feature (TotalBins)"
	public static List<FeatureSet> parse(Set<String> featureKeys) {
		List<FeatureSet> features = new ArrayList<FeatureSet>();
		Map<String,Set<Statistic>> statsMap = new HashMap<>(); 
		Matcher m;
		String statName = null, featureName = null;
		Integer numBins = null;
		
		for (String key: featureKeys) {
			m = statPattern.matcher(key);
			if (m.matches()) {
				statName = m.group(1).trim();
				key = m.group(2).trim();
			}
			m = featurePattern.matcher(key);
			m.matches();
			featureName = m.group(1).trim();
			key = m.group(2).replaceAll("\\p{Space}", "");;
			m = binPattern.matcher(key);
			m.matches();
			numBins = Integer.parseInt(m.group(2));
			
			if (statsMap.get(featureName) == null) 
				statsMap.put(featureName, new HashSet<Statistic>());
			statsMap.get(featureName).add(getStat(statName, numBins));
			statName = null;
		}

		for (String featureKey: statsMap.keySet()) {
			features.add(getFeature(featureKey,statsMap.get(featureKey)));
		}
		return features;
	}
	
	private static Statistic getStat(String name, int numBins) {
		Statistic stat = null;
		StatisticRange range = new StatisticRange(numBins);
		Matcher m = null;
		
		if (name != null) {
			name = name.toLowerCase();
			m = Pattern.compile("([0-9]+)").matcher(name);
		}
		if (name == null) {
			return new Unit(range);
		} else if (m.find()) {
			stat = new Ordinal(range,Integer.parseInt(m.group(1)));
		}
		switch(name) {
		case "count": 
			stat = new Count(range);
			break;
		case "max": 
			stat = new Max(range);
			break;
		case "mean": 
			stat = new Mean(range);
			break;
		case "median": 
			stat = new Median(range);
			break;
		case "variance": 
			stat = new Variance(range);
			break;
		}
		return stat;
	}
	
	private static FeatureSet getFeature(String name, Set<Statistic> statsSet) {
		List<Statistic> stats = new ArrayList<Statistic>(statsSet);
		FeatureSet feature = null;
		double cutoff;
		Matcher tableMatcher = tableFeaturePattern.matcher(name);
		Matcher weightMatcher = weightFeaturePattern.matcher(name);
		Matcher tableVectorMatcher = tableVectorFeaturePattern.matcher(name);
		Matcher densityMatcher = densityFeaturePattern.matcher(name);
		String obj;
		
		if (tableMatcher.matches()) {
			obj = tableMatcher.group(1).toLowerCase();
			name = tableMatcher.group(2);
			switch(obj) {
			case "node":
				feature = new NodeTableFeature(stats, name);
				break;
			case "edge":
				feature = new EdgeTableFeature(stats, name);
				break;
			}
		} else if (weightMatcher.matches()) {
			name = weightMatcher.group(1);
			feature = new EdgeWeight(stats,name);
		} else if (tableVectorMatcher.matches()) {
			obj = tableVectorMatcher.group(1).toLowerCase();
			name = tableVectorMatcher.group(2);
			switch(obj) {
			case "node":
				feature = new NodeTableVectorCorrelation(stats,Arrays.asList(name.split(",")));
				break;
			case "edge":
				feature = new EdgeTableVectorCorrelation(stats,Arrays.asList(name.split(",")));
				break;
			}
		} else if (densityMatcher.matches()) {
			cutoff = Double.parseDouble(densityMatcher.group(1));
			feature = new Density(stats, cutoff);
		}
		switch(name.toLowerCase()) {
		case "clustering coefficient":
			feature = new ClusterCoefficient(stats);
			break;
		case "degree":
			feature = new Degree(stats);
			break;
		case "degree correlation":
			feature = new DegreeCorrelation(stats);
			break;
		case "density":
			feature = new Density(stats);
			break;
		case "node":
			feature = new Node(stats);
			break;
		case "singular value":
			feature = new SingularValue(stats);
			break;
		case "topological coefficient":
			feature = new TopologicalCoefficient(stats);
			break;
		}
		return feature;
	}
}
