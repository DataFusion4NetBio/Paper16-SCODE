##Supervised Complex Detection

###SCODE: A Cytoscape App for Supervised Complex Detection in PPI networks 
  Available for free download online: http://apps.cytoscape.org/apps/scode
  User Manual : https://github.com/DataFusion4NetBio/Paper16-SCODE/blob/master/Demo/SCODEUserManual.pdf

****
This [Cytoscape](http://cytoscape.org/) [app](http://apps.cytoscape.org/) allows users to search for complexes in weighted graphs using supervised learning on a Bayesian network model. It is based on the algorithm developed by Qi et al in [this paper](http://www.cs.cmu.edu/~qyj/SuperComplex/)


###Reference
Y. Qi, F. Balem, C. Faloutsos, J. Klein-Seetharaman, Z. Bar-Joseph (2008). Protein Complex Identification by Supervised Graph Clustering , Bioinformatics 2008, 24(13), i250-i268.  
The 16th Annual International Conference Intelligent Systems for Molecular Biology (ISMB), July 2008  
(Impact Factor 4.328)   
(Acceptance Rate of ISMB08: 17% = 49/292)   
  
###Usage

****
A detailed User Manual can be found [here](https://github.com/DataFusion4NetBio/Paper16-SCODE/blob/master/Demo/SCODEUserManual.pdf)
****
####Installation
Supervised Complex Detection (SCODE) is available through the Cytoscape App Store [here](http://apps.cytoscape.org/apps/scode).  
To install, either  
  1. Open Cytoscape on your machine, and navigate to Apps > App Manager from the menu bar. Search for 'SCODE' and click Install.
  2. Download the .jar file from the [Cytoscape App Store website](http://apps.cytoscape.org/apps/scode). Then move the jar file into your ~/Cytoscape/installed/ directory.

####Basics
To use this app:
  1. Load the graph on which you would like to search for complexes.
  2. Go to Apps > SCODE > Open SCODE. The application will open in the left panel.
  3. Customize the search and scoring parameters. 
    3a. If scoring using only edge weight information, select the "no learning" option, or 
    3b. If scoring using a Bayesian network, load positive training file with which to train the Bayesian template (see below for formatting requirements), or select the trained Bayesian network
  5. Click Analyze.
  6. Results are stored as subnetworks of the target network in your session file, complete with a likelihood score. If you select the learning scoring option, the app will also generate a new 'Model' network that represents a trained model, which you can re-use with future searches.
  7. Optional: Evaluate your results using a testing file of all known protein complexes in the network


***

#####Searching a PPI Graph

Currently, SCODE supports an [iterative simulated annealing search](http://en.wikipedia.org/wiki/Simulated_annealing) for finding candidate complexes within a dataset.  This search comes in three flavors:
* ISA: This is the fastest option and will perform the worst.  Each round, a candidate is expanded (or not) using a single, random neighboring node.  
* M-ISA: This a slower option that will perform better than ISA.  Each round, a candidate is expanded by testing the M highest degree neighboring nodes.  The best of these M nodes is used for expansion.
* Greedy-ISA:  This option, the slowest, tests all the neighboring nodes for expansion and selects the best one.  This will result in more, larger, higher-scoring candidate complexes.

SCODE allows you to specify several additional search parameters to define the scope of the search (temperature, scaling ratio, etc). For an explanation of each of these parameters, see the more detailed [User Manual](Demo/SCODEUserManual.pdf)

#####Scoring Candidate Complexes
There are three options for scoring candidate complexes: 
  1. Score using only edge weights (no learning)
  2. Train a Bayesian template with positive complex exemplars
  3. Score using a trained Bayesian model

Option 1 does not require any additional input information; the average edge weight among the members of a candidate complex will be used to calculate its score. 
Options 2 and 3 are described in more detail below.

#####Training a Bayesian Template
The type of model used by SCODE is a [Bayesian network](http://en.wikipedia.org/wiki/Bayesian_network).  Bayesian networks are probabilistic graphical models that make it easy to define relationships between supposed features of complexes.  Each node in a Bayesian network represents a feature (e.g. Number of nodes in a complex).  Each edge between nodes represents a dependency between features or conditioning of one feature by another (e.g. the complex's density given the number of nodes in the complex).  The values of each feature are discretized before training or scoring a candidate complex.

SCODE provides several options for creating or loading a model. You may:
  1. Use the default, un-trained model provided by SCODE - the default model is based on [the paper by Qi et al](http://www.cs.cmu.edu/~qyj/SuperComplex/). You must provide training data (positive complex exemplars),
  2. Load a custom, un-trained model you have created as a network in Cytoscape. You must provide training data (positive complex exemplars), or
  3. Load a trained model stored as a network in Cytoscape

######Training Data
SCODE uses a supervised learning model to find candidate complexes with similar properties to known training complexes. Users provide these training complexes as examples to the model.  Training data should be stored according to the following format:

Each row represents a known complex, with tab-separated columns as follows:  
* Column 1: Numerical identifier for the complex  
* Column 2: Name identifier for the complex  
* Column 3: Space-separated names of the protein members of the complex, as they appear in the input PPI graph.  

***

###Notes on Development
Some key files:
* CyActivator registers the app with Cytoscsape.
* The Bayesian network implementation is located in the Graph class.
* SeedSearch contains the core implementation of the search functions.
* Use [these](http://wiki.cytoscape.org/Cytoscape_3/AppDeveloper) instructions to setup your environment for Cytoscape development.
