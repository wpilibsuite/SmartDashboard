/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.gui;

import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JPanel;

/**
 * Contains the DashboardPanels of the SmartDashboard and
 * gives functionality to swap between them.
 * @author Sam
 */
public final class MainPanel extends JPanel {
    
    public static final HashMap<String, DashboardPanel> panels = new HashMap();
    private static DashboardPanel currentPanel;
    
    public MainPanel(LayoutManager layout, DashboardPanel defaultPanel, DashboardPanel... panels) {
        super(layout);
        for(DashboardPanel panel : panels)
            MainPanel.panels.put(panel.getName(), panel);
        currentPanel = defaultPanel;
    }
    
    public static DashboardPanel getCurrentPanel() {
        return currentPanel;
    }
    
    public static DashboardPanel getPanel(String name) {
        return panels.get(name);
    }
    
    public void setCurrentPanel(DashboardPanel panel) {
        if(panels.containsValue(panel))
            currentPanel = panel;
        else throw new IllegalArgumentException("Not a valid panel");
    }
    
    public void addPanel(String name, DashboardPanel panel) {
        if(!panels.containsValue(panel))
            panels.put(name, panel);
        else throw new IllegalArgumentException("That panel already exists");
    }
    
}
