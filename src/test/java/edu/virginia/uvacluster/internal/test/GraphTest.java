package edu.virginia.uvacluster.internal.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.junit.Test;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.Graph;
import edu.virginia.uvacluster.internal.feature.FeatureSet;
import edu.virginia.uvacluster.internal.feature.FeatureUtil;

public class GraphTest extends TestNetwork {
	CyRootNetwork net;
	CyRootNetwork netNeg;
	Graph model;
	List<Cluster> trainingPoints;
	List<Cluster> negTraining;
 	List<CySubNetwork> examples;
	List<CySubNetwork> negExamples;
	CySubNetwork validationExample;
	CyNode root, nodeCount, maxDegree, density, weight;
	CyNode a,b,c,d,e;
	CyEdge ab,ac,bc,cd,ce,de,ad, bd;
	
	@Test
	public void oneToManyModelShouldScoreCorrectly() {
		double result;
		Graph graph = new Graph("Test", 0.0001);
		List<FeatureSet> features = FeatureUtil.parse(graph.loadModelFrom(getOneToManyModel()));
		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
		graph.trainBins(trainingPoints);
		graph.trainOn(trainingPoints);
		result = graph.score(new Cluster(features, examples.get(0)));
		assertEquals("Model should score each example correctly",(9.0/49.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(1)));
		assertEquals("Model should score each example correctly",(9.0/49.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(2)));
		assertEquals("Model should score each example correctly",(4.0/49.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(3)));
		assertEquals("Model should score each example correctly",(4.0/49.0),result,0.001);
		result = graph.score(new Cluster(features, validationExample));
		assertEquals("Model should score each example correctly",(6.0/49.0),result,0.001);
	}
	
	
	@Test
	public void weightModel() {
		double scorePos, scoreNeg, resultScore;
		Graph posGraph = new Graph("Positive Bayes", 0.0001);
		Graph negGraph = new Graph("Negative Bayes", 0.9999);
		List<FeatureSet> features = FeatureUtil.parse(posGraph.loadModelFrom(getOneToManyModel()));
	}
	
//	@Test
//	public void positiveAndNegativeScoring() {
//		double scorePos, scoreNeg, resultScore;
//		Graph posGraph = new Graph("Positive Bayes", 0.0001);
//		Graph negGraph = new Graph("Negative Bayes", 0.9999);
//		List<FeatureSet> features = FeatureUtil.parse(posGraph.loadModelFrom(getOneToManyModel()));
//		
//		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
//		posGraph.trainBins(trainingPoints);
//		posGraph.trainOn(trainingPoints);
//		scorePos = posGraph.score(new Cluster(features, validationExample));
//		
//		features = FeatureUtil.parse(negGraph.loadModelFrom(getOneToManyModel()));
//		for(CySubNetwork e2: negExamples) {negTraining.add(new Cluster(features, e2));}
//		negGraph.trainBins(trainingPoints);
//		negGraph.trainOn(negTraining);
//		scoreNeg = negGraph.score(new Cluster(features, validationExample));
//		
//		resultScore = Math.log((0.0001*scorePos) / 
//				(0.9999*scoreNeg) );
//		assertEquals("Score on pos BN", ((2.0/7.0)*(3.0/7.0)), scorePos, 0.001);
//		assertEquals("Score on neg BN", ((4.0/7.0)*(3.0/7.0)), scoreNeg, 0.001);
//		assertEquals("Log of ratio of positive to negative BN scores", 
//				( Math.log( (0.0001 * (2.0/7.0) * (3.0/7.0)) / (0.9999 * (4.0/7.0) * (3.0/7.0)) ) ), 
//				resultScore, 0.001);
//	}

//	@Test
//	public void serialModelShouldScoreCorrectly() {
//		double result;
//		Graph graph = new Graph("Test", 0.0001);
//		List<FeatureSet> features = FeatureUtil.parse(graph.loadModelFrom(getSerialModel()));
//		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
//		graph.trainOn(trainingPoints);
//		result = graph.score(new Cluster(features, examples.get(0)));
//		assertEquals("Model should score each example correctly",(3.0/7.0)*(3.0/5.0),result,0.001);
//		result = graph.score(new Cluster(features, examples.get(1)));
//		assertEquals("Model should score each example correctly",(3.0/7.0)*(3.0/5.0),result,0.001);
//		result = graph.score(new Cluster(features, examples.get(2)));
//		assertEquals("Model should score each example correctly",(2.0/7.0)*(2.0/4.0),result,0.001);
//		result = graph.score(new Cluster(features, examples.get(3)));
//		assertEquals("Model should score each example correctly",(2.0/7.0)*(2.0/4.0),result,0.001);
//		result = graph.score(new Cluster(features, validationExample));
//		assertEquals("Model should score each example correctly",(2.0/7.0)*(1.0/4.0),result,0.001);
//	}
	
	@Test
	public void shouldSaveAndLoad() {
		double result, newResult;
		Graph graph = new Graph("Test", 0.0001);
		CyNetwork saveTo = nts.getNetwork();
		List<FeatureSet> features = FeatureUtil.parse(graph.loadModelFrom(getOneToManyModel()));
		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
		graph.trainBins(trainingPoints);
		graph.trainOn(trainingPoints);
		result = graph.score(new Cluster(features, validationExample));
		graph.saveTrainedModelTo(saveTo,features);
		graph = new Graph("Test", 0.0001);
		features = graph.loadTrainedModelFrom(saveTo);
		newResult = graph.score(new Cluster(features, validationExample));
		assertEquals("Score is the same before and after saving",result,newResult,0.001);
	}
	
	private CyNetwork getModelWireFrame() {
		CyNetwork net = nts.getNetwork();
		root = net.addNode();
		net.getRow(root).set("name", "Root");
		nodeCount = net.addNode();
		net.getRow(nodeCount).set("name", "Count : Node (3)");
		maxDegree = net.addNode();
		net.getRow(maxDegree).set("name", "Max : Degree (3)");
		density = net.addNode();
		net.getRow(density).set("name", "Density (3)");
		weight = net.addNode();
		net.getRow(weight).set("name", "Mean : weight{weight} (3)");
		return net;
	}
	
	private CyNetwork getOneToManyModel() {
		CyNetwork net = getModelWireFrame();
		net.addEdge(root, nodeCount, true);
		net.addEdge(root, maxDegree, true);
		return net;
	}
	
	private CyNetwork getWeightModel() {
		CyNetwork net = getModelWireFrame();
		net.addEdge(root, weight, true);
		return net;
	}
	
//	private CyNetwork getSerialModel() {
//		CyNetwork net = getModelWireFrame();
//		net.addEdge(root, nodeCount, true);
//		net.addEdge(root, maxDegree, true);
//		net.addEdge(nodeCount, maxDegree, true);
//		return net;
//	}
	
	public GraphTest() {
		net = rootMgr.getRootNetwork(nts.getNetwork());
		examples = new ArrayList<CySubNetwork>();
		trainingPoints = new ArrayList<Cluster>();
		for(int i = 0; i < 4; i++) {examples.add(net.addSubNetwork());}
		validationExample = net.addSubNetwork();
		a = net.addNode(); b = net.addNode(); c = net.addNode(); 
		d = net.addNode(); e = net.addNode();
		ab = net.addEdge(a, b, false); ac = net.addEdge(a, c, false);
		bc = net.addEdge(b, c, false); cd = net.addEdge(c, d, false);
		ce = net.addEdge(c, e, false); de = net.addEdge(d, e, false);
		ad = net.addEdge(a, d, false); bd = net.addEdge(b, d, false);
		
		
		negExamples = new ArrayList<CySubNetwork>();
		negTraining = new ArrayList<Cluster>();
		for (int i = 0; i < 4; i ++) {negExamples.add(net.addSubNetwork());}

		
		
		/* POSITIVE EXAMPLES */
		//First example
		examples.get(0).addNode(a);
		examples.get(0).addNode(b);
		examples.get(0).addNode(c);
		examples.get(0).addEdge(ab);
		examples.get(0).addEdge(ac);
		examples.get(0).addEdge(bc);
		//Second example
		examples.get(1).addNode(a);
		examples.get(1).addNode(b);
		examples.get(1).addNode(c);
		examples.get(1).addEdge(ab);
		examples.get(1).addEdge(ac);
		//Third example
		examples.get(2).addNode(a);
		examples.get(2).addNode(b);
		examples.get(2).addNode(c);
		examples.get(2).addNode(d);
		examples.get(2).addEdge(ac);
		examples.get(2).addEdge(bc);
		examples.get(2).addEdge(cd);
		//Fourth example
		for(CyNode n: net.getNodeList()) {examples.get(3).addNode(n);}
		for(CyEdge x: net.getEdgeList()) {examples.get(3).addEdge(x);}
		//Validation example
		validationExample.addNode(a);
		validationExample.addNode(b);
		validationExample.addNode(c);
		validationExample.addNode(d);
		validationExample.addNode(e);
		validationExample.addEdge(ab);
		validationExample.addEdge(bc);
		validationExample.addEdge(cd);
		validationExample.addEdge(de);
		
		
		/* NEGATIVE EXAMPLES */
		// First example: a -- b -- c -- d
		negExamples.get(0).addNode(a);
		negExamples.get(0).addNode(b);
		negExamples.get(0).addNode(c);
		negExamples.get(0).addNode(d);
		negExamples.get(0).addEdge(ab);
		negExamples.get(0).addEdge(bc);
		negExamples.get(0).addEdge(cd);
		
		// Second example:     b
		//					   |
		//				  c -- a -- d
		negExamples.get(1).addNode(a);
		negExamples.get(1).addNode(b);
		negExamples.get(1).addNode(c);
		negExamples.get(1).addNode(d);
		negExamples.get(1).addEdge(ab);
		negExamples.get(1).addEdge(ac);
		negExamples.get(1).addEdge(ad);
		
		// Third example: a -- b
		//				  |	   |
		//				  c -- d
		negExamples.get(2).addNode(a);
		negExamples.get(2).addNode(b);
		negExamples.get(2).addNode(c);
		negExamples.get(2).addNode(d);
		negExamples.get(2).addEdge(ab);
		negExamples.get(2).addEdge(ac);
		negExamples.get(2).addEdge(cd);
		negExamples.get(2).addEdge(bd);

		// Fourth example:  a -- b
		negExamples.get(3).addNode(a);
		negExamples.get(3).addNode(b);
		negExamples.get(3).addEdge(ab);
	}
}