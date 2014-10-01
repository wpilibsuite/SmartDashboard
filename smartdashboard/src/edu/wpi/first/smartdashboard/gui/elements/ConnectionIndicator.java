package edu.wpi.first.smartdashboard.gui.elements;

import java.awt.*;

import javax.swing.*;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.wpilibj.tables.*;

/**
 *
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
    private boolean firstRun = true;
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
        if (!connected) {
            connected = true;
            if (!firstRun) {
                SwingUtilities.invokeLater(repainter);
            }
        }
        firstRun = false;
	}

	@Override
	public void disconnected(IRemote remote) {
        if (connected) {
            connected = false;
            if (!firstRun) {
                SwingUtilities.invokeLater(repainter);
            }
        }
        firstRun = false;
	}
}
