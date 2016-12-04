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
 * @author Joe Grinstead
 */
public class CommandButton extends AbstractTableWidget {

	public static final DataType[] TYPES = {CommandType.get()};

	@Override
	public void init() {
		JButton start = new JButton(getFieldName());
		start.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				table.putBoolean("running", true);
			}
		});

		start.setFocusable(false);

		setLayout(new BorderLayout());

		add(start, BorderLayout.CENTER);
		
		revalidate();
		repaint();
	}

	@Override
	public void propertyChanged(Property property) {
		//no properties
	}
}
