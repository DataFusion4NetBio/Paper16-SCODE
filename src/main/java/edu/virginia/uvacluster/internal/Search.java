package edu.virginia.uvacluster.internal;

import java.util.List;

import org.cytoscape.model.subnetwork.*;
import org.cytoscape.work.TaskMonitor;

public interface Search {
	public List<Cluster> execute(InputTask userInput, TaskMonitor monitor) throws Exception;
	public void cancel();
}
