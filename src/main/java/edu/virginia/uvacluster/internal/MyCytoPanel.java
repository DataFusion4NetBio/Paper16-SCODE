package edu.virginia.uvacluster.internal;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

// Define a CytoPanel class
public class MyCytoPanel extends JPanel implements CytoPanelComponent {
    @Override
    public CytoPanelName getCytoPanelName() {
        return CytoPanelName.WEST;
    }

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}
