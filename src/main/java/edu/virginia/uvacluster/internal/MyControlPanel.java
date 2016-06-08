package edu.virginia.uvacluster.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.subnetwork.CySubNetwork;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.swing.DialogTaskManager;
import org.cytoscape.work.util.ListSingleSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class MyControlPanel extends JPanel implements CytoPanelComponent {

	private static final long serialVersionUID = 8292806967891823933L;
	
	private JPanel searchPanel;
	private JPanel trainPanel;
	private JPanel evaluatePanel;
	private JPanel advancedTrainPanel;
	private JPanel advancedSearchPanel;
	private JPanel outerSearchPanel;
	private JPanel outerTrainPanel;
	
	private JButton analyzeButton;
	private JComboBox chooser;
	private JComboBox proteinGraph;
	private JComboBox inputGraphChooser;
	private JTextField checkNumNeighbors;
	private JCheckBox useSelectedForSeeds;
	private File useSelectedForSeedsFile;
	private JButton useSelectedForSeedsButton;
	private JPanel useSelectedForSeedsPanel;
	private JTextField numSeeds;
	private JTextField searchLimit;
	private JTextField initTemp;
	private JTextField tempScalingFactor;
	private JTextField overlapLimit;
	private JTextField minScoreThreshold;
	private JTextField minSize;
	private JTextField numResults;
	
	private JLabel chooserLabel;
	private JLabel proteinGraphLabel;
	private JLabel checkNumNeighborsLabel;
	private JLabel useSelectedForSeedsLabel;
	private JLabel numSeedsLabel;
	private JLabel searchLimitLabel;
	private JLabel initTempLabel;
	private JLabel tempScalingFactorLabel;
	private JLabel overlapLimitLabel;
	private JLabel minScoreThresholdLabel;
	private JLabel minSizeLabel;
	private JLabel numResultsLabel;
	
	private JCheckBox trainNewModel;
	private JComboBox existingModel;
	private JComboBox bayesModel;
	private JPanel customModelPanel;
	private JComboBox weightName;
	private JTextField clusterPrior;
	private JTextField negativeExamples;
	private JCheckBox ignoreMissing;
	private File trainingFile;
	private JButton trainingFileButton;
	private JLabel trainingFileLabel;
	
	/* NEW */
	private JRadioButton useTrainedModel;
	private JRadioButton trainDefaultModel;
	private JRadioButton trainCustomModel;
	private JComboBox model;
	private JPanel trainingOptionPanel;
	private File resultFile;
	
	private JLabel useTrainedModelLabel;
	private JLabel trainDefaultModelLabel;
	private JLabel trainCustomModelLabel;
	private JLabel resultFileLabel;
	
	/* ----- */
	
	/* SCORING */
	
	private JPanel scorePanel;
	private JRadioButton weightScoreOption;
	private JRadioButton learningScoreOption;
	
	/* ------- */
	
	private JLabel trainNewModelLabel;
	private JLabel existingModelLabel;
	private JLabel customModelLabel;
	private JLabel bayesModelLabel;
	private JLabel weightNameLabel;
	private JLabel clusterPriorLabel;
	private JLabel negativeExamplesLabel;
	private JLabel ignoreMissingLabel;
	
	SupervisedComplexTaskFactory clusterFactory;
	private ArrayList<Cluster> searchResults;
	private JTextField p;
	private File evaluationFile;
	private JButton evaluationFileButton;
	private JButton evaluateButton;
	private JLabel pLabel;
	private JLabel evaluationFileLabel;
	
	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final CyApplicationManager appManager;
	private final Logger logger;
	public MyControlPanel(final CySwingApplication swingApplication, final CyServiceRegistrar registrar, final CyApplicationManager appManager) {

		logger = LoggerFactory.getLogger(getClass());
		
		this.swingApplication = swingApplication;
		this.registrar = registrar;
		this.appManager = appManager;
		
		analyzeButton = new JButton("Analyze Network");
		analyzeButton.addActionListener(new ActionListener() { 
			  public void actionPerformed(ActionEvent e) { 
			    analyzeButtonPressed();
			  } 
			} );
		
		evaluateButton = new JButton("Evaluate Results");
		evaluateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					evaluateButtonPressed(evaluationFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
			
		final GroupLayout layout = new GroupLayout(this);
		final JPanel outerPanel = new JPanel();

		// Set up search panel
		outerSearchPanel = new JPanel();
		outerSearchPanel.setLayout(new BoxLayout(outerSearchPanel, BoxLayout.Y_AXIS));	
		outerSearchPanel.add(createSearchPanel());
		advancedSearchPanel = new CollapsiblePanel("> Advanced Search Parameters", createAdvancedSearchParams());
		outerSearchPanel.add(advancedSearchPanel);
		TitledBorder search = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Searching for Complexes");
		search.setTitleColor(Color.blue);
		outerSearchPanel.setBorder(search);
		
		// Set up train panel
		outerTrainPanel = new JPanel();
		outerTrainPanel.setLayout(new BoxLayout(outerTrainPanel, BoxLayout.Y_AXIS));	
		outerTrainPanel.add(createTrainPanel());
		advancedTrainPanel = new CollapsiblePanel("> Advanced Training Parameters", createAdvancedTrainParams());
		outerTrainPanel.add(advancedTrainPanel);
		TitledBorder train = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Training the Bayesian Network");
		train.setTitleColor(Color.blue);
		outerTrainPanel.setBorder(train);

		// Set up score panel
		scorePanel = createScorePanel();	
		TitledBorder score = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Scoring Complexes");
		score.setTitleColor(Color.blue);
		scorePanel.setBorder(score);

		
		// Set up evaluation panel
		evaluatePanel = createEvaluatePanel();
		evaluatePanel.setBorder(null);
		
		
		// Button to save the results to file
//		resultFileLabel = new JLabel("Save Results to File");
		JButton resultFileButton = new JButton("Save Results To File");
        resultFileButton.addActionListener(new ActionListener() {	 
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser resultChooser = new JFileChooser();
                int result = resultChooser.showOpenDialog(MyControlPanel.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    resultFile = resultChooser.getSelectedFile();
                    try {
						writeResultsToFile(resultFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
                }
            }
        }); 
		
		
		// Wrap all panels in an outer panel
		outerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(0,0,15,15); 
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 1.0;
		gbc.weightx = 1.0;
		gbc.gridy = 0;
		outerPanel.add(outerSearchPanel, gbc);
		gbc.gridy = 1;
		outerPanel.add(scorePanel, gbc);
		gbc.gridy = 2;
		outerPanel.add(analyzeButton, gbc);
		gbc.gridy = 3;
		outerPanel.add(resultFileButton, gbc);
		gbc.gridy = 4;
		outerPanel.add(evaluatePanel, gbc);
		gbc.gridy = 5;
		outerPanel.add(evaluateButton, gbc);
		
		
		// Add the outer panel to a JScrollPanel to make it 
		// vertically scrollable
		final JScrollPane scrollablePanel = new JScrollPane(outerPanel);
		scrollablePanel.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollablePanel.setBorder(null);
		setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER, true)
				.addComponent(scrollablePanel)
				);
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.CENTER, true)
				.addComponent(scrollablePanel)
				);
	}
	
	public JPanel createAdvancedSearchParams() {
		if (searchPanel != null) {
			if (advancedSearchPanel == null) {
				advancedSearchPanel = new JPanel();
				final GroupLayout layout = new GroupLayout(advancedSearchPanel);
				advancedSearchPanel.setLayout(layout);
				layout.setAutoCreateContainerGaps(true);
				layout.setAutoCreateGaps(true);
				
				layout.setHorizontalGroup(
						layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(searchLimitLabel)
									.addComponent(initTempLabel)
									.addComponent(tempScalingFactorLabel)
									.addComponent(overlapLimitLabel)
									.addComponent(minScoreThresholdLabel)
									.addComponent(minSizeLabel)
									.addComponent(useSelectedForSeedsLabel)
									.addComponent(numSeedsLabel)
									.addComponent(numResultsLabel))
							
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(searchLimit)
									.addComponent(initTemp)
									.addComponent(tempScalingFactor)
									.addComponent(overlapLimit)
									.addComponent(minScoreThreshold)
									.addComponent(minSize)
									.addComponent(useSelectedForSeedsPanel)
									.addComponent(numSeeds)
									.addComponent(numResults))
				);
				
				layout.setVerticalGroup(
						layout.createSequentialGroup()
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
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(useSelectedForSeedsLabel)
								.addComponent(useSelectedForSeedsPanel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(numSeedsLabel)
								.addComponent(numSeeds))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(numResultsLabel)
								.addComponent(numResults))
				);
			}
		}
		return advancedSearchPanel;
	}
	
	public JPanel createAdvancedTrainParams() {
		if (trainPanel != null) {
			if (advancedTrainPanel == null) {
				advancedTrainPanel = new JPanel();
				final GroupLayout layout = new GroupLayout(advancedTrainPanel);
				advancedTrainPanel.setLayout(layout);
				layout.setAutoCreateContainerGaps(true);
				layout.setAutoCreateGaps(true);
				
				layout.setHorizontalGroup(
						layout.createSequentialGroup()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(clusterPriorLabel)
									.addComponent(negativeExamplesLabel)
									.addComponent(ignoreMissingLabel))
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
									.addComponent(clusterPrior)
									.addComponent(negativeExamples)
									.addComponent(ignoreMissing))
				);
				
				layout.setVerticalGroup(
						layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(clusterPriorLabel)
								.addComponent(clusterPrior))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(negativeExamplesLabel)
								.addComponent(negativeExamples))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(ignoreMissingLabel)
								.addComponent(ignoreMissing))
				);
			
			}
		}
		return advancedTrainPanel;
	}
	
	
	public JPanel createSearchPanel() {
		if (searchPanel == null) {		
			searchPanel = new JPanel();
			final GroupLayout layout = new GroupLayout(searchPanel);
			searchPanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(true);
			layout.setAutoCreateGaps(true);
			
			String[] variants = { "ISA", "M ISA", "Greedy ISA"};
			chooser = new JComboBox(variants);

			chooser.addActionListener(
			  new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			      String searchVariant = (String) chooser.getSelectedItem();
			      if (searchVariant == "M ISA") {
			    	  checkNumNeighbors.setVisible(true);
			    	  checkNumNeighborsLabel.setVisible(true);
			      } else {
			    	  checkNumNeighbors.setVisible(false);
			    	  checkNumNeighborsLabel.setVisible(false);
			      }
			    }
			  }
			);
				
			chooserLabel = new JLabel("Search Variant");
			
			
			// Select protein graph
			ArrayList<String> networkNames = new ArrayList<String>();
			networkNames.add(" - Select Network - ");
			networkNames.addAll(getNetworkNames());
			
			proteinGraph = new JComboBox(networkNames.toArray());
			proteinGraphLabel = new JLabel("Protein graph");
			proteinGraph.addActionListener(
					// Automatically set the number of starting seeds to be 1/8 the number of nodes in the graph
					  new ActionListener() {
					    public void actionPerformed(ActionEvent e) {
					    	if (! proteinGraph.getSelectedItem().equals(" - Select Network - ")) {
								for (CyNetwork network: CyActivator.networkManager.getNetworkSet()) {	
									if (network.getRow(network).get(CyNetwork.NAME, String.class).equals(proteinGraph.getSelectedItem())) {
										int numNodes = network.getNodeCount();
										numSeeds.setText(Integer.toString(numNodes / 8));
									}
								}
					    	}
					    }
					  }
					);
			
			// Name of column containing weights
			weightName = new JComboBox(getEdgeColumnNames().toArray());
			weightNameLabel = new JLabel("Weight column in graph");
			
			// Number of neighbors to consider
			checkNumNeighbors = new JTextField("20");
			checkNumNeighborsLabel = new JLabel("Neighbors to consider");
			checkNumNeighbors.setVisible(false);
			checkNumNeighborsLabel.setVisible(false);
			
			// Number of Seeds
			numSeeds = new JTextField("10");
			numSeedsLabel = new JLabel("Number of Random Seeds");
			
			// Search Limit
			searchLimit = new JTextField("20");
			searchLimitLabel = new JLabel("Search Limit");
			
			// Initial Temperature
			initTemp = new JTextField("1.8");
			initTempLabel = new JLabel("Initial Temperature");
			
			// Temperature scaling factor (Rate of change of temperature)
			tempScalingFactor = new JTextField("0.88");
			tempScalingFactorLabel = new JLabel("Temperature Scaling Factor");
			
			// Permissible amount of overlap between complexes
			overlapLimit = new JTextField("0.75");
			overlapLimitLabel = new JLabel("Overlap Limit");
			
			// Minimum acceptable score
			minScoreThreshold = new JTextField("0");
			minScoreThresholdLabel = new JLabel("Minimum Complex Score");
			
			// Minimum acceptable complex size
			minSize = new JTextField("3");
			minSizeLabel = new JLabel("Minimum Complex Size");
			
			// Selecting seeds from file
			useSelectedForSeedsButton = new JButton("Seed File (.tab, .tsv)");
			useSelectedForSeedsButton.setEnabled(false);
			useSelectedForSeeds = new JCheckBox();
			useSelectedForSeedsLabel = new JLabel("Use Seeds From File");
			useSelectedForSeeds.addActionListener(
				new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(useSelectedForSeeds.isEnabled() && useSelectedForSeeds.isSelected()) {
							useSelectedForSeedsButton.setEnabled(true);
							numSeeds.setVisible(false);
							numSeedsLabel.setVisible(false);
						} else {
							useSelectedForSeedsButton.setEnabled(false);
							numSeeds.setVisible(true);
							numSeedsLabel.setVisible(true);
						}
					}
				}
				);	
			
			useSelectedForSeedsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0)); // Container for checkbox and button
			((FlowLayout)useSelectedForSeedsPanel.getLayout()).setHgap(0);
			useSelectedForSeedsPanel.add(useSelectedForSeeds);
			useSelectedForSeedsPanel.add(useSelectedForSeedsButton);
	        useSelectedForSeedsButton.addActionListener(new ActionListener() {	 
	            public void actionPerformed(ActionEvent e)
	            {
	            	if (useSelectedForSeedsButton.getText().equals("Seed File (.tab, .tsv)")) { 
		                JFileChooser seedsChooser = new JFileChooser();
		                int result = seedsChooser.showOpenDialog(MyControlPanel.this);
		                if (result == JFileChooser.APPROVE_OPTION) {
		                    useSelectedForSeedsFile = seedsChooser.getSelectedFile();
		                    if (useSelectedForSeedsFile == null) {
		                    	useSelectedForSeeds.setSelected(false);
								numSeeds.setVisible(true);
								numSeedsLabel.setVisible(true);	             
		                    } else {
		                    	useSelectedForSeeds.setSelected(true);
		                    	useSelectedForSeedsLabel.setText(useSelectedForSeedsFile.getName());
		                    	useSelectedForSeedsButton.setText("Remove File");
								numSeeds.setVisible(false);
								numSeedsLabel.setVisible(false);
		                    }
		                }
	            	} else {
	            		useSelectedForSeeds.setSelected(false);
	                	useSelectedForSeedsLabel.setText("Use Seeds From File");
	                	useSelectedForSeedsButton.setText("Seed File (.tab, .tsv)");
						numSeeds.setVisible(true);
						numSeedsLabel.setVisible(true);		                	
	            	}
	            }
	        });
	        
	        // Number of results to save to file
	        numResults = new JTextField("10");
	        numResultsLabel = new JLabel("Number of results to display");
			

			chooser.addActionListener(
			  new ActionListener() {
			    public void actionPerformed(ActionEvent e) {
			      String searchVariant = (String) chooser.getSelectedItem();
			      if (searchVariant == "M ISA") {
			    	  checkNumNeighbors.setVisible(true);
			    	  checkNumNeighborsLabel.setVisible(true);
			      } else {
			    	  checkNumNeighbors.setVisible(false);
			    	  checkNumNeighborsLabel.setVisible(false);
			      }
			    }
			  }
			);
	        
	        // Add search components to layout
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(proteinGraphLabel)
								.addComponent(chooserLabel)
								.addComponent(checkNumNeighborsLabel))
						
//								.addComponent(resultFileLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(proteinGraph)
								.addComponent(chooser)
								.addComponent(checkNumNeighbors))
//								.addComponent(resultFileButton))
			);
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(proteinGraphLabel)
							.addComponent(proteinGraph))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(chooserLabel)
							.addComponent(chooser))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(checkNumNeighborsLabel)
							.addComponent(checkNumNeighbors))
//					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
//							.addComponent(resultFileLabel)
//							.addComponent(resultFileButton))
			);
		}
		return searchPanel;
	}
	
	public JPanel createScorePanel() {
		
		if (scorePanel == null) {
			scorePanel = new JPanel();
			
			weightScoreOption = new JRadioButton("Use only edge information (no learning)");
			weightScoreOption.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {
	                if (weightScoreOption.isSelected()) {
	                	outerTrainPanel.setVisible(false);
	                }
	            }
			});
			
			learningScoreOption = new JRadioButton("Use supervised learning with a Bayesian model");
			learningScoreOption.addActionListener(new ActionListener() {
	            public void actionPerformed(ActionEvent e)
	            {
	                if (learningScoreOption.isSelected()) {
	                	outerTrainPanel.setVisible(true);
	                }
	            }
			});
			
			scorePanel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(0,0,15,15); 
			gbc.fill = GridBagConstraints.HORIZONTAL;
			gbc.weighty = 1.0;
			gbc.weightx = 1.0;
			gbc.gridy = 0;			
			scorePanel.add(weightScoreOption, gbc);
			gbc.gridy = 1;
			scorePanel.add(learningScoreOption, gbc);
			gbc.gridy = 2;
			outerTrainPanel.setVisible(false);
        	scorePanel.add(outerTrainPanel, gbc);
			
			ButtonGroup scoringButtons = new ButtonGroup();
			scoringButtons.add(weightScoreOption);
			scoringButtons.add(learningScoreOption);
			
		}
		return scorePanel;
	}
	
	public JPanel createTrainPanel() {
		if (trainPanel == null) {
			
			final JPanel modelPanel = new JPanel();
			final JPanel trainingFilePanel = new JPanel();
			
			trainPanel = new JPanel();
			trainPanel.setLayout(new BoxLayout(trainPanel, BoxLayout.Y_AXIS));	

			
			// By Default, the model selection and training file buttons should be disabled
			// until an option is selected
			modelPanel.setVisible(false);
			trainingFilePanel.setVisible(false);
			
			
			// Use Trained Model?
			useTrainedModel = new JRadioButton("Provide a trained model");
			useTrainedModel.addActionListener(new ActionListener() {
	        	 
	            public void actionPerformed(ActionEvent e)
	            {
	                if (useTrainedModel.isSelected()) {
	                	modelPanel.setVisible(true);
	                	trainingFilePanel.setVisible(false);
	                	
	                	clusterPrior.setEnabled(false);
	                	negativeExamples.setEnabled(false);
	                	ignoreMissing.setEnabled(false);
	                	
	                }
	            }
	        }); 
			
			// Train the Default (Built-in) Model?
			trainDefaultModel = new JRadioButton("Train the built-in model");
			trainDefaultModel.addActionListener(new ActionListener() {
        	 
	            public void actionPerformed(ActionEvent e)
	            {
	                if (trainDefaultModel.isSelected()) {
	                	modelPanel.setVisible(false);
	                	trainingFilePanel.setVisible(true);
	                	
	                	clusterPrior.setEnabled(true);
	                	negativeExamples.setEnabled(true);
	                	ignoreMissing.setEnabled(true);
	                }
	            }
	        }); 
			
			// Train a Custom Model?
			trainCustomModel = new JRadioButton("Train a custom model");
			trainCustomModel.addActionListener(new ActionListener() {
	        	 
	            public void actionPerformed(ActionEvent e)
	            {
	                if (trainCustomModel.isSelected()) {
	                	modelPanel.setVisible(true);
	                	trainingFilePanel.setVisible(true);
	                	
	                	clusterPrior.setEnabled(true);
	                	negativeExamples.setEnabled(true);
	                	ignoreMissing.setEnabled(true);
	                }
	            }
	        }); 

			
			// Options for training: train built-in model, train a custom model,
			// or provide an already-trained model. Radio buttons controlling these
			// options are in one ButtonGroup (only one can be selected at a time
			ButtonGroup trainingButtons = new ButtonGroup();
			trainingButtons.add(useTrainedModel);
			trainingButtons.add(trainDefaultModel);
			trainingButtons.add(trainCustomModel);
			

			// Create the panel holding radio buttons for training options
			trainingOptionPanel = new JPanel();
			trainingOptionPanel.setLayout(new BoxLayout(trainingOptionPanel, BoxLayout.Y_AXIS));
			trainingOptionPanel.add(trainDefaultModel);
			trainingOptionPanel.add(useTrainedModel);
			trainingOptionPanel.add(trainCustomModel);
			TitledBorder border = new TitledBorder("Select a Training Option:");
		    border.setTitleJustification(TitledBorder.CENTER);
		    border.setTitlePosition(TitledBorder.TOP);
			trainingOptionPanel.setBorder(border);
			
			
			model = new JComboBox(getNetworkNames().toArray());
			JLabel modelLabel = new JLabel("Select Model");
			modelPanel.add(modelLabel);
			modelPanel.add(model);
			
			
			clusterPrior = new JTextField("1E-4");
			clusterPriorLabel = new JLabel("Cluster Probability Prior");
			
			negativeExamples = new JTextField("2000");
			negativeExamplesLabel = new JLabel("Generate # of Negative Examples");
			
			trainingFileButton = new JButton("Training File (.tab, .tsv)");
			trainingFileLabel = new JLabel("Positive Training Data");
	        trainingFileButton.addActionListener(new ActionListener() {	 
	            public void actionPerformed(ActionEvent e)
	            {
	                JFileChooser trainingChooser = new JFileChooser();
	                int result = trainingChooser.showOpenDialog(MyControlPanel.this);
	                if (result == JFileChooser.APPROVE_OPTION) {
	                    trainingFile = trainingChooser.getSelectedFile();
	                    trainingFileLabel.setText(trainingFile.getName());
	                    
	                    // Get number of positive training examples in order to set number of negative training examples
	                    LineNumberReader lnr;
						try {
							lnr = new LineNumberReader(new FileReader(trainingFile));
		                    lnr.skip(Long.MAX_VALUE);
							negativeExamples.setText(Integer.toString(lnr.getLineNumber() + 1));
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
	                    
	                }
	            }
	        }); 
	        trainingFilePanel.add(trainingFileLabel);
	        trainingFilePanel.add(trainingFileButton);
			
			ignoreMissing = new JCheckBox();
			ignoreMissing.setSelected(true);
			ignoreMissingLabel = new JLabel("Ignore Missing Nodes");
			
			
			
			// Set the alignment on the panels to be left-justified
			trainingOptionPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
			modelPanel.setAlignmentX( Component.CENTER_ALIGNMENT );
			trainingFilePanel.setAlignmentX( Component.CENTER_ALIGNMENT );	
			

		trainPanel.add(trainingOptionPanel);
		trainPanel.add(modelPanel);
		trainPanel.add(trainingFilePanel);
		
		}
		return trainPanel;
	}
	
	public JPanel createEvaluatePanel() {
		if (evaluatePanel == null) {
			evaluatePanel = new JPanel();
			final GroupLayout layout = new GroupLayout(evaluatePanel);
			evaluatePanel.setLayout(layout);
			layout.setAutoCreateContainerGaps(true);
			layout.setAutoCreateGaps(true);
			
			TitledBorder eval = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Evaluate Results");
			evaluatePanel.setBorder(eval);
			
			p = new JTextField("0.5");
			pLabel = new JLabel("p");
			
			evaluationFileLabel = new JLabel("File of Testing Complexes");
			evaluationFileButton = new JButton("Select Evaluation File");
	        evaluationFileButton.addActionListener(new ActionListener() {
	        	 
	            public void actionPerformed(ActionEvent e)
	            {
	                JFileChooser evaluationChooser = new JFileChooser();
	                int evaluation = evaluationChooser.showOpenDialog(MyControlPanel.this);
	                if (evaluation == JFileChooser.APPROVE_OPTION) {
	                    evaluationFile = evaluationChooser.getSelectedFile();
	                    evaluationFileLabel.setText(evaluationFile.getName());
	                }
	            }
	        }); 
			
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(pLabel)
								.addComponent(evaluationFileLabel))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(p)
								.addComponent(evaluationFileButton))
					);
			layout.setVerticalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(pLabel)								
								.addComponent(p))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(evaluationFileLabel)
								.addComponent(evaluationFileButton))				
					);
			
			Component[] components = evaluatePanel.getComponents(); 
			for(int i = 0; i < components.length; i++) {
				components[i].setVisible(false);
			}
			evaluateButton.setVisible(false);
		}
		return evaluatePanel;
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
		ImageIcon icon = new ImageIcon(MyControlPanel.class.getResource("/images/SCODElogo2.png"));
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
		
		Integer inputValidation = validateInput();
		if (inputValidation == 1) {	
			InputTask inputTask = createInputTask();
			clusterFactory = new SupervisedComplexTaskFactory(inputTask, appManager);		
			DialogTaskManager dialogTaskManager = registrar.getService(DialogTaskManager.class);
			TaskIterator taskIter = clusterFactory.createTaskIterator();
			dialogTaskManager.execute(taskIter);

			Component[] evalComponents = evaluatePanel.getComponents();
			for(int i = 0; i < evalComponents.length; i++) {
				evalComponents[i].setVisible(true);
			}
			evaluatePanel.setVisible(true);
			evaluateButton.setVisible(true);
			TitledBorder eval = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Evaluate Results");
			eval.setTitleColor(Color.blue);
			evaluatePanel.setBorder(eval);			

		} else if (inputValidation == 2) {
			JOptionPane.showMessageDialog(this, "Please select a training option");
		} else if (inputValidation == 3) {
			JOptionPane.showMessageDialog(this, "Please load a positive training file");
		} else if (inputValidation == 4) {
			JOptionPane.showMessageDialog(this, "Please select a scoring option");
		} else if (inputValidation == 5) {
			JOptionPane.showMessageDialog(this, "Please load a seed file, or uncheck 'Use Starting Seeds From File' under the advanced search parameters.");
		} else if (inputValidation == 6) {
			JOptionPane.showMessageDialog(this, "Please select the protein graph under 'Search'");
		}
	}
	
	private void printResults() {
		searchResults = clusterFactory.getSearchTask().getResults();
		for (Cluster network : searchResults) {
			CySubNetwork result = network.getSubNetwork();
			List<CyNode> nodes = result.getNodeList();
			List<CyEdge> edges = result.getEdgeList();
			System.out.println(result.getRow(result).get(CyNetwork.NAME, String.class) + ": " + nodes.size() + " nodes and " + edges.size() + " edges.");
		}
	}
	
	private void writeResultsToFile(File resultFile) throws IOException {
		if (clusterFactory == null || clusterFactory.getSearchTask() == null) {
			JOptionPane.showMessageDialog(this, "You have not performed search yet.");
		} else if (clusterFactory.getSearchTask().getResults().size() == 0) {
			JOptionPane.showMessageDialog(this, "Search has not returned any results.");
		} else {
			searchResults = clusterFactory.getSearchTask().getResults();
			if (! resultFile.exists()) { resultFile.createNewFile(); }
			
			FileWriter writer = new FileWriter(resultFile);
			String fileContents = "";
			
			int counter = 1;
			for (Cluster network : searchResults) {
				CySubNetwork result = network.getSubNetwork();
				List<CyNode> nodes = result.getNodeList();
				
				String complexName = (result.getRow(result).get(CyNetwork.NAME, String.class) == null) ? 
						"Complex #" + counter 
						: result.getRow(result).get(CyNetwork.NAME, String.class);
				fileContents = fileContents + counter +"\t" + complexName + "\t";
				
				
				
				for (CyNode n : nodes) {
					CyNetwork nodeNetwork = getNetworkPointer(); // The network pointer is set in SearchTask.java
					fileContents = fileContents + " " + (nodeNetwork.getDefaultNodeTable().getRow(n.getSUID()).get("shared name", String.class));
				}
				fileContents = fileContents + "\n";
				counter++;
			}
			
			writer.write(fileContents);
			writer.close();
			JOptionPane.showMessageDialog(this, "Results written to file '" + resultFile.getName() + "'");
		}
	}
	
	private void evaluateButtonPressed(File evaluateFile) throws IOException {
		
		if (evaluateFile == null) {
			JOptionPane.showMessageDialog(this, "You must provide an evaluation file.");
		} else {
			
			ArrayList< Set<String> > resultComplexes = new ArrayList< Set<String> >();
			ArrayList< Set<String> > evalComplexes = new ArrayList< Set<String> >();	
			
			searchResults = clusterFactory.getSearchTask().getResults();
			for (Cluster network : searchResults) {
				CySubNetwork result = network.getSubNetwork();
				System.out.print("Printing a complex:");
				List<CyNode> nodes = result.getNodeList();
				Set<String> nodeNames = new HashSet<String>();
				for (CyNode n : nodes) {
					CyNetwork nodeNetwork = getNetworkPointer(); // The network pointer is set in SearchTask.java
					nodeNames.add(nodeNetwork.getDefaultNodeTable().getRow(n.getSUID()).get("shared name", String.class));
					System.out.print(" " + nodeNetwork.getDefaultNodeTable().getRow(n.getSUID()).get("shared name", String.class));
				}
				System.out.println("");
				resultComplexes.add(nodeNames);
			}
	
			System.out.println("");
			System.out.println("");
	
			FileReader evalfileReader = new FileReader(evaluateFile);
			BufferedReader evalbufferedReader = new BufferedReader(evalfileReader);
			String line = null ;
			while((line = evalbufferedReader.readLine()) != null) {
					String[] l = line.split("\t");
					if (l.length == 3) {
						System.out.println("Line: " + line);
						String complexes_string = l[2];
						HashSet eval_complexes = new HashSet(Arrays.asList(complexes_string.split(" ")));
						evalComplexes.add(eval_complexes);
					}
			}
			evalbufferedReader.close();
			
			System.out.println("");
			System.out.println("");
		
			int countPredicted = 0;
			int countKnown = 0;
			
			for (Set<String> predicted : resultComplexes) {
				for (Set<String> known : evalComplexes) {
					
					Set<String> intersection = new HashSet<String>(predicted);
					intersection.retainAll(known);
					
					double C = intersection.size() ; 
					double A = predicted.size() - C ; 
					double B = known.size() - C ;
					
					float pVal = Float.parseFloat(p.getText());
					if ( ((C / (A + C)) > pVal) && ((C / (B + C)) > pVal) ) {
						countPredicted++;
					}
				}
			}
			
			for (Set<String> known : evalComplexes) {
				for (Set<String> predicted : resultComplexes) {
					
					Set<String> intersection = new HashSet<String>(known);
					intersection.retainAll(predicted);
					
					double C = intersection.size() ; 
					double A = predicted.size() - C ; 
					double B = known.size() - C ;
					
					float pVal = Float.parseFloat(p.getText());
					if ( ((C / (A + C)) > pVal) && ((C / (B + C)) > pVal) ) {
						countKnown++;
					}
				}
			}
			
			double recall = (double) countKnown / (double) evalComplexes.size() ;
			double precision = (double) countPredicted / (double) resultComplexes.size() ;
			
			JOptionPane.showMessageDialog(this, "Recall: " + recall + "\nPrecision: " + precision, "Evaluation Scoring", JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	private InputTask createInputTask() {
		InputTask inputTask = new InputTask();
		
		inputTask.graphName = proteinGraph.getSelectedItem().toString();
		
		ListSingleSelection<String> inputChooser = new ListSingleSelection<String>("Greedy ISA", "M ISA", "ISA");
		inputChooser.setSelectedValue(chooser.getSelectedItem().toString());
		inputTask.chooser = inputChooser;
		
		int inputCheckNumNeighbors = Integer.parseInt(checkNumNeighbors.getText());
		inputTask.checkNumNeighbors = inputCheckNumNeighbors;
		
		boolean inputUseSelectedForSeeds = useSelectedForSeeds.isSelected();
		inputTask.useSelectedForSeeds = inputUseSelectedForSeeds;
		
		int inputNumSeeds = Integer.parseInt(numSeeds.getText());
		inputTask.numSeeds = inputNumSeeds;
		
		int inputSearchLimit = Integer.parseInt(searchLimit.getText());
		inputTask.searchLimit = inputSearchLimit;
		
		double inputInitTemp = Double.parseDouble(initTemp.getText());
		inputTask.initTemp = inputInitTemp;
		
		double inputTempScalingFactor = Double.parseDouble(tempScalingFactor.getText());
		inputTask.tempScalingFactor = inputTempScalingFactor;
		
		double inputOverlapLimit = Double.parseDouble(overlapLimit.getText());
		inputTask.overlapLimit = inputOverlapLimit;
		
		double inputMinScoreThreshold = Double.parseDouble(minScoreThreshold.getText());
		inputTask.minScoreThreshold = inputMinScoreThreshold;
		
		int inputMinSize = Integer.parseInt(minSize.getText());
		inputTask.minSize = inputMinSize;
		
		boolean inputTrainNewModel = trainCustomModel.isSelected() || trainDefaultModel.isSelected();
		inputTask.trainNewModel = inputTrainNewModel;
		
		ListSingleSelection<String> inputExistingModel = new ListSingleSelection<String>(getNetworkNames());
		inputExistingModel.setSelectedValue(model.getSelectedItem().toString());
		inputTask.existingModel = inputExistingModel;
		
		boolean inputCustomModel = trainCustomModel.isSelected();
		inputTask.customModel = inputCustomModel;
		
		ListSingleSelection<String> inputBayesModel = new ListSingleSelection<String>(getNetworkNames());
		inputBayesModel.setSelectedValue(model.getSelectedItem().toString());
		inputTask.bayesModel = inputBayesModel;
		
		ListSingleSelection<String> inputWeightName = new ListSingleSelection<String>(getEdgeColumnNames());
//		inputWeightName.setSelectedValue(weightName.getSelectedItem().toString());
		inputWeightName.setSelectedValue("weight");
		inputTask.weightName = inputWeightName;
		
		double inputClusterPrior = Double.parseDouble(clusterPrior.getText());
		inputTask.clusterPrior = inputClusterPrior;
		
		int inputNegativeExamples = Integer.parseInt(negativeExamples.getText());
		inputTask.negativeExamples = inputNegativeExamples;
		
		inputTask.trainingFile = trainingFile;
		
		boolean inputIgnoreMissing = ignoreMissing.isSelected();
		inputTask.ignoreMissing = inputIgnoreMissing;
		
		inputTask.resultFile = resultFile;
		
		inputTask.selectedSeedFile = useSelectedForSeedsFile;
		
		inputTask.supervisedLearning = learningScoreOption.isSelected();
		
		inputTask.numResults = Integer.parseInt((numResults.getText()));
		
		return inputTask;
	}
	
	private Integer validateInput() {

		if (learningScoreOption.isSelected() && !useTrainedModel.isSelected() && !trainDefaultModel.isSelected() && !trainCustomModel.isSelected()) {
			// User has not selected a training option
			return 2;
		} 
		else if ( learningScoreOption.isSelected() && (trainDefaultModel.isSelected() || trainCustomModel.isSelected() ) && trainingFile == null ) {
			// User has not provided a positive training file for one of the training options
			return 3;
		}
		else if (proteinGraph.getSelectedItem().equals(" - Select Network - ")) {
			// User has not selected a protein graph
			return 6;
		}
		else if (!weightScoreOption.isSelected() && !learningScoreOption.isSelected()) {
			// User has not selected a scoring option
			return 4;
		}
		else if (useSelectedForSeeds.isSelected() && (useSelectedForSeedsFile == null)) {
			// User has not provided a seed file
			return 5;
		}
		return 1;
	}
	
	private CyNetwork getNetworkPointer() {
		CyNetwork networkptr = null;
		for (CyNetwork network: CyActivator.networkManager.getNetworkSet()) {
			if (network.getRow(network).get(CyNetwork.NAME, String.class).equals(proteinGraph.getSelectedItem().toString())) {
				networkptr = network;
			}
		}
		return networkptr;
	}
	
}