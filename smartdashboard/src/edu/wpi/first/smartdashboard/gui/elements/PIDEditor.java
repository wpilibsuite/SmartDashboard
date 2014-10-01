package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.livewindow.elements.NameTag;
import java.awt.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.smartdashboard.types.named.*;

/**
 *
 * @author Joe Grinstead
 */
public class PIDEditor extends AbstractTableWidget {

	public static final DataType[] TYPES = {PIDType.get()};

	private NumberTableField pField;
	private NumberTableField iField;
	private NumberTableField dField;
	private NumberTableField fField;
	private NumberTableField sField;
	private BooleanTableCheckBox eBox;
	private JLabel pLabel;
	private JLabel iLabel;
	private JLabel dLabel;
	private JLabel fLabel;
	private JLabel sLabel;
	private JLabel eLabel;
	
	public PIDEditor() {//TODO alert user when the robot is about reset modified PID values
		setLayout(new GridBagLayout());

		pLabel = new JLabel("P:");
		iLabel = new JLabel("I:");
		dLabel = new JLabel("D:");
		fLabel = new JLabel("F:");
		sLabel = new JLabel("Setpoint:");
		eLabel = new JLabel("Enabled:");
		pLabel.setHorizontalAlignment(JLabel.RIGHT);
		iLabel.setHorizontalAlignment(JLabel.RIGHT);
		dLabel.setHorizontalAlignment(JLabel.RIGHT);
		fLabel.setHorizontalAlignment(JLabel.RIGHT);
		sLabel.setHorizontalAlignment(JLabel.RIGHT);
		eLabel.setHorizontalAlignment(JLabel.RIGHT);

		pField = new NumberTableField("p");
		iField = new NumberTableField("i");
		dField = new NumberTableField("d");
		fField = new NumberTableField("f");
		sField = new NumberTableField("setpoint");
		eBox = new BooleanTableCheckBox("enabled");

		int columns = 10;
		pField.setColumns(columns);
		iField.setColumns(columns);
		dField.setColumns(columns);
		fField.setColumns(columns);
		sField.setColumns(columns);

		
		GridBagConstraints c = new GridBagConstraints();
		
        
		c.gridy = 1;
		add(pLabel, c);
		c.gridy = 2;
		add(iLabel, c);
		c.gridy = 3;
		add(dLabel, c);
		c.gridy = 4;
		add(fLabel, c);
		c.gridy = 5;
		add(sLabel, c);
		c.gridy = 6;
		add(eLabel, c);
		
		c.gridx = 1;
		c.weightx = 1.0;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
        add(nameTag = new NameTag(""), c);
		nameTag.setHorizontalAlignment(JLabel.LEFT);
		c.gridy = 1;
		add(pField, c);
		c.gridy = 2;
		add(iField, c);
		c.gridy = 3;
		add(dField, c);
		c.gridy = 4;
		add(fField, c);
		c.gridy = 5;
		add(sField, c);
		c.gridy = 6;
		add(eBox, c);

		setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

		revalidate();
		repaint();
	}
	
	@Override
	public void init(){
		nameTag.setText(getFieldName());
	}

	@Override
	public void propertyChanged(Property property) {
	}
}
