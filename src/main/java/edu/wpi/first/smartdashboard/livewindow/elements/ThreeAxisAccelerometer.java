package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.ThreeAxisAccelerometerType;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;


/**
 * Displays the X, Y and Z accelerations from a 3 axis accelerometer
 */
public class ThreeAxisAccelerometer extends AbstractTableWidget implements ITableListener {

  public static final DataType[] TYPES = {ThreeAxisAccelerometerType.get()};

  private final UneditableNumberField x = new UneditableNumberField();
  private final UneditableNumberField y = new UneditableNumberField();
  private final UneditableNumberField z = new UneditableNumberField();
  private JLabel xLabel;
  private JLabel yLabel;
  private JLabel zLabel;

  /**
   * @inheritdoc
   */
  public void init() {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    nameTag = new NameTag(getFieldName());
    add(nameTag);

    c.gridy = 1;
    c.gridx = 0;
    xLabel = new JLabel("X");
    add(xLabel, c);
    c.gridx = 1;
    x.setFocusable(false);
    setNumberBinding("X", x);
    x.setColumns(10);
    add(x, c);

    c.gridy = 2;
    c.gridx = 0;
    yLabel = new JLabel("Y");
    add(yLabel, c);
    c.gridx = 1;
    y.setFocusable(false);
    setNumberBinding("Y", y);
    y.setColumns(10);
    add(y, c);

    c.gridy = 3;
    c.gridx = 0;
    zLabel = new JLabel("Z");
    add(zLabel, c);
    c.gridx = 1;
    z.setFocusable(false);
    setNumberBinding("Z", z);
    z.setColumns(10);
    add(z, c);

    setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

    revalidate();
    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
  }

}
