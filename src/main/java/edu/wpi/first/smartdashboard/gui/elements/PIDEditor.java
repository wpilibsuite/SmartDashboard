package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.livewindow.elements.Controller;
import edu.wpi.first.smartdashboard.livewindow.elements.NameTag;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.PIDType;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;

/**
 * @author Joe Grinstead
 */
public class PIDEditor extends AbstractTableWidget implements Controller {

  public static final DataType[] TYPES = {PIDType.get()};

  private final boolean editType;

  private NumberTableComboBox tBox;
  private NumberTableField pField;
  private NumberTableField iField;
  private NumberTableField dField;
  private NumberTableField sField;
  private JLabel tLabel;
  private JLabel pLabel;
  private JLabel iLabel;
  private JLabel dLabel;
  private JLabel sLabel;

  public PIDEditor() {
    this(true);
  }

  public PIDEditor(boolean editType) { //TODO alert user when the robot is about reset modified
    // PID values
    this.editType = editType;
  }

  @Override
  public void init() {
    setLayout(new GridBagLayout());

    pLabel = new JLabel("P:");
    iLabel = new JLabel("I:");
    dLabel = new JLabel("D:");
    sLabel = new JLabel("Setpoint:");
    pLabel.setHorizontalAlignment(JLabel.RIGHT);
    iLabel.setHorizontalAlignment(JLabel.RIGHT);
    dLabel.setHorizontalAlignment(JLabel.RIGHT);
    sLabel.setHorizontalAlignment(JLabel.RIGHT);
    pField = new NumberTableField("p");
    iField = new NumberTableField("i");
    dField = new NumberTableField("d");
    sField = new NumberTableField("setpoint");

    int columns = 10;
    pField.setColumns(columns);
    iField.setColumns(columns);
    dField.setColumns(columns);
    sField.setColumns(columns);

    GridBagConstraints c = new GridBagConstraints();


    c.gridy = 1;
    add(pLabel, c);
    c.gridy = 2;
    add(iLabel, c);
    c.gridy = 3;
    add(dLabel, c);
    c.gridy = 4;
    add(sLabel, c);

    c.gridx = 1;
    c.weightx = 1.0;
    c.gridy = 0;
    c.fill = GridBagConstraints.HORIZONTAL;
    add(nameTag = new NameTag(""), c);
    nameTag.setHorizontalAlignment(JLabel.LEFT);
    nameTag.setText(getFieldName());
    c.gridy = 1;
    add(pField, c);
    c.gridy = 2;
    add(iField, c);
    c.gridy = 3;
    add(dField, c);
    c.gridy = 4;
    add(sField, c);

    setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

    revalidate();
    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
  }

  @Override
  public void reset() {
  }
}
