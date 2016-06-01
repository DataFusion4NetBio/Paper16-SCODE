##Supervised Complex Detection

###SCODE: A Cytoscape App for Supervised Complex Detection in PPI networks 
  Available for free download online: http://apps.cytoscape.org/apps/scode


****
This [Cytoscape](http://cytoscape.org/) [app](http://apps.cytoscape.org/) allows users to search for complexes in weighted graphs using a supervised model. It is based on the algorithm developed by Qi et al in [this paper](http://www.cs.cmu.edu/~qyj/SuperComplex/)


###Reference
Y. Qi, F. Balem, C. Faloutsos, J. Klein-Seetharaman, Z. Bar-Joseph (2008). Protein Complex Identification by Supervised Graph Clustering , Bioinformatics 2008, 24(13), i250-i268.  
The 16th Annual International Conference Intelligent Systems for Molecular Biology (ISMB), July 2008  
(Impact Factor 4.328)   
(acceptance rate of ISMB08: 17% = 49/292)   
  
###Usage
####Installation
Supervised Complex Detection (SCODE) is available through the Cytoscape App Store [here](http://apps.cytoscape.org/apps/scode).  
To install, either  
  1. Open Cytoscape on your machine, and navigate to Apps > App Manager from the menu bar. Search for 'SCODE' and click Install.
  2. Download the .jar file from the [Cytoscape App Store website](http://apps.cytoscape.org/apps/scode). Then move the jar file into your ~/Cytoscape/3/apps/installed/ directory.

####Basics
To use this app:
  1. Load the graph on which you would like to search for complexes.
  2. Go to Apps > SCODE > Open SCODE. The application will open in the left panel.
  3. Customize the search and training parameters. 
  4. Load training data (see below for formatting requirements).
  5. Click Analyze.
  6. Results are stored as subnetworks of the target network in your session file, complete with a likelihood score.  The app will also generate a new 'Model' network that represents a trained model, which you can re-use with future searches.
***

#####Model
The type of model used by SCODE is a [Bayesian network](http://en.wikipedia.org/wiki/Bayesian_network).  Bayesian networks are probabilistic graphical models that make it easy to define relationships between supposed features of complexes.  Each node in a Bayesian network represents a feature (e.g. Number of nodes in a complex).  Each edge between nodes represents a dependency between features or conditioning of one feature by another (e.g. the complex's density given the number of nodes in the complex).  The values of each feature are discretized before training or scoring a candidate complex.

SCODE provides several options for creating or loading a model. You may:
  1. Use the default, un-trained model provided by SCODE - the default model is based on [the paper by Qi et al](http://www.cs.cmu.edu/~qyj/SuperComplex/). You must provide training data (positive complex exemplars),
  2. Load a custom, un-trained model you have created as a network in Cytoscape. You must provide training data (positive complex exemplars), or
  3. Load a trained model stored as a network in Cytoscape

######Creating Custom Bayesian Networks
To create a custom Bayesian network, start with an empty Cytoscape Network.  Your graph must contain a node labeled "Root", which represents classification of candidate complexes (complex/non-complex).  All nodes must be connected by directed edges.  Those edges must not form cycles.

A node's name will determine the feature that it represents.  For example 'Count: Node (3)' represents the number of nodes in a complex, with 3 possible bins for feature values.  Descretization/binning is based on the range of a feature's training values.  If the model is trained on complexes composed of 3-11 nodes, bin 1 would account for complexes of 3-5 nodes, bin 2 for complexes of 6-8 nodes, and bin 3 for complexes of 9-11 nodes.

The general syntax for features is [<Statistic>] : <Feature>[{args}] (Bins).  Statistics are used to transform the values returned by a feature, which are generally calculated per node.
*Note that this syntax is case insensitive.  All features/statistics may be entered as is, unless specific examples are given.*
* **Statistic** is optional, as the Density feature is a single value and so does not need a statistic. Available statistics include: 
  * Mean
  * Median
  * Max
  * Variance
  * Count
  * Ordinals (e.g. 1st, 2nd, 3rd)
* Each **Feature** is calculated for complexes during training and candidates during search.  Each node must correspond to a feature.  Available features include:
  * Cluster Coefficient
  * Degree
  * Degree Correlation
  * Density
  * Density at cutoff N (e.g. Density at cutoff 1.2)
  * Edge Table Feature (e.g. edge{ColumnName}) *The specified column must contain numeric values.*
  * Edge Table Correlation Feature (e.g. edge{ColumnName,ColumnName,...}) *The specified columns must contain numeric values.*
  * Node
  * Node Table Feature (e.g. node{ColumnName}) *The specified column must contain numeric values.*
  * Node Table Correlation Feature (e.g. node{ColumnName,ColumnName,...}) *The specified columns must contain numeric values.*
  * Singular Value
  * Topological Coefficient

***

#####Training Data
SCODE uses a supervised learning model to find candidate complexes with similar properties to known training complexes. Users provide these training complexes as examples to the model.  Training data should be stored in tab seperated values files, where each row represents a known cluster with an arbitrary number of nodes.  Each value should be the name of a protein as it appears in the graph you are searching, under the 'name' column.  The first two columns should be a numerical id for the complex and a name, respectively.
***

#####Search
*For inforamation on parameters, check out the tooltips!*
Currently, SCODE supports an [iterative simulated annealing search](http://en.wikipedia.org/wiki/Simulated_annealing) for finding candidate complexes within a dataset.  This search comes in three flavors:
* ISA: This is the fastest option and will perform the worst.  Each round, a candidate is expanded (or not) using a single, random neighboring node.  
* M-ISA: This a slower option that will perform better than ISA.  Each round, a candidate is expanded by testing the M highest degree neighboring nodes.  The best of these M nodes is used for expansion.
* Greedy-ISA:  This option, the slowest, tests all the neighboring nodes for expansion and selects the best one.  This will result in more, larger, higher-scoring candidate complexes.

***
###Notes on Development
Some key files:
* CyActivator registers the app with Cytoscsape.
* The Bayesian network implementation is located in the Graph class.
* SeedSearch contains the core implementation of the search functions.
* Use [these](http://wiki.cytoscape.org/Cytoscape_3/AppDeveloper) instructions to setup your environment for Cytoscape development.
