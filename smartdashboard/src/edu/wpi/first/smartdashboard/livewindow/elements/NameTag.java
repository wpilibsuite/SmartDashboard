/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JLabel;

/**
 * A custom JLabel implementation that's used for titles
 * of individual components within a subsystem.
 * @author Sam
 * @see AbstractTableWidget#nameTag
 */
public class NameTag extends JLabel {
    
    public NameTag(String text) {
        super(text);
        setFont(Font.decode("Arial-BOLD-12"));
        setForeground(new Color(0, 0, 125));
    }
    
}
