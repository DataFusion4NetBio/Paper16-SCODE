package edu.virginia.uvacluster.internal.test;

import java.util.Arrays;
import java.util.List;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.NetworkTestSupport;
import org.cytoscape.model.subnetwork.CyRootNetwork;
import org.cytoscape.model.subnetwork.CyRootNetworkManager;
import org.cytoscape.model.subnetwork.CySubNetwork;

public class TestNetwork {
	protected final NetworkTestSupport nts = new NetworkTestSupport();
	protected final CyRootNetworkManager rootMgr = nts.getRootNetworkFactory();
	protected final CyNetwork network = nts.getNetwork();
	protected final CyRootNetwork rootNetwork = rootMgr.getRootNetwork(network);
	protected final CyNode a,b,c,d,e, f;
	protected final CyEdge ab,bd,de,be,bc;
	protected List<CyNode> nodes;
	protected List<CyEdge> edges;
	
	public TestNetwork() {
		network.getDefaultEdgeTable().createColumn("weight", Double.class, false);
		network.getDefaultEdgeTable().createColumn("testA", Double.class, false);
		network.getDefaultEdgeTable().createColumn("testB", Double.class, false);

		a = network.addNode();
		b = network.addNode();
		c = network.addNode();
		d = network.addNode();
		e = network.addNode();
		f = network.addNode();
		
		ab = network.addEdge(a, b, false);
		bd = network.addEdge(b, d, false);
		de = network.addEdge(d, e, false);
		be = network.addEdge(b, e, false);
		bc = network.addEdge(b, c, false);
		
		network.getRow(ab).set("weight", 0.5);
		network.getRow(bd).set("weight", 0.75);
		network.getRow(de).set("weight", 0.25);
		network.getRow(be).set("weight", 1.25);
		network.getRow(bc).set("weight", 4.0);
		
		network.getRow(ab).set("testA", 0.23);
		network.getRow(bd).set("testA", 0.55);
		network.getRow(ab).set("testB", 0.35);
		network.getRow(bd).set("testB", 0.40);
		
		nodes = network.getNodeList();
		edges = network.getEdgeList();
	}
	
	public CySubNetwork getCliqueSubNetwork() {
		CySubNetwork subNetwork = rootNetwork.addSubNetwork();
		subNetwork.addNode(b); subNetwork.addNode(e); subNetwork.addNode(d);
		subNetwork.addEdge(be); subNetwork.addEdge(de); subNetwork.addEdge(bd);
		return subNetwork;
	}
	
	public CySubNetwork getStarSubNetwork() {
		CySubNetwork net = rootNetwork.addSubNetwork();
		net.addNode(a); net.addNode(b); net.addNode(c); net.addNode(d);
		net.addEdge(ab); net.addEdge(bc); net.addEdge(be);
		return net;
	}
	
	public CySubNetwork getCompleteSubNetwork() {
		CySubNetwork subNetwork = rootNetwork.addSubNetwork();
		for (CyNode node: nodes) {subNetwork.addNode(node);}
		for (CyEdge edge: edges) {subNetwork.addEdge(edge);}
		subNetwork.removeNodes(Arrays.asList(f));
		return subNetwork;
	}
	
	public CySubNetwork getCorrelationSubNetwork() {
		CySubNetwork net = rootNetwork.addSubNetwork();
		net.addNode(a); net.addNode(b); net.addNode(d);
		net.addEdge(ab); net.addEdge(bd);
		return net;
	}
}
