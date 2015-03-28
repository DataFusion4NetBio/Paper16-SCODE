package edu.virginia.uvacluster.internal;

import java.util.List;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.time.StopWatch;
import org.cytoscape.model.CyNode;

public class SeedSearch implements Runnable {
	private List<Cluster> clusters;
	private Phaser phaser;
	private double temp = 0;
	private Model model;
	List<Cluster> candidates;
	InputTask input;
	
	//TODO wtf are complexes vs candidates?
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
				for (Cluster x: candidates) {
					if ((! cluster.searchComplete) &&
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
		//neighbors = neighbors.subList(0, 50); //TODO Refine this....
		CyNode topNode = null;
		double topScore = -Double.MAX_VALUE, originalScore = model.score(complex);
		double updateProbability = 0;
		StopWatch timer = new StopWatch();
		timer.start();
		
		
		System.out.println("Neighbors: " + neighbors.size());
		if (neighbors.size() > 0) {	
			for (CyNode node: neighbors) {
				complex.add(node);
				
				//System.out.println(complex.getScore() - topScore);
				if (model.score(complex) > topScore) {
					topScore = model.score(complex);
					topNode = node;
				}
					
				complex.remove(node);
			}
			System.out.println("Neighbors scored at time: " + timer.getTime());
			
			updateProbability = Math.exp((originalScore - topScore)/temp);
			
			if (topNode != null) {
				if ((topScore > originalScore) || (ThreadLocalRandom.current().nextDouble() < updateProbability)) //then accept the new complex
					complex.add(topNode);
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
