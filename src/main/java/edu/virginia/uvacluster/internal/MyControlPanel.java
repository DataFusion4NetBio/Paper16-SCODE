package edu.virginia.uvacluster.internal;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import org.cytoscape.model.CyNetwork;
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
	
	private JLabel trainNewModelLabel;
	private JLabel existingModelLabel;
	private JLabel customModelLabel;
	private JLabel bayesModelLabel;
	private JLabel weightNameLabel;
	private JLabel clusterPriorLabel;
	private JLabel negativeExamplesLabel;
	private JLabel ignoreMissingLabel;
	
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
					evaluateButtonPressed(resultFile, evaluationFile);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		
		final GroupLayout layout = new GroupLayout(this);
		final JPanel outerPanel = new JPanel();


		// Set up search panel
		final JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));	
		searchPanel.add(createSearchPanel());
		
		final CollapsiblePanel advancedSearchPanel = new CollapsiblePanel("> Advanced Search Parameters", createAdvancedSearchParams());
		searchPanel.add(advancedSearchPanel);
		TitledBorder search = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Search");
		searchPanel.setBorder(search);
		
		// Set up train panel
		final JPanel trainPanel = new JPanel();
		trainPanel.setLayout(new BoxLayout(trainPanel, BoxLayout.Y_AXIS));	
		trainPanel.add(createTrainPanel());
		
		final CollapsiblePanel advancedTrainPanel = new CollapsiblePanel("> Advanced Training Parameters", createAdvancedTrainParams());
		trainPanel.add(advancedTrainPanel);
		TitledBorder train = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Train");
		trainPanel.setBorder(train);

		
		// Set up evaluation panel
		evaluatePanel = createEvaluatePanel();
		evaluatePanel.setBorder(null);

		outerPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weighty = 1.0;
		gbc.gridy = 0;
		outerPanel.add(trainPanel, gbc);
		gbc.gridy = 1;
		outerPanel.add(searchPanel, gbc);
		gbc.gridy = 2;
		outerPanel.add(analyzeButton, gbc);
		gbc.gridy = 3;
		outerPanel.add(evaluatePanel, gbc);
		gbc.gridy = 4;
		outerPanel.add(evaluateButton, gbc);
		
		final JScrollPane scrollablePanel = new JScrollPane(outerPanel);
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
		JPanel advancedPanel = null;
		if (searchPanel != null) {
			advancedPanel = new JPanel();
			final GroupLayout layout = new GroupLayout(advancedPanel);
			advancedPanel.setLayout(layout);
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
								.addComponent(numSeedsLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(searchLimit)
								.addComponent(initTemp)
								.addComponent(tempScalingFactor)
								.addComponent(overlapLimit)
								.addComponent(minScoreThreshold)
								.addComponent(minSize)
								.addComponent(useSelectedForSeedsPanel)
								.addComponent(numSeeds))
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
			);
		}
		return advancedPanel;
		
	}
	
	public JPanel createAdvancedTrainParams() {
		JPanel advancedPanel = null;
		if (trainPanel != null) {
			advancedPanel = new JPanel();
			final GroupLayout layout = new GroupLayout(advancedPanel);
			advancedPanel.setLayout(layout);
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
		return advancedPanel;
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
			proteinGraph = new JComboBox(getNetworkNames().toArray());
			proteinGraphLabel = new JLabel("Protein graph");
			
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
			minScoreThreshold = new JTextField("-4E2");
			minScoreThresholdLabel = new JLabel("Minimum Complex Score");
			
			// Minimum acceptable complex size
			minSize = new JTextField("3");
			minSizeLabel = new JLabel("Minimum Complex Size");
			
			// Selecting seeds from file
			useSelectedForSeedsButton = new JButton("Select Seed File");
			useSelectedForSeedsButton.setEnabled(false);
			useSelectedForSeeds = new JCheckBox();
			useSelectedForSeedsLabel = new JLabel("Use Starting Seeds From File");
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
			
			useSelectedForSeedsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0)); // Container for checkbox and button
			useSelectedForSeedsPanel.add(useSelectedForSeeds);
			useSelectedForSeedsPanel.add(useSelectedForSeedsButton);
	        useSelectedForSeedsButton.addActionListener(new ActionListener() {	 
	            public void actionPerformed(ActionEvent e)
	            {
	            	if (useSelectedForSeedsButton.getText().equals("Select Seed File")) { 
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
	                	useSelectedForSeedsLabel.setText("Use Starting Seeds From File");
	                	useSelectedForSeedsButton.setText("Select Seed File");
						numSeeds.setVisible(true);
						numSeedsLabel.setVisible(true);		                	
                }
	            }
	        }); 
			

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
			
			// Save the results to file
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
	        
	        // Add search components to layout
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(proteinGraphLabel)
								.addComponent(weightNameLabel)
								.addComponent(chooserLabel)
								.addComponent(checkNumNeighborsLabel)
								.addComponent(resultFileLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(proteinGraph)
								.addComponent(weightName)
								.addComponent(chooser)
								.addComponent(checkNumNeighbors)
								.addComponent(resultFileButton))
			);
			
			layout.setVerticalGroup(
					layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(proteinGraphLabel)
							.addComponent(proteinGraph))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(weightNameLabel)
							.addComponent(weightName))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(chooserLabel)
							.addComponent(chooser))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(checkNumNeighborsLabel)
							.addComponent(checkNumNeighbors))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(resultFileLabel)
							.addComponent(resultFileButton))
			);
		}
		return searchPanel;
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
			
			trainingFileButton = new JButton("Select Training File");
			trainingFileLabel = new JLabel("Load Positive Training Data");
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
			
			TitledBorder eval = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Evaluate Results");
			evaluatePanel.setBorder(eval);
			
			p = new JTextField("0.5");
			pLabel = new JLabel("p");
			
			evaluationFileLabel = new JLabel("File of Predicted Complexes");
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
		
		Integer inputValidation = validateInput();
		if (inputValidation == 1) {	
			InputTask inputTask = createInputTask();
			SupervisedComplexTaskFactory clusterFactory = new SupervisedComplexTaskFactory(inputTask, appManager);		
			DialogTaskManager dialogTaskManager = registrar.getService(DialogTaskManager.class);
			TaskIterator taskIter = clusterFactory.createTaskIterator();
			dialogTaskManager.execute(taskIter);
			
			if (resultFile != null) {
				Component[] evalComponents = evaluatePanel.getComponents();
				for(int i = 0; i < evalComponents.length; i++) {
					evalComponents[i].setVisible(true);
				}
				evaluatePanel.setVisible(true);
				evaluateButton.setVisible(true);
				TitledBorder eval = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Evaluate Results");
				evaluatePanel.setBorder(eval);
			}
		} else if (inputValidation == 2) {
			JOptionPane.showMessageDialog(this, "Please select a training option");
		} else if (inputValidation == 3) {
			JOptionPane.showMessageDialog(this, "Please load a positive training file");
		} else if (inputValidation == 4) {
			JOptionPane.showMessageDialog(this, "Please select the edge weight column under 'Search'");
		} else if (inputValidation == 5) {
			JOptionPane.showMessageDialog(this, "Please load a seed file, or uncheck 'Use Starting Seeds From File' under the advanced search parameters.");
		}
	}
	
	private void evaluateButtonPressed(File resultFile, File evaluateFile) throws IOException {
		ArrayList< Set<String> > resultComplexes = new ArrayList< Set<String> >();
		ArrayList< Set<String> > evalComplexes = new ArrayList< Set<String> >();	
		FileReader fileReader = new FileReader(resultFile) ;
		BufferedReader bufferedReader = new BufferedReader(fileReader) ;
		String line = bufferedReader.readLine() ; 
		while((line = bufferedReader.readLine()) != null) {
			String[] l = line.split("\t");
			String complexes_string = l[2];
			HashSet result_complexes = new HashSet(Arrays.asList(complexes_string.split(" ")));
			resultComplexes.add(result_complexes);
		}

		System.out.println("");
		System.out.println("");

		FileReader evalfileReader = new FileReader(evaluateFile);
		BufferedReader evalbufferedReader = new BufferedReader(evalfileReader);
		line = evalbufferedReader.readLine() ;
		while((line = evalbufferedReader.readLine()) != null) {
				String[] l = line.split("\t");
				if (l.length == 3) {
					System.out.println("Line: " + line);
					String complexes_string = l[2];
					HashSet eval_complexes = new HashSet(Arrays.asList(complexes_string.split(" ")));
					evalComplexes.add(eval_complexes);
				}
		}
		
		System.out.println("");
		System.out.println("");
	
		int countPredicted = 0;
		int countKnown = 0;
		
		for (int i = 0; i < resultComplexes.size(); i++) {
			Set<String> predicted = resultComplexes.get(i);
			Boolean predictedAlreadyMatched = false;
			for(int j = 0; j < evalComplexes.size(); j++) {
				double A = 0; 
				double B = 0; 
				double C = 0; 
				Set<String> known = evalComplexes.get(j);
				
				Set<String> intersection = new HashSet<String>(predicted); // use the copy constructor
				intersection.retainAll(known);
				
				C = intersection.size();
				A = predicted.size() - C;
				B = known.size() - C;
				
				float pVal = Float.parseFloat(p.getText());
				
				if ( (C / (A + C) > pVal) && (C / (B + C)) > pVal) {
					if (!predictedAlreadyMatched) {
						// This forces to iterate to the next predicted complex, so that a predicted complex is not counted twice
						// if it matches with multiple known complexes
						countPredicted ++;
						predictedAlreadyMatched = true;
					}
					countKnown++;
				}
			}
		}
		
		// Get the number of positive examples from the test set
		LineNumberReader  lnr = new LineNumberReader(new FileReader(trainingFile));
		lnr.skip(Long.MAX_VALUE);
		int numPosTrainingExamples = lnr.getLineNumber(); 
		lnr.close();
		
		double recall = countKnown / evalComplexes.size() ;
		double precision = countPredicted / resultComplexes.size();
		
		JOptionPane.showMessageDialog(this, "Recall: " + recall + "\nPrecision: " + precision, "Evaluation Scoring", JOptionPane.INFORMATION_MESSAGE);

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
		inputWeightName.setSelectedValue(weightName.getSelectedItem().toString());
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
		return inputTask;
	}
	
	private Integer validateInput() {

		if (!useTrainedModel.isSelected() && !trainDefaultModel.isSelected() && !trainCustomModel.isSelected()) {
			// User has not selected a training option
			return 2;
		} 
		else if ( (trainDefaultModel.isSelected() || trainCustomModel.isSelected() ) && trainingFile == null ) {
			// User has not provided a positive training file for one of the training options
			return 3;
		}
		else if (weightName.getSelectedItem().equals("- Select Column -")) {
			// User has not selected a weight column
			return 4;
		}
		else if (useSelectedForSeeds.isSelected() && (useSelectedForSeedsFile == null)) {
			// User has not provided a seed file
			return 5;
		}
		return 1;
	}
	
}