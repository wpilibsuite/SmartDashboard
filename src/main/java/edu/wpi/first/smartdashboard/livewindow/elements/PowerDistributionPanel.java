package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.PowerDistributionPanelType;
import edu.wpi.first.wpilibj.tables.ITableListener;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;


/**
 * Displays the current and voltage from the PDP
 */
public class PowerDistributionPanel extends AbstractTableWidget implements ITableListener {

  public static final DataType[] TYPES = {PowerDistributionPanelType.get()};

  private final UneditableNumberField voltage = new UneditableNumberField();
  private final UneditableNumberField totCurrent = new UneditableNumberField();
  private final UneditableNumberField[] current = new UneditableNumberField[16];
  private final JLabel[] curLabel = new JLabel[16];
  private JLabel totCurLabel;
  private JLabel voltageLabel;

  /**
   * @inheritdoc
   */
  public void init() {
    setLayout(new GridBagLayout());
    GridBagConstraints c = new GridBagConstraints();

    nameTag = new NameTag(getFieldName());
    add(nameTag);
    for (int i = 0; i < 8; i++) {
      c.gridx = 0;
      c.gridy = i + 1;
      curLabel[i] = new JLabel("Chan" + i);
      curLabel[i].setHorizontalAlignment(JLabel.RIGHT);
      add(curLabel[i], c);
      c.gridx = 1;
      current[i] = new UneditableNumberField();
      setNumberBinding("Chan" + i, current[i]);
      current[i].setColumns(6);
      add(current[i], c);
    }
    for (int i = 8; i < 16; i++) {
      c.gridx = 2;
      c.gridy = 16 - i;
      curLabel[i] = new JLabel("Chan" + i);
      curLabel[i].setHorizontalAlignment(JLabel.RIGHT);
      add(curLabel[i], c);
      c.gridx = 3;
      current[i] = new UneditableNumberField();
      setNumberBinding("Chan" + i, current[i]);
      current[i].setColumns(6);
      add(current[i], c);
    }

    c.gridy = 9;
    c.gridx = 0;
    voltageLabel = new JLabel("Voltage");
    add(voltageLabel, c);
    c.gridx = 1;
    voltage.setFocusable(false);
    setNumberBinding("Voltage", voltage);
    voltage.setColumns(5);
    add(voltage, c);

    c.gridx = 2;
    totCurLabel = new JLabel("TotalCurrent");
    add(totCurLabel, c);
    c.gridx = 3;
    setNumberBinding("TotalCurrent", totCurrent);
    totCurrent.setColumns(7);
    add(totCurrent, c);

    setMaximumSize(new Dimension(Integer.MAX_VALUE, getPreferredSize().height));

    revalidate();
    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
  }

}
