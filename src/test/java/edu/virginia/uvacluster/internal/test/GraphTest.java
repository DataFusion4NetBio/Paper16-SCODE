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
	Graph model;
	List<Cluster> trainingPoints;
	List<CySubNetwork> examples;
	CySubNetwork validationExample;
	CyNode root, nodeCount, maxDegree, density;
	CyNode a,b,c,d,e;
	CyEdge ab,ac,bc,cd,ce,de;
	
	@Test
	public void oneToManyModelShouldScoreCorrectly() {
		double result;
		Graph graph = new Graph("Test");
		List<FeatureSet> features = FeatureUtil.parse(graph.loadModelFrom(getOneToManyModel()));
		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
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
	public void serialModelShouldScoreCorrectly() {
		double result;
		Graph graph = new Graph("Test");
		List<FeatureSet> features = FeatureUtil.parse(graph.loadModelFrom(getSerialModel()));
		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
		graph.trainOn(trainingPoints);
		result = graph.score(new Cluster(features, examples.get(0)));
		assertEquals("Model should score each example correctly",(3.0/7.0)*(3.0/5.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(1)));
		assertEquals("Model should score each example correctly",(3.0/7.0)*(3.0/5.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(2)));
		assertEquals("Model should score each example correctly",(2.0/7.0)*(2.0/4.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(3)));
		assertEquals("Model should score each example correctly",(2.0/7.0)*(2.0/4.0),result,0.001);
		result = graph.score(new Cluster(features, validationExample));
		assertEquals("Model should score each example correctly",(2.0/7.0)*(1.0/4.0),result,0.001);
	}
	
	@Test
	public void manyToOneModelShouldScoreCorrectly() {
		double result;
		Graph graph = new Graph("Test");
		List<FeatureSet> features = FeatureUtil.parse(graph.loadModelFrom(getManyToOneModel()));
		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
		graph.trainOn(trainingPoints);
		result = graph.score(new Cluster(features, examples.get(0)));
		assertEquals("Model should score each example correctly",(3.0/7.0)*(3.0/7.0)*(3.0/5.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(1)));
		assertEquals("Model should score each example correctly",(3.0/7.0)*(3.0/7.0)*(3.0/5.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(2)));
		assertEquals("Model should score each example correctly",(2.0/7.0)*(2.0/7.0)*(2.0/4.0),result,0.001);
		result = graph.score(new Cluster(features, examples.get(3)));
		assertEquals("Model should score each example correctly",(2.0/7.0)*(2.0/7.0)*(2.0/4.0),result,0.001);
		result = graph.score(new Cluster(features, validationExample));
		assertEquals("Model should score each example correctly",(2.0/7.0)*(3.0/7.0)*(1.0/3.0),result,0.001);
	}
	
	@Test
	public void shouldSaveAndLoad() {
		double result, newResult;
		Graph graph = new Graph("Test");
		CyNetwork saveTo = nts.getNetwork();
		List<FeatureSet> features = FeatureUtil.parse(graph.loadModelFrom(getOneToManyModel()));
		for(CySubNetwork e: examples) {trainingPoints.add(new Cluster(features, e));}
		graph.trainOn(trainingPoints);
		result = graph.score(new Cluster(features, examples.get(0)));
		graph.saveTrainedModelTo(saveTo);
		graph = new Graph("Test");
		graph.loadTrainedModelFrom(saveTo);
		newResult = graph.score(new Cluster(features, examples.get(0)));
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
		return net;
	}
	
	private CyNetwork getOneToManyModel() {
		CyNetwork net = getModelWireFrame();
		net.addEdge(root, nodeCount, true);
		net.addEdge(root, maxDegree, true);
		return net;
	}
	
	private CyNetwork getManyToOneModel() {
		CyNetwork net = getModelWireFrame();
		net.addEdge(root, nodeCount, true);
		net.addEdge(root, maxDegree, true);
		net.addEdge(root, density, true);
		net.addEdge(nodeCount, density, true);
		net.addEdge(maxDegree, density, true);
		return net;
	}
	
	private CyNetwork getSerialModel() {
		CyNetwork net = getModelWireFrame();
		net.addEdge(root, nodeCount, true);
		net.addEdge(root, maxDegree, true);
		net.addEdge(nodeCount, maxDegree, true);
		return net;
	}
	
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
	}
}
