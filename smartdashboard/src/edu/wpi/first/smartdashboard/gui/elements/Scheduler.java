package edu.wpi.first.smartdashboard.gui.elements;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.types.*;
import edu.wpi.first.smartdashboard.types.named.*;
import edu.wpi.first.wpilibj.networktables2.type.NumberArray;
import edu.wpi.first.wpilibj.networktables2.type.StringArray;
import edu.wpi.first.wpilibj.tables.*;

/**
 *
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
    private ITable table;
    private GridLayout commandLayout;
    private GridLayout cancelLayout;
    private CardLayout cardLayout;
    
    private StringArray commands = new StringArray();
    private NumberArray ids = new NumberArray();
    private NumberArray toCancel = new NumberArray();
    
    private ITableListener listener = new ITableListener() {

        boolean running = false;

        @Override
        public void valueChanged(ITable source, String key, Object value, boolean isNew) {
            if (running) {
                return;
            }
            running = true;
            SwingUtilities.invokeLater(new Runnable() {

                public void run() {
                    synchronized (table) {
                        table.retrieveValue("Names", commands);
                        table.retrieveValue("Ids", ids);
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
                                        table.retrieveValue("Cancel", toCancel);
                                        toCancel.add(ids.get(index));
                                        table.putValue("Cancel", toCancel);
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
            table.removeTableListener(listener);
        }
        table = (ITable) value;
        table.addTableListener(listener, true);

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
