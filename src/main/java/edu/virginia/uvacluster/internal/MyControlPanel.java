package edu.virginia.uvacluster.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.Tunable;
import org.cytoscape.work.util.ListSingleSelection;

import javax.swing.JLabel;

public class MyControlPanel extends JPanel implements CytoPanelComponent {

	private static final long serialVersionUID = 8292806967891823933L;
	
	private JPanel searchPanel;
	private JPanel trainPanel;
	private JComboBox chooser;
	private JTextField checkNumNeighbors;
	private JCheckBox useSelectedForSeeds;
	private JTextField numSeeds;
	private JTextField searchLimit;
	private JTextField initTemp;
	private JTextField tempScalingFactor;
	private JTextField overlapLimit;
	private JTextField minScoreThreshold;
	private JTextField minSize;
	
	private JCheckBox trainNewModel;
	private JComboBox existingModel;
	private JCheckBox customModel;
	private JComboBox bayesModel;
	private JComboBox weightName;
	private JTextField clusterPrior;
	private JTextField negativeExamples;
	private JCheckBox ignoreMissing;
	private File trainingFile;
	private File resultFile;
	private JLabel trainingFileLabel;
	private JLabel resultFileLabel;
	
	public MyControlPanel() {
		//JPanel scopePnl = new JPanel();
		final GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(false);
		layout.setAutoCreateGaps(true);
		
		//this.add(createSearchPanel());
		
		JButton analyzeButton = new JButton("Analyze Network");
		analyzeButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    analyzeButtonPressed();
			  } 
			} );
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER, true)
				.addComponent(createSearchPanel())
				.addComponent(createTrainPanel())
				.addComponent(analyzeButton)
		);
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(createSearchPanel())
				.addComponent(createTrainPanel())
				.addComponent(analyzeButton)
		);
		

	}
	
	public JPanel createSearchPanel() {
		if (searchPanel == null) {
			searchPanel = new JPanel();
			final GroupLayout layout = new GroupLayout(searchPanel);
			searchPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(true);
			layout.setAutoCreateGaps(true);
			
			TitledBorder search = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Search");
			searchPanel.setBorder(search);
			String[] variants = { "Greedy ISA", "ISA", "M ISA" };
			chooser = new JComboBox(variants);
			JLabel chooserLabel = new JLabel("Choose a variant: ");
			
			checkNumNeighbors = new JTextField("20");
			JLabel checkNumNeighborsLabel = new JLabel("Neighbors to consider: ");
			
			useSelectedForSeeds = new JCheckBox();
			JLabel useSelectedForSeedsLabel = new JLabel("Use Selected Nodes as Seeds");
			
			numSeeds = new JTextField("10");
			JLabel numSeedsLabel = new JLabel("Number of Seeds");
			
			searchLimit = new JTextField("20");
			JLabel searchLimitLabel = new JLabel("Search Limit");
			
			initTemp = new JTextField("1.8");
			JLabel initTempLabel = new JLabel("Initial Temperature");
			
			tempScalingFactor = new JTextField("0.88");
			JLabel tempScalingFactorLabel = new JLabel("Temperature Scaling Factor");
			
			overlapLimit = new JTextField("0.75");
			JLabel overlapLimitLabel = new JLabel("Overlap Limit");
			
			minScoreThreshold = new JTextField("-4E2");
			JLabel minScoreThresholdLabel = new JLabel("Minimum Complex Score");
			
			minSize = new JTextField("3");
			JLabel minSizeLabel = new JLabel("Minimum Complex Size");
			
			
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(chooserLabel)
								.addComponent(checkNumNeighborsLabel)
								.addComponent(useSelectedForSeedsLabel)
								.addComponent(numSeedsLabel)
								.addComponent(searchLimitLabel)
								.addComponent(initTempLabel)
								.addComponent(tempScalingFactorLabel)
								.addComponent(overlapLimitLabel)
								.addComponent(minScoreThresholdLabel)
								.addComponent(minSizeLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(chooser)
								.addComponent(checkNumNeighbors)
								.addComponent(useSelectedForSeeds)
								.addComponent(numSeeds)
								.addComponent(searchLimit)
								.addComponent(initTemp)
								.addComponent(tempScalingFactor)
								.addComponent(overlapLimit)
								.addComponent(minScoreThreshold)
								.addComponent(minSize))
			);
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(chooserLabel)
							.addComponent(chooser))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(checkNumNeighborsLabel)
							.addComponent(checkNumNeighbors))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(useSelectedForSeedsLabel)
							.addComponent(useSelectedForSeeds))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(numSeedsLabel)
							.addComponent(numSeeds))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(searchLimitLabel)
							.addComponent(searchLimit))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(initTempLabel)
							.addComponent(initTemp))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(tempScalingFactorLabel)
							.addComponent(tempScalingFactor))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(overlapLimitLabel)
							.addComponent(overlapLimit))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(minScoreThresholdLabel)
							.addComponent(minScoreThreshold))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(minSizeLabel)
							.addComponent(minSize))
			);
		}
		return searchPanel;
	}
	
	public JPanel createTrainPanel() {
		if (trainPanel == null) {
			trainPanel = new JPanel();
			final GroupLayout layout = new GroupLayout(trainPanel);
			trainPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(true);
			layout.setAutoCreateGaps(true);
			
			TitledBorder train = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Train");
			trainPanel.setBorder(train);
			
			trainNewModel = new JCheckBox();
			JLabel trainNewModelLabel = new JLabel("Train New Model");
			
			existingModel = new JComboBox(getNetworkNames().toArray());
			JLabel existingModelLabel = new JLabel("Use Trained Model");
			
			customModel = new JCheckBox();
			JLabel customModelLabel = new JLabel("Use Custom Bayesian Network");
			
			bayesModel = new JComboBox(getNetworkNames().toArray());
			JLabel bayesModelLabel = new JLabel("Custom Bayesian Network");
			
			weightName = new JComboBox(getEdgeColumnNames().toArray());
			JLabel weightNameLabel = new JLabel("Edge Weight Column");
			
			clusterPrior = new JTextField("1E-4");
			JLabel clusterPriorLabel = new JLabel("Cluster Probability Prior");
			
			negativeExamples = new JTextField("2000");
			JLabel negativeExamplesLabel = new JLabel("Generate # of Negative Examples");
			
			JButton trainingFileButton = new JButton("Select Training File");
			trainingFileLabel = new JLabel("Load Positive Training Data");
	        trainingFileButton.addActionListener(new ActionListener() {	 
	            public void actionPerformed(ActionEvent e)
	            {
	                JFileChooser trainingChooser = new JFileChooser();
	                int result = trainingChooser.showOpenDialog(MyControlPanel.this);
	                if (result == JFileChooser.APPROVE_OPTION) {
	                    trainingFile = trainingChooser.getSelectedFile();
	                    trainingFileLabel.setText(trainingFile.getName());
	                }
	            }
	        }); 
			
			ignoreMissing = new JCheckBox();
			JLabel ignoreMissingLabel = new JLabel("Ignore Missing Nodes");
			
			resultFileLabel = new JLabel("Save Results to File (Optional)");
			JButton resultFileButton = new JButton("Select Results File");
	        resultFileButton.addActionListener(new ActionListener() {
	        	 
	            public void actionPerformed(ActionEvent e)
	            {
	                JFileChooser resultChooser = new JFileChooser();
	                int result = resultChooser.showOpenDialog(MyControlPanel.this);
	                if (result == JFileChooser.APPROVE_OPTION) {
	                    resultFile = resultChooser.getSelectedFile();
	                    resultFileLabel.setText(resultFile.getName());
	                }
	            }
	        }); 
			
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(trainNewModelLabel)
								.addComponent(existingModelLabel)
								.addComponent(customModelLabel)
								.addComponent(bayesModelLabel)
								.addComponent(weightNameLabel)
								.addComponent(clusterPriorLabel)
								.addComponent(negativeExamplesLabel)
								.addComponent(trainingFileLabel)
								.addComponent(ignoreMissingLabel)
								.addComponent(resultFileLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(trainNewModel)
								.addComponent(existingModel)
								.addComponent(customModel)
								.addComponent(bayesModel)
								.addComponent(weightName)
								.addComponent(clusterPrior)
								.addComponent(negativeExamples)
								.addComponent(trainingFileButton)
								.addComponent(ignoreMissing)
								.addComponent(resultFileButton))
			);
			layout.setVerticalGroup(
					layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(trainNewModelLabel)
							.addComponent(trainNewModel))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(existingModelLabel)
							.addComponent(existingModel))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(customModelLabel)
							.addComponent(customModel))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(bayesModelLabel)
							.addComponent(bayesModel))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(weightNameLabel)
							.addComponent(weightName))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(clusterPriorLabel)
							.addComponent(clusterPrior))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(negativeExamplesLabel)
							.addComponent(negativeExamples))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(trainingFileLabel)
							.addComponent(trainingFileButton))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(ignoreMissingLabel)
							.addComponent(ignoreMissing))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(resultFileLabel)
							.addComponent(resultFileButton))
			);
		}
		return trainPanel;
	}

	public Component getComponent() {
		return this;
	}

	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}

	public String getTitle() {
		return "";
	}

	public Icon getIcon() {
		ImageIcon icon = new ImageIcon(MyControlPanel.class.getResource("/images/scodelogo.png"));
		return icon;
	}
	
	private List<String> getNetworkNames() {
		List<String> names = new ArrayList<String>();
		for (CyNetwork network: CyActivator.networkManager.getNetworkSet()) {
			names.add(network.getRow(network).get(CyNetwork.NAME, String.class));
		}
		return names;
	}
	
	private List<String> getEdgeColumnNames() {
		List<String> names = new ArrayList<String>();
		names.add("- Select Column -");
		for (CyNetwork network: CyActivator.networkManager.getNetworkSet()) {
			for (CyColumn col:  network.getDefaultEdgeTable().getColumns()) {
				if (!names.contains(col.getName()) &&  (!col.getName().equals("SUID")) &&
						((col.getType() == Double.class) || (col.getType() == Integer.class) || (col.getType() == Long.class))) 
					names.add(col.getName());
			}
		}
		return names;
	}
	
	private void analyzeButtonPressed() {
			
	}
	
}