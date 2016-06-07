package edu.virginia.uvacluster.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

import org.cytoscape.model.CyNode;

public class SeedSearch implements Runnable {
	private List<Cluster> clusters;
	private Phaser phaser;
	private double temp = 0;
	private Model model;
	List<Cluster> candidates;
	InputTask input;
	
	public SeedSearch(Model model, List<Cluster> complexes, Phaser p, InputTask input, List<Cluster> candidates) {
		clusters = complexes;
		phaser = p;
		temp = input.initTemp;
		this.input = input;
		this.candidates = candidates;
		this.model = model;
	}
	
	@Override
	public void run() {
		//Search
		for (int i = 0; i < input.searchLimit; i++){
			phaser.arriveAndAwaitAdvance();
			//check overlap and deactivate clusters with too high an overlap ratio
			for (Cluster cluster: clusters) {

				System.out.println("In SeedSearch.java: searching on cluster " + cluster.getSUID());
				for (Cluster x: candidates) {
					// Changed: check that neither one is deactivated -- leave one on
					if ((! (cluster.searchComplete || x.searchComplete)) &&
						(cluster.getSUID() != x.getSUID()) && 
						(getOverlapRatio(cluster,x) > input.overlapLimit)) {
						cluster.searchComplete = true;
					}
				}
			}
			phaser.arriveAndAwaitAdvance();
			
			//update each cluster for this step
			for (Cluster cluster: clusters) {
				if (! cluster.searchComplete) {
					try {updateCluster(cluster);}
					catch (Exception e) {e.printStackTrace();}
				}
			}		
			
			//update the temp
			temp *= input.tempScalingFactor;
			System.out.println("Completed search step: " + (i + 1));
		}
		phaser.arriveAndDeregister();
	}
	
	//Update individual cluster for one iteration of ISA, uses but does not modify temp
	private void updateCluster(Cluster complex) throws Exception {
		List<CyNode> neighbors = complex.getNeighborList();
		CyNode candidateNode = null, node = null;
		double newScore, topScore = -Double.MAX_VALUE;
		double originalScore = model.score(complex);
		double updateProbability = 0;
		
		
		System.out.println("Neighbors: " + neighbors.size());
		if (neighbors.size() > 0) {	
			if (input.getSelectedSearch().equals("Greedy ISA")) {
				for (CyNode n: neighbors) {
					complex.add(n);
					
					if (model.score(complex) > topScore) {
						topScore = model.score(complex);
						candidateNode = n;
					}
						
					complex.remove(n);
				}
			} else if (input.getSelectedSearch().equals("ISA")) {
			    candidateNode = neighbors.get((int) Math.round(ThreadLocalRandom.current().nextDouble() * neighbors.size()));
			} else if (input.getSelectedSearch().equals("M ISA")) {
				neighbors = ClusterUtil.sortByDegree(complex.getRootNetwork(), neighbors);
				for (int i = 0; i < input.checkNumNeighbors; i++) {
					node = neighbors.get(i);
					complex.add(node);
					
					if (model.score(complex) > topScore) {
						topScore = model.score(complex);
						candidateNode = node;
					}
						
					complex.remove(node);
				}
			}
			
			complex.add(candidateNode);
            newScore = model.score(complex);
			updateProbability = Math.exp((newScore - originalScore)/temp); //TODO note this in writeup
			System.out.print("Update probability: " + updateProbability);
			if ((newScore > originalScore) || (input.supervisedLearning && (ThreadLocalRandom.current().nextDouble() < updateProbability))){ 
				//then accept the new complex
			} else {
				complex.remove(candidateNode);
			}
		}
	}
	
	/** Returns the overlap ratio of cluster x to cluster y
	 * 
	 * @param x a cluster
	 * @param y another cluster
	 * @return the overlap ratio
	 */
	private double getOverlapRatio(Cluster x, Cluster y) {
		double ratio = 0;
		double nodesInCommon = ClusterUtil.sizeOfIntersection(x.getNodes(), y.getNodes());
		
		ratio = nodesInCommon / x.getNodes().size();
		
		return ratio;
	}
}
