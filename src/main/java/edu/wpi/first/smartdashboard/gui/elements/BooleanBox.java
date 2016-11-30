package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractValueWidget;
import java.awt.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 * Implements a simple text box UI element with a name label.
 * @author pmalmsten
 */
public class BooleanBox extends AbstractValueWidget {

    public static final DataType[] TYPES = {DataType.BOOLEAN};
    public final ColorProperty colorOnTrue = new ColorProperty(this, "Color to show when true", Color.GREEN);
    public final ColorProperty colorOnFalse = new ColorProperty(this, "Color to show when false", Color.RED);
    private JPanel valueField;
    private boolean value;

    public void init() {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JLabel nameLabel = new JLabel(getFieldName());
        valueField = new JPanel();
        valueField.setPreferredSize(new Dimension(10, 10));

        add(valueField);
        add(nameLabel);
        revalidate();
        repaint();
    }

    @Override
    public void setValue(final boolean value) {
    	this.value = value;
        valueField.setBackground(value ? colorOnTrue.getValue() : colorOnFalse.getValue());
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
    	setValue(value);//force backgound change
    }
}
