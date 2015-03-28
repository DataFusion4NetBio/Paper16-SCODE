package edu.virginia.uvacluster.internal;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskIterator;

//Initiates user input, which appends other tasks to iterator (because of control)...
public class SupervisedComplexTaskFactory extends AbstractNetworkTaskFactory{

	@Override
	public TaskIterator createTaskIterator(CyNetwork network) {
		TaskIterator programTasks = new TaskIterator();
		
		InputTask userInput = new InputTask();
		TrainingTask train = new TrainingTask(network, userInput);
		SearchTask search = new SearchTask(network, userInput, train);
		
		programTasks.append(userInput);
		programTasks.append(train);
		programTasks.append(search);
		
		return programTasks;
	}
}