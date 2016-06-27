package edu.virginia.uvacluster.internal;

import javax.swing.JDialog;
import javax.swing.JLabel;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.task.AbstractNetworkTaskFactory;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

//Initiates user input, which appends other tasks to iterator (because of control)...
public class SupervisedComplexTaskFactory implements TaskFactory{

	private final InputTask inputTask;
	private SearchTask search;
	CyApplicationManager appManager;
	Logger logger = LoggerFactory.getLogger(getClass());
	
	public SupervisedComplexTaskFactory(InputTask inputTask, CyApplicationManager appManager) {
		
		this.inputTask = inputTask;
		this.appManager = appManager;
	}
	
	@Override
	public TaskIterator createTaskIterator() {
		
	    
		TaskIterator programTasks = new TaskIterator();
		String networkName = inputTask.graphName;
		CyNetwork network = null;
		
		for (CyNetwork n: CyActivator.networkManager.getNetworkSet()) {
			if ((n.getRow(n).get(CyNetwork.NAME, String.class).equals(networkName))) {
				network = n;
			}
		}
		
		InputTask userInput = inputTask;
		
		// If training a bayesian network then add training task to the iterator, otherwise
		// Perform only simple search
		TrainingTask train = new TrainingTask(network, userInput);
		if (userInput.supervisedLearning) {
			programTasks.append(train);
		}
		search = new SearchTask(network, userInput, train);
		programTasks.append(search);
		
		return programTasks;
	}
	
	public SearchTask getSearchTask() {
		return search;
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}

}