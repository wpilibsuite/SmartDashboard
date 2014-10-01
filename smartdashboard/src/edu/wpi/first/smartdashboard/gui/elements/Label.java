package edu.wpi.first.smartdashboard.gui.elements;

import java.awt.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;

/**
 *
 * @author Joe Grinstead
 */
public class Label extends StaticWidget {

    public final StringProperty text = new StringProperty(this, "Text", "Label");
    public final MultiProperty horizontal = new MultiProperty(this, "Horizontal Alignment");
    public final MultiProperty vertical = new MultiProperty(this, "Vertical Alignment");

    private JLabel label;

    public Label() {
        horizontal.add("Left", SwingConstants.LEFT);
        horizontal.add("Center", SwingConstants.CENTER);
        horizontal.add("Right", SwingConstants.RIGHT);
        horizontal.setDefault("Center");

        vertical.add("Up", SwingConstants.TOP);
        vertical.add("Center", SwingConstants.CENTER);
        vertical.add("Down", SwingConstants.BOTTOM);
        vertical.setDefault("Center");
    }

    @Override
    public void init() {
        setLayout(new BorderLayout());

        label = new JLabel(text.getValue());

        label.setHorizontalAlignment((Integer) horizontal.getValue());
        label.setVerticalAlignment((Integer) vertical.getValue());

        add(label, BorderLayout.CENTER);
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == text) {
            label.setText(text.getValue());
        } else if (property == horizontal) {
            label.setHorizontalAlignment((Integer) horizontal.getValue());
        } else if (property == vertical) {
            label.setVerticalAlignment((Integer) vertical.getValue());
        }
    }
}
