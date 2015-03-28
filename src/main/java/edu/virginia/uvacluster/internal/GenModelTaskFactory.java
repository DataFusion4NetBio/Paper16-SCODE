package edu.virginia.uvacluster.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

public class GenModelTaskFactory extends AbstractNetworkTaskFactory{

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		TaskIterator tasks = new TaskIterator();
		tasks.append(new GenModelTask());
		return tasks;
	}
}
