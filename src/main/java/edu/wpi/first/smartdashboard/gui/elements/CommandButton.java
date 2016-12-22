package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.CommandType;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;

/**
 * @author Joe Grinstead
 */
public class CommandButton extends AbstractTableWidget {

  public static final DataType[] TYPES = {CommandType.get()};

  @Override
  public void init() {
    JButton start = new JButton(getFieldName());
    start.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        table.putBoolean("running", true);
      }
    });

    start.setFocusable(false);

    setLayout(new BorderLayout());

    add(start, BorderLayout.CENTER);

    revalidate();
    repaint();
  }

  @Override
  public void propertyChanged(Property property) {
    //no properties
  }
}
