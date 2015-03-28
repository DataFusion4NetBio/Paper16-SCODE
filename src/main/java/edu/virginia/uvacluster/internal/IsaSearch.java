package edu.virginia.uvacluster.internal;

import org.cytoscape.model.CyEdge.Type;
import org.cytoscape.model.subnetwork.*;
import org.cytoscape.model.CyNode;
import org.cytoscape.work.TaskMonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class IsaSearch implements Search {
	private CyRootNetwork rootNetwork;
	private Model model;
	private List<Cluster> candidates;
	private ExecutorService executor = null;
	
	public IsaSearch(CyRootNetwork network, Model model, List<CyNode> seedNodes) throws Exception {
		List<CyNode> neighbors = null;
		CySubNetwork net;
		int maxDegree = 0, degree;
		candidates = new ArrayList<Cluster>(seedNodes.size());
		rootNetwork = network;
		this.model = model;
		
		//init candidate list
		for (CyNode seed: seedNodes) {
			net = rootNetwork.addSubNetwork();
			net.addNode(seed);
			candidates.add(new Cluster(model.getFeatures(),net));
		}
		
		//add highest degree neighbor of each seed
		for (Cluster complex: candidates) {
			neighbors = complex.getNeighborList();
			
			maxDegree = 0;
			for (CyNode node: neighbors) {
				degree = rootNetwork.getNeighborList(node, Type.ANY).size();
				if (degree > maxDegree) 
					maxDegree = degree;
			}
			
			for (CyNode node: neighbors) {
				degree = rootNetwork.getNeighborList(node, Type.ANY).size();
				if (degree == maxDegree) {
					complex.add(node);
					break;
				}
			}
		}
		
		for (int i = 0; i < candidates.size(); i++) {
			if (candidates.get(i).getNodes().size() < 2) {
				candidates.remove(i);
				i--;
				System.out.println("Candidate disqualified");
			}
		}
		
		//give each cluster an initial score
		for (Cluster complex: candidates)
			model.score(complex);
	}
	
	public List<CySubNetwork> execute(InputTask input, TaskMonitor progress) throws Exception {
		List<CySubNetwork> results = new ArrayList<CySubNetwork>();
		int cores = Runtime.getRuntime().availableProcessors() * 2;
		List<List<Cluster>> jobs = ClusterUtil.divideWork(candidates, cores);
		executor = Executors.newFixedThreadPool(cores);
		Phaser phaser = new Phaser();

		for (List<Cluster> job: jobs) {
			Runnable searchThread = new SeedSearch(model, job, phaser, input, candidates);
			phaser.register();
			executor.execute(searchThread);
		}
		executor.shutdown();
        while (!executor.isTerminated()) {}
		
		//Filter out clusters that are too small or too low-scoring and copy good ones to results
		for (Cluster complex: candidates) {
			if ((complex.getNodes().size() >= input.minSize) && (model.score(complex) >= input.minScoreThreshold))
				results.add(complex.getSubNetwork());
			else
				complex.destroy();
		}
		
		return results;
	}
	
	public void cancel() {
		if (executor != null)
			executor.shutdownNow();
	}	
}
