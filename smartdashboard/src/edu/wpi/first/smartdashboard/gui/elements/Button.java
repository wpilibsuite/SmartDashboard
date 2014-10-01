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
public class Button extends AbstractTableWidget {

	public static final DataType[] TYPES = {ButtonType.get()};

	@Override
	public void init() {
		JButton start = new JButton(getFieldName());

		start.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				table.putBoolean("pressed", true);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				table.putBoolean("pressed", false);
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
	}
}
