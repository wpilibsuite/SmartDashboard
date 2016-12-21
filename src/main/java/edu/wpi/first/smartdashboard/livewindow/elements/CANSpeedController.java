package edu.wpi.first.smartdashboard.livewindow.elements;

import edu.wpi.first.smartdashboard.gui.elements.PIDEditor;
import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.CANSpeedControllerType;
import edu.wpi.first.wpilibj.tables.ITable;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JPanel;

/**
 * @author Sam Carlberg
 */
public class CANSpeedController extends AbstractTableWidget implements Controller {

  public static final DataType[] TYPES = {CANSpeedControllerType.get()};

  private static final String TYPE_TALON = "CANTalon";
  private static final String TYPE_JAGUAR = "CANJaguar";

  /**
   * The type of the CAN controller.
   */
  private String type = "";
  /**
   * The mode the CAN controller is operating in.
   */
  private int mode = 0;

  private static final int TALON_DISABLED_MODE = 15;

  /**
   * The modes that the CAN Talon can run in.
   */
  private static final String[] talonModes
      = {"PercentVbus", "Position", "Speed", "Current", "Voltage", "Follower", "Disabled"};

  /**
   * The modes that the CAN Jaguar can run in.
   */
  private static final String[] jaguarModes
      = {"PercentVbus", "Current", "Speed", "Position", "Voltage"};

  private JComboBox<String> talonModeBox = new JComboBox<>(talonModes);
  private JComboBox<String> jaguarModeBox = new JComboBox<>(jaguarModes);
  private JComboBox<String> modeBox = talonModeBox;

  /**
   * Header panel containing the name tag and the mode combobox.
   */
  private JPanel headerPanel;

  /**
   * Control panel containing the PID editor / speed controller editor.
   */
  private JPanel controlPanel;
  private JPanel disabledControlPanel;
  private PIDEditor pidControlPanel;
  private SpeedController normalControlPanel;
  private static final String DISABLED_PANEL = "Disabled";
  private static final String PID_PANEL = "PID";
  private static final String NORMAL_PANEL = "Normal";

  private ActionListener modeSelection = e -> {
    int m = modeBox.getSelectedIndex();
    if (m == 6) {
      // CAN Talon mode #6 has a value of 15
      m = TALON_DISABLED_MODE;
    }
    normalControlPanel.reset();
    mode = m;
    table.putBoolean("Enabled",
        mode == 0 || mode == 4); // enable on %VBus and voltage, disable on all others
    table.putNumber("Mode", mode);
  };

  @Override
  public void init() {
    nameTag = new NameTag(getFieldName());
    headerPanel = new JPanel();
    controlPanel = new JPanel(new CardLayout());
    disabledControlPanel = new JPanel();
    createPIDControlPanel();
    createNormalControlPanel();
    controlPanel.add(disabledControlPanel, DISABLED_PANEL);
    controlPanel.add(normalControlPanel, NORMAL_PANEL);
    controlPanel.add(pidControlPanel, PID_PANEL);

    headerPanel.setLayout(new GridBagLayout());
    GridBagConstraints headerConstraints = new GridBagConstraints();
    headerConstraints.fill = GridBagConstraints.BOTH;
    headerConstraints.gridx = 0;
    headerConstraints.gridy = 0;
    headerPanel.add(nameTag, headerConstraints);
    headerConstraints.gridx++;
    headerPanel.add(modeBox, headerConstraints);

    setLayout(new GridBagLayout());
    GridBagConstraints controlConstraints = new GridBagConstraints();
    controlConstraints.gridx = 0;
    controlConstraints.gridy = 0;
    controlConstraints.fill = GridBagConstraints.BOTH;
    add(headerPanel, controlConstraints);
    controlConstraints.gridy++;
    add(controlPanel, controlConstraints);

    talonModeBox.addActionListener(modeSelection);
    jaguarModeBox.addActionListener(modeSelection);
  }

  private void createPIDControlPanel() {
    pidControlPanel = new PIDEditor(false);
    pidControlPanel.setValue(this.table);
    pidControlPanel.init();
  }

  private void createNormalControlPanel() {
    normalControlPanel = new SpeedController();
    normalControlPanel.setValue(this.table);
    normalControlPanel.init();
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    pidControlPanel.setValue(value);
    normalControlPanel.setValue(value);
    this.type = table.getString("Type", "[unknown]");
    this.mode = (int) table.getNumber("Mode", 0);

    headerPanel.remove(modeBox);
    switch (type) {
      case TYPE_TALON:
        modeBox = talonModeBox;
        break;
      case TYPE_JAGUAR:
        modeBox = jaguarModeBox;
        break;
    }
    headerPanel.add(modeBox);
    modeBox.setSelectedIndex(mode == TALON_DISABLED_MODE ? 6 : mode);
    setControlPanel();
  }

  @Override
  public void booleanChanged(ITable source, String key, boolean value, boolean isNew) {
    pidControlPanel.booleanChanged(source, key, value, isNew);
    normalControlPanel.booleanChanged(source, key, value, isNew);
  }

  @Override
  public void doubleChanged(ITable source, String key, double value, boolean isNew) {
    if ("Mode".equals(key)) {
      this.mode = (int) value;
      modeBox.setSelectedIndex(mode == TALON_DISABLED_MODE ? 6 : mode);
      setControlPanel();
      normalControlPanel.reset();
    }
    pidControlPanel.doubleChanged(source, key, value, isNew);
    normalControlPanel.doubleChanged(source, key, value, isNew);
  }

  @Override
  public void stringChanged(ITable source, String key, String value, boolean isNew) {
    if ("Type".equals(key)) {
      this.type = value;
    }
    pidControlPanel.stringChanged(source, key, value, isNew);
    normalControlPanel.stringChanged(source, key, value, isNew);
  }

  @Override
  public void valueChanged(ITable source, String key, Object value, boolean isNew) {
    super.valueChanged(source, key, value, isNew);
    pidControlPanel.valueChanged(source, key, value, isNew);
    normalControlPanel.valueChanged(source, key, value, isNew);
  }

  @Override
  public void propertyChanged(Property property) {
    pidControlPanel.propertyChanged(property);
    normalControlPanel.propertyChanged(property);
  }

  @Override
  public void reset() {
    normalControlPanel.reset();
    pidControlPanel.reset();
  }

  private void setControlPanel() {
    if (mode == TALON_DISABLED_MODE || mode == 5) {
      // empty (follower or disabled)
      normalControlPanel.reset();
      pidControlPanel.reset();
      ((CardLayout) controlPanel.getLayout()).show(controlPanel, DISABLED_PANEL);
    } else if (isPID()) {
      if (!pidControlPanel.isVisible()) {
        // set control to PID
        normalControlPanel.reset();
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, PID_PANEL);
      }
    } else {
      if (!normalControlPanel.isVisible()) {
        // set control to normal
        pidControlPanel.reset();
        ((CardLayout) controlPanel.getLayout()).show(controlPanel, NORMAL_PANEL);
      }
      // set the range on the slider depending on the mode
      switch (mode) {
        case 0: // Percent vbus
          normalControlPanel.setRange(-1, 1);
          break;
        case 4: // voltage
          normalControlPanel.setRange(-12, 12);
          break;
        default:
          normalControlPanel.setRange(-0.0, 0);
          break;
      }
    }
  }

  /**
   * Checks if the current type and mode are PID-compatible.
   */
  private boolean isPID() {
    switch (type) {
      case TYPE_TALON:
        return mode == 1 || mode == 2 || mode == 3; // position, speed, current
      case TYPE_JAGUAR:
        return mode == 1 || mode == 2 || mode == 3; // current, speed, position
      default:
        return false;
    }
  }

}
