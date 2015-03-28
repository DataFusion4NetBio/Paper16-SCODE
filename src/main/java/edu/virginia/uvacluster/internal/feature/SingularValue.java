package edu.virginia.uvacluster.internal.feature;

import java.util.ArrayList;
import java.util.List;

import org.cytoscape.model.CyNode;
import org.ejml.simple.SimpleMatrix;
import org.ejml.simple.SimpleSVD;

import edu.virginia.uvacluster.internal.Cluster;
import edu.virginia.uvacluster.internal.statistic.Statistic;

public class SingularValue extends FeatureSet {

	public SingularValue(List<Statistic> statistics) {
		super("Singular Value", statistics);
	}

	@Override
	public List<Double> computeInputs(Cluster cluster) {
		List<Double> singularValues = new ArrayList<Double>(); 
	    double[][] adjacencyValues = getAdjacencyValues(cluster);
	    SimpleMatrix adjacencyMatrix = new SimpleMatrix(adjacencyValues);
	    int numNodes = cluster.getNodes().size();
	  	@SuppressWarnings("rawtypes")
		SimpleSVD svd = adjacencyMatrix.svd(); //this svd function guarantees ordering
	    
	    for (int i = 0; i < 3; i++) {
	    	if (i < numNodes)
	    		singularValues.add(svd.getSingleValue(i));
	    	else
	    		singularValues.add(0.0);
	    }
	    	
	    return singularValues;
	}
	
	/**
	 * Gets degree coefficients of nodes in cluster in 2d array for matrix svd.
	 *
	 * @return an 2d array of doubles with edge weights between 'nodes'.
	 */
	private double[][] getAdjacencyValues(Cluster cluster) {
	  List<CyNode> nodeList = cluster.getNodes();
	  int numElements = nodeList.size();
	  double[][] adjacencyMatrix = new double[numElements][numElements];
	  CyNode nodeX, nodeY;
	  
	  //System.out.println(table);
	  
	  for (int x = 0; x < numElements; x++)
	  {
	  	nodeX = nodeList.get(x);
	  	
	  	for (int y = 0; y < numElements; y++)
	   	{
	   		nodeY = nodeList.get(y);
	   		
	   		if (cluster.containsEdge(nodeX, nodeY))
	   		{
	   			adjacencyMatrix[x][y] = 1;
	   		} else {
	   			adjacencyMatrix[x][y] = 0;
	   		}
	   	}
	  }
	    
	  return adjacencyMatrix;
	}
}
