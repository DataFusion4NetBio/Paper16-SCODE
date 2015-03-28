package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class Density extends FeatureSet {
	private Double cutoff = null;

	public Density(List<Statistic> statistics) {
		super("Density", statistics);
	}
	
	public Density(List<Statistic> statistics, double cutoff) {
		super("Density wrt cutoff " + Double.toString(cutoff), statistics);
		this.cutoff = cutoff;
	}
	
	public Double getCutoff() {return cutoff;}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> result = new ArrayList<Double>();
		List<Double> weights = cluster.getEdgeWeights();
        double edges = cluster.getEdges().size();
        double nodes = cluster.getNodes().size();
        double maxEdges = (nodes * (nodes - 1))/2;

        if ((cutoff != null) && (weights != null)) {
        	edges = 0;
        	for (Double weight: weights) {
        		if (weight >= cutoff)
        			edges++;
        	}
        }

        result.add(edges/maxEdges);
        return result;
	}
}
