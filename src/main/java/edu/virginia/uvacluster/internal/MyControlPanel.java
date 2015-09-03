package edu.virginia.uvacluster.internal;

import java.awt.Component;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.work.util.ListSingleSelection;

import javax.swing.JLabel;

public class MyControlPanel extends JPanel implements CytoPanelComponent {
	
	private static final long serialVersionUID = 8292806967891823933L;

	public MyControlPanel() {
		//Create GroupLayout to organize components
		GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		String[] variants = { "Greedy ISA", "M ISA", "ISA" };
		JComboBox chooser = new JComboBox(variants);
		layout.setHorizontalGroup(
			layout.createSequentialGroup().addComponent(chooser)
				);
	
		
		JTextField checkNumNeighbors = new JTextField(20);
		//this.add(checkNumNeighbors);
		JLabel cNN = new JLabel("Neighbors to consider");
		
		JCheckBox useSelectedForSeeds = new JCheckBox("Use the selected nodes for seeds?");
		useSelectedForSeeds.setSelected(false);
		JLabel uSFS = new JLabel("Use Selected Nodes For Seeds?");
		//useSelectedForSeeds.addItemListener((ItemListener) this);
		//this.add(useSelectedForSeeds);
		
		JTextField numSeeds = new JTextField(20);
		JLabel nS = new JLabel("Number of Seeds");
		//this.add(numSeeds);
		
		JTextField searchLimit = new JTextField(20);
		JLabel sL = new JLabel("Search Limit");
		//this.add(searchLimit);
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(checkNumNeighbors)
				.addComponent(useSelectedForSeeds)
				.addComponent(numSeeds)
				.addComponent(searchLimit)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(cNN)
						.addComponent(uSFS)
						.addComponent(nS)
						.addComponent(sL))
				);
		
		this.setVisible(true);
		
	}


	public Component getComponent() {
		return this;
	}


	public CytoPanelName getCytoPanelName() {
		return CytoPanelName.WEST;
	}


	public String getTitle() {
		return "SCODE - Analyze Network";
	}


	public Icon getIcon() {
		return null;
	}
}