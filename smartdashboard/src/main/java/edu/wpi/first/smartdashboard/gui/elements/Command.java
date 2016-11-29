package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.smartdashboard.types.named.*;
import edu.wpi.first.wpilibj.tables.*;

/**
 *
 * @author Jeff Copeland
 */
public class Command extends AbstractTableWidget {

    public static final DataType[] TYPES = {CommandType.get()};
    private static final String START_CARD = "Start";
    private static final String CANCEL_CARD = "Cancel";
    public final ColorProperty startBackground = new ColorProperty(this, "Start Button Color", new Color(32, 134, 32));
    public final ColorProperty cancelBackground = new ColorProperty(this, "Cancel Button Color", new Color(243, 32, 32));
    private JLabel name;
    private JPanel buttonPanel;
    private CardLayout layout;
    private JButton start;
    private JButton cancel;

    @Override
    public void init() {
        setResizable(false);

        buttonPanel = new JPanel(layout = new CardLayout());
        buttonPanel.setOpaque(false);

        start = new JButton("start");
        start.setOpaque(false);
        start.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                table.putBoolean("running", true);
            }
        });
        start.setForeground(startBackground.getValue());

        buttonPanel.add(start, START_CARD);

        cancel = new JButton("cancel");
        cancel.setOpaque(false);
        cancel.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                table.putBoolean("running", false);
            }
        });
        cancel.setForeground(cancelBackground.getValue());

        buttonPanel.add(cancel, CANCEL_CARD);

        name = new JLabel(getFieldName());
        add(name);
        add(buttonPanel);
    }
    
    @Override
    public void booleanChanged(ITable source, String key, final boolean value, boolean isNew) {
    	if(key.equals("running")){
    		SwingUtilities.invokeLater(new Runnable() {
    			public void run() {
    				if (value)
    					layout.show(buttonPanel, CANCEL_CARD);
    				else
    					layout.show(buttonPanel, START_CARD);
    				revalidate();
    				repaint();
    			}
    		});
    	}
    }
	
    @Override
    public void propertyChanged(Property property) {
        if (property == startBackground) {
            start.setBackground(startBackground.getValue());
        } else if (property == cancelBackground) {
            cancel.setBackground(cancelBackground.getValue());
        }
    }
}
