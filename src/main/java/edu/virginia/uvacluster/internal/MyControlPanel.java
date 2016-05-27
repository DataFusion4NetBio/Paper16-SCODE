package edu.virginia.uvacluster.internal;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
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
import org.cytoscape.task.NetworkTaskFactory;
import org.cytoscape.work.AbstractTask;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;
import org.cytoscape.work.Tunable;
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
	private JTextField checkNumNeighbors;
	private JCheckBox useSelectedForSeeds;
	private File useSelectedForSeedsFile;
	private JButton useSelectedForSeedsButton;
	private JTextField numSeeds;
	private JTextField searchLimit;
	private JTextField initTemp;
	private JTextField tempScalingFactor;
	private JTextField overlapLimit;
	private JTextField minScoreThreshold;
	private JTextField minSize;
	
	private JLabel chooserLabel;
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
	private JCheckBox customModel;
	private JComboBox bayesModel;
	private JComboBox weightName;
	private JTextField clusterPrior;
	private JTextField negativeExamples;
	private JCheckBox ignoreMissing;
	private File trainingFile;
	private JButton trainingFileButton;
	private File resultFile;
	private JLabel trainingFileLabel;
	private JLabel resultFileLabel;
	
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
		final JPanel outerPanel = new JPanel(new BorderLayout());
		
		// Set up train panel
		final JPanel trainPanel = new JPanel();
		trainPanel.setLayout(new BoxLayout(trainPanel, BoxLayout.Y_AXIS));	
		trainPanel.add(createTrainPanel());
		
		final CollapsiblePanel advancedTrainPanel = new CollapsiblePanel("> Advanced Training Parameters", createAdvancedTrainParams());
		trainPanel.add(advancedTrainPanel);
		TitledBorder train = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Train");
		trainPanel.setBorder(train);
		trainPanel.setSize(trainPanel.getPreferredSize());

		// Set up search panel
		final JPanel searchPanel = new JPanel();
		searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.Y_AXIS));	
		searchPanel.add(createSearchPanel());
		
		final CollapsiblePanel advancedSearchPanel = new CollapsiblePanel("> Advanced Search Parameters", createAdvancedSearchParams());
		searchPanel.add(advancedSearchPanel);
		TitledBorder search = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Search");
		searchPanel.setBorder(search);
		Dimension search_dimension = searchPanel.getPreferredSize();
		Double search_height = search_dimension.getHeight();
		Dimension train_dimension = trainPanel.getPreferredSize();
		Double train_width = train_dimension.getWidth();
		searchPanel.setSize(new Dimension(train_width.intValue(), search_height.intValue()));
		
		// Set up evaluation panel
		final JPanel evaluationPanel = createEvaluatePanel();
		evaluationPanel.setBorder(null);

		outerPanel.add(searchPanel);
		outerPanel.add(trainPanel);
		outerPanel.add(analyzeButton);
		outerPanel.add(evaluationPanel);
		outerPanel.add(evaluateButton);
		outerPanel.setLayout(new BoxLayout(outerPanel, BoxLayout.Y_AXIS));
		outerPanel.setSize(outerPanel.getPreferredSize());
		
//		final JScrollPane scrollablePanel = new JScrollPane(outerPanel);
		
		setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		
		layout.setHorizontalGroup(layout.createParallelGroup(Alignment.CENTER, true)
				.addComponent(outerPanel)
				);
		layout.setVerticalGroup(layout.createParallelGroup(Alignment.CENTER, true)
				.addComponent(outerPanel)
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
								.addComponent(numSeedsLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(searchLimit)
								.addComponent(initTemp)
								.addComponent(tempScalingFactor)
								.addComponent(overlapLimit)
								.addComponent(minScoreThreshold)
								.addComponent(minSize)
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
				
			chooserLabel = new JLabel("Search Variant: ");
			
			checkNumNeighbors = new JTextField("20");
			checkNumNeighborsLabel = new JLabel("Neighbors to consider: ");
			checkNumNeighbors.setVisible(false);
			checkNumNeighborsLabel.setVisible(false);
			
//			useSelectedForSeeds = new JCheckBox();
//			useSelectedForSeedsLabel = new JLabel("Use Selected Nodes as Seeds");
//			useSelectedForSeeds.addActionListener(
//				new ActionListener() {
//					public void actionPerformed(ActionEvent e) {
//						if(useSelectedForSeeds.isEnabled() && useSelectedForSeeds.isSelected()) {
//							numSeeds.setVisible(false);
//							numSeedsLabel.setVisible(false);
//						} else {
//							numSeeds.setVisible(true);
//							numSeedsLabel.setVisible(true);
//						}
//					}
//				}
//				);
			
			numSeeds = new JTextField("10");
			numSeedsLabel = new JLabel("Number of Random Seeds");
			
			searchLimit = new JTextField("20");
			searchLimitLabel = new JLabel("Search Limit");
			
			initTemp = new JTextField("1.8");
			initTempLabel = new JLabel("Initial Temperature");
			
			tempScalingFactor = new JTextField("0.88");
			tempScalingFactorLabel = new JLabel("Temperature Scaling Factor");
			
			overlapLimit = new JTextField("0.75");
			overlapLimitLabel = new JLabel("Overlap Limit");
			
			minScoreThreshold = new JTextField("-4E2");
			minScoreThresholdLabel = new JLabel("Minimum Complex Score");
			
			minSize = new JTextField("3");
			minSizeLabel = new JLabel("Minimum Complex Size");
			
			
			useSelectedForSeedsButton = new JButton("Select Seed File");
			
			useSelectedForSeedsLabel = new JLabel("Use Starting Seeds From File");
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
			
			layout.setHorizontalGroup(
					layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(chooserLabel)
								.addComponent(checkNumNeighborsLabel)
								.addComponent(useSelectedForSeedsLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(chooser)
								.addComponent(checkNumNeighbors)
								.addComponent(useSelectedForSeedsButton))
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
							.addComponent(useSelectedForSeedsButton))
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
			
			trainNewModel = new JCheckBox();
			trainNewModelLabel = new JLabel("Train New Model");
			trainNewModel.setSelected(true);
			trainNewModel.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(trainNewModel.isEnabled() && trainNewModel.isSelected()) {
								existingModel.setVisible(false);
								existingModelLabel.setVisible(false);		
								customModel.setVisible(true);
								customModelLabel.setVisible(true);
								if (customModel.isSelected()) {
									bayesModel.setVisible(true);
									bayesModelLabel.setVisible(true);
								}
								
								weightName.setVisible(true);
								weightNameLabel.setVisible(true);
								clusterPrior.setVisible(true);
								clusterPriorLabel.setVisible(true);
								negativeExamples.setVisible(true);
								negativeExamplesLabel.setVisible(true);
								trainingFileButton.setVisible(true);
								trainingFileLabel.setVisible(true);
								ignoreMissing.setVisible(true);
								ignoreMissingLabel.setVisible(true);
							} else {
								existingModel.setVisible(true);
								existingModelLabel.setVisible(true);
								customModel.setVisible(false);
								customModelLabel.setVisible(false);
								bayesModel.setVisible(false);
								bayesModelLabel.setVisible(false);
								
								weightName.setVisible(false);
								weightNameLabel.setVisible(false);
								clusterPrior.setVisible(false);
								clusterPriorLabel.setVisible(false);
								negativeExamples.setVisible(false);
								negativeExamplesLabel.setVisible(false);
								trainingFileButton.setVisible(false);
								trainingFileLabel.setVisible(false);
								ignoreMissing.setVisible(false);
								ignoreMissingLabel.setVisible(false);
							}
						}
					}
					);
			
			
			existingModel = new JComboBox(getNetworkNames().toArray());
			existingModelLabel = new JLabel("Use Trained Model");
			existingModel.setVisible(false);
			existingModelLabel.setVisible(false);
			
			customModel = new JCheckBox();
			customModelLabel = new JLabel("Use Custom Bayesian Network");
			customModel.addActionListener(
					new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(customModel.isEnabled() && customModel.isSelected()) {
									bayesModel.setVisible(true);
									bayesModelLabel.setVisible(true);
							} else {
								bayesModel.setVisible(false);
								bayesModelLabel.setVisible(false);
							}
						}
					}
					);
			
			
			
			bayesModel = new JComboBox(getNetworkNames().toArray());
			bayesModelLabel = new JLabel("Custom Bayesian Network");
			bayesModel.setVisible(false);
			bayesModelLabel.setVisible(false);
			
			weightName = new JComboBox(getEdgeColumnNames().toArray());
			weightNameLabel = new JLabel("Edge Weight Column");
			
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
	                }
	            }
	        }); 
			
			ignoreMissing = new JCheckBox();
			ignoreMissingLabel = new JLabel("Ignore Missing Nodes");
			
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
								.addComponent(trainingFileLabel)
								.addComponent(resultFileLabel))
						
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(trainNewModel)
								.addComponent(existingModel)
								.addComponent(customModel)
								.addComponent(bayesModel)
								.addComponent(weightName)
								.addComponent(trainingFileButton)
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
							.addComponent(trainingFileLabel)
							.addComponent(trainingFileButton))
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
							.addComponent(resultFileLabel)
							.addComponent(resultFileButton))
			);
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
			
			TitledBorder eval = BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.gray), "Evaluate Resuls");
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
			evaluateButton.setVisible(true);
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
			String complexes_string = l[2];
			HashSet eval_complexes = new HashSet(Arrays.asList(complexes_string.split(" ")));
			evalComplexes.add(eval_complexes);
		}
		
		System.out.println("");
		System.out.println("");
	
		int countPredicted = 0;
		int countKnown = 0;
		
		for (int i = 0; i < resultComplexes.size(); i++) {
			Set<String> predicted = resultComplexes.get(i);
			Boolean predictedAlreadyMatched = false;
			for(int j = 0; j < evalComplexes.size(); j++) {
				int A = 0; 
				int B = 0; 
				int C = 0; 
				Set<String> known = evalComplexes.get(i);
				
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
		
		boolean inputTrainNewModel = trainNewModel.isSelected();
		inputTask.trainNewModel = inputTrainNewModel;
		
		ListSingleSelection<String> inputExistingModel = new ListSingleSelection<String>(getNetworkNames());
		inputExistingModel.setSelectedValue(existingModel.getSelectedItem().toString());
		inputTask.existingModel = inputExistingModel;
		
		boolean inputCustomModel = customModel.isSelected();
		inputTask.customModel = inputCustomModel;
		
		ListSingleSelection<String> inputBayesModel = new ListSingleSelection<String>(getNetworkNames());
		inputBayesModel.setSelectedValue(bayesModel.getSelectedItem().toString());
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
	
}