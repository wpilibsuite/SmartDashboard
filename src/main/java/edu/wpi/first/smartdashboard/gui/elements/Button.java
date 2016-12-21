package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.elements.bindings.AbstractTableWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.types.DataType;
import edu.wpi.first.smartdashboard.types.named.ButtonType;
import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

/**
 * @author Joe Grinstead
 */
public class Button extends AbstractTableWidget {

  public static final DataType[] TYPES = {ButtonType.get()};

  @Override
  public void init() {
    JButton start = new JButton(getFieldName());

    start.addMouseListener(new MouseAdapter() {
      @Override
      public void mousePressed(MouseEvent e) {
        table.putBoolean("pressed", true);
      }

      @Override
      public void mouseReleased(MouseEvent e) {
        table.putBoolean("pressed", false);
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
  }
}
