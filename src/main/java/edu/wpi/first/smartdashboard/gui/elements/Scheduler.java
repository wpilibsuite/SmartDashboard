package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTable.TableEventListener;
import edu.wpi.first.smartdashboard.gui.Widget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.SchedulerType;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * @author Jeff Copeland
 */
public class Scheduler extends Widget {

  public static final DataType[] TYPES = {SchedulerType.get()};
  private static final String NO_COMMAND_CARD = "No Command";
  private static final String COMMAND_CARD = "Commands";
  private int count = 0;
  private JLabel noCommands;
  private JPanel commandLabels;
  private JPanel cancelButtons;
  private JPanel commandPanel;
  private List<JLabel> labels;
  private List<JButton> buttons;
  private NetworkTable table;
  private GridLayout commandLayout;
  private GridLayout cancelLayout;
  private CardLayout cardLayout;

  private List<String> commands = new ArrayList<>();
  private List<Double> ids = new ArrayList<>();
  private List<Double> toCancel = new ArrayList<>();

  private int listenerHandle;
  private TableEventListener listener = new TableEventListener() {

    boolean running = false;

    @Override
    public void accept(NetworkTable source, String key, 
                             NetworkTableEvent event) {
      if (running) {
        return;
      }
      running = true;
      SwingUtilities.invokeLater(new Runnable() {

        public void run() {
          synchronized (table) {
            commands = Arrays.asList(table.getEntry("Names").getStringArray(new String[0]));
            ids = Arrays.asList(table.getEntry("Ids").getDoubleArray(new Double[0]));
            assert commands.size() == ids.size();

            // Update displayed commands
            for (int i = 0; i < commands.size(); i++) {
              if (i >= labels.size()) {
                labels.add(new JLabel());
              }
              JLabel label = labels.get(i);
              label.setText(commands.get(i));

              if (i >= buttons.size()) {
                JButton button = new JButton("cancel");
                final int index = i;
                button.addActionListener(new ActionListener() {
                  public void actionPerformed(ActionEvent e) {
                    // Cancel commands
                    toCancel = new ArrayList<Double>(Arrays.asList(table.getEntry("Cancel")
                                    .getDoubleArray(new Double[0])));
                    toCancel.add(ids.get(index));
                    table.getEntry("Cancel").setValue(toCancel.toArray(new Double[0]));
                  }
                });
                buttons.add(button);
              }
              JButton button = buttons.get(i);

              if (i > count - 1) {
                commandLabels.add(label);
                cancelButtons.add(button);
              }
            }
          }

          // Remove leftover widgets
          if (count > commands.size()) {
            for (int i = commands.size(); i < count; i++) {
              commandLabels.remove(labels.get(i));
              cancelButtons.remove(buttons.get(i));
            }
          }

          count = commands.size();

          cardLayout.show(Scheduler.this, count == 0 ? NO_COMMAND_CARD : COMMAND_CARD);

          running = false;
        }
      });
    }
  };

  @Override
  public void setValue(Object value) {
    if (table != null) {
      table.removeListener(listenerHandle);
    }
    table = (NetworkTable) value;
    listenerHandle = table.addListener(
      EnumSet.of(NetworkTableEvent.Kind.kImmediate, NetworkTableEvent.Kind.kPublish,
                 NetworkTableEvent.Kind.kValueAll),
                 listener);

    revalidate();
    repaint();
  }

  @Override
  public void init() {
    setLayout(cardLayout = new CardLayout());

    labels = new ArrayList<JLabel>();
    buttons = new ArrayList<JButton>();

    commandPanel = new JPanel();
    commandPanel.setLayout(new GridLayout(0, 2));

    commandLabels = new JPanel();
    cancelButtons = new JPanel();

    commandPanel.add(commandLabels, BorderLayout.WEST);
    commandPanel.add(cancelButtons, BorderLayout.CENTER);

    commandLayout = new GridLayout(0, 1);
    cancelLayout = new GridLayout(0, 1);

    commandLabels.setLayout(commandLayout);
    cancelButtons.setLayout(cancelLayout);

    add(commandPanel, COMMAND_CARD);

    noCommands = new JLabel("No commands running.");
    noCommands.setHorizontalAlignment(JLabel.CENTER);
    add(noCommands, NO_COMMAND_CARD);

    cardLayout.show(this, NO_COMMAND_CARD);

    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
  }
}
