package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

//Vectors are defined by a list of property names
public class NodeTableVectorCorrelation extends FeatureSet {
	protected List<String> propertyNames = null;
	
	public NodeTableVectorCorrelation(List<Statistic> statistics, List<String> propertyNames) {
		super(null, statistics);
		this.propertyNames = propertyNames;
		description = "node{" + join(propertyNames) + "}";
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<List<Double>> vectors = new ArrayList<List<Double>>();
		List<Double> vec;
		List<CyNode> nodes = cluster.getNodes();
		CyTable table = cluster.getRootNetwork().getSharedNodeTable();

		for (CyNode node: nodes) {
			vec = new ArrayList<Double>();
			for (String prop: propertyNames) 
				vec.add(table.getRow(node.getSUID()).get(prop, Double.class));
			vectors.add(vec);
		}
		
		return computeDistances(vectors);
	}
	
	public List<String> getPropertyNames() {return propertyNames;}
	
	protected List<Double> computeDistances(List<List<Double>> vectors) {
		List<Double> distances = new ArrayList<Double>();
		List<List<Double>> others = new ArrayList<List<Double>>();
		others.addAll(vectors);
		for (List<Double> vec: vectors) {
			others.remove(vec);
			for (List<Double> other: others) {
				distances.add(computeCorrelation(vec, other));
			}
			others.add(vec);
		}
		return distances;
	}
	
	protected double computeCorrelation(List<Double> vecA, List<Double> vecB) {
		double meanA = mean(vecA), meanB = mean(vecB);
		double a, b, nominator = 0, aDiff = 0, bDiff = 0;
		for (int i = 0; i < vecA.size(); i++) {
			a = vecA.get(i);
			b = vecB.get(i);
			nominator += ((a - meanA)*(b - meanB));
			aDiff += Math.pow((a - meanA),2);
			bDiff += Math.pow((b - meanB),2);
		}
		return nominator/(Math.sqrt(aDiff)*Math.sqrt(bDiff));
	}
	
	protected double mean(List<Double> x) {
		double sum = 0;
		for(Double n: x) {sum += n;}
		return sum / x.size();
	}
	
	protected String join(List<String> items) {
		String result = items.get(0);
		for (String item :items.subList(1, items.size()-1)) {
			result += "," + item;
		}
		return result;
	}
}
