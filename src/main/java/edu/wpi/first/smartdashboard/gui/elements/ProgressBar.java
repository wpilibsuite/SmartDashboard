package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractValueWidget;
import java.awt.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;

/**
 *
 * @author brad (Heavily modified by Alex Henning)
 */
public class ProgressBar extends AbstractValueWidget {
    public static final DataType[] TYPES = {DataType.NUMBER};

    public final ColorProperty foreground = new ColorProperty(this, "Foreground");
    public final ColorProperty background = new ColorProperty(this, "Background");
    public final DoubleProperty max = new DoubleProperty(this, "Maximum", 100);
    public final DoubleProperty min = new DoubleProperty(this, "Minimum", 0);

    private NumberProgressBar progressBar;

    @Override
    public void init() {
        progressBar = new NumberProgressBar();
        progressBar.setMin(min.getValue());
        progressBar.setMax(max.getValue());
        progressBar.setBorderPainted(false);
        progressBar.setBounds(progressBar.getX(), progressBar.getY(),
                progressBar.getX() + 200, progressBar.getY() + 40);
        setNumberBinding(progressBar);

        setLayout(new BorderLayout());
        add(new JLabel(getFieldName()), BorderLayout.PAGE_START);
        add(progressBar, BorderLayout.CENTER);


        update(foreground, progressBar.getForeground());
        update(background, progressBar.getBackground());
        
        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == foreground) {
            progressBar.setForeground(foreground.getValue());
        } else if (property == background) {
            progressBar.setBackground(background.getValue());
        } else if (property == min) {
        	progressBar.setMin(min.getValue());
        } else if (property == max) {
        	progressBar.setMax(max.getValue());
        }
    }
}
