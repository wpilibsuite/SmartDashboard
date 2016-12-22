package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.ColorProperty;
import edu.wpi.first.smartdashboard.properties.MultiProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.wpilibj.tables.IRemote;
import edu.wpi.first.wpilibj.tables.IRemoteConnectionListener;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.SwingUtilities;

/**
 * @author Joe Grinstead
 */
public class ConnectionIndicator extends StaticWidget implements IRemoteConnectionListener {

  public static final String NAME = "Connection Indicator";
  private static final int DRAW_EMBOSSED = 0;
  private static final int DRAW_ENGRAVED = 1;
  private static final int DRAW_ROUNDED = 2;
  private static final int DRAW_PLAIN = 3;
  public final ColorProperty positive = new ColorProperty(this, "Connection Color", Color.GREEN);
  public final ColorProperty negative = new ColorProperty(this, "No Connection Color", Color.RED);
  public final MultiProperty display = new MultiProperty(this, "Graphics");
  private boolean connected = false;
  private Runnable repainter = new Runnable() {

    public void run() {
      repaint();
    }
  };

  public ConnectionIndicator() {
    setPreferredSize(new Dimension(32, 32));

    display.add("Embossed", DRAW_EMBOSSED);
    display.add("Engraved", DRAW_ENGRAVED);
    display.add("Rounded", DRAW_ROUNDED);
    display.add("Simple", DRAW_PLAIN);
    display.setDefault("Embossed");
  }

  @Override
  public void init() {
    Robot.addConnectionListener(this, true);
  }

  @Override
  public void disconnect() {
    Robot.removeConnectionListener(this);
  }

  @Override
  public void propertyChanged(Property property) {
    if (property == positive && connected) {
      repaint();
    } else if (property == negative && !connected) {
      repaint();
    } else if (property == display) {
      repaint();
    }
  }

  @Override
  protected void paintComponent(Graphics g) {
    Dimension size = getSize();

    switch ((Integer) display.getValue()) {
      case DRAW_EMBOSSED:
        g.setColor(connected ? positive.getValue() : negative.getValue());
        g.fill3DRect(0, 0, size.width, size.height, true);
        break;
      case DRAW_ENGRAVED:
        g.setColor(connected ? positive.getValue() : negative.getValue());
        g.fill3DRect(0, 0, size.width, size.height, false);
        break;
      case DRAW_ROUNDED:
        g.setColor(getBackground());
        g.fillRect(0, 0, size.width, size.height);
        g.setColor(connected ? positive.getValue() : negative.getValue());
        g.fillRoundRect(0, 0, size.width, size.height, 8, 8);
        break;
      case DRAW_PLAIN:
      default:
        g.setColor(connected ? positive.getValue() : negative.getValue());
        g.fillRect(0, 0, size.width, size.height);
    }
  }

  @Override
  public void connected(IRemote remote) {
    System.out.println("ConnectionIndicator CONNECTED");
    if (!connected) {
      connected = true;
      SwingUtilities.invokeLater(repainter);
    }
  }

  @Override
  public void disconnected(IRemote remote) {
    System.out.println("ConnectionIndicator DISCONNECTED");
    if (connected) {
      connected = false;
      SwingUtilities.invokeLater(repainter);
    }
  }
}
