/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.camera;

import edu.wpi.first.smartdashboard.gui.DashboardPrefs;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIFFmpegVideo;
import edu.wpi.first.wpijavacv.WPIGrayscaleImage;
import edu.wpi.first.wpijavacv.WPIImage;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 *
 * @author Greg Granito
 */
public class VideoStreamViewerExtension extends StaticWidget {

    public static final String NAME = "Video Stream Viewer";
    private boolean connected = false;

    public class BGThread extends Thread {

        boolean destroyed = false;

        public BGThread() {
            super("Video Stream Background");
        }

        @Override
        public void run() {
            WPIImage image;
            while (!destroyed) {
                if (cam == null) {
                    cam = new WPIFFmpegVideoViewer(pathProperty.getSaveValue());
                }
                try {
                    drawnImage = cam.getNewImage(5.0);
                    repaint();

                } catch (final Exception e) {
                    e.printStackTrace();
                    cam.dispose();
                    cam = null;
                    drawnImage = null;
                    repaint();
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                    }
                }
            }

        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }
    private boolean resized = false;
    private WPIFFmpegVideoViewer cam;
    private BufferedImage drawnImage;
    private BGThread bgThread = new BGThread();
    private final int team = DashboardPrefs.getInstance().team.getValue();
    public final StringProperty pathProperty = new StringProperty(this, "Video Path", "http://10." + (team / 100) + "." + (team % 100) + ".11/mjpg/video.mjpg");

    @Override
    public void init() {
        setPreferredSize(new Dimension(100, 100));
        bgThread.start();
        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == pathProperty) {
            if (cam != null) {
                cam.dispose();
            }
            try {
                cam = new WPIFFmpegVideoViewer(pathProperty.getSaveValue());
            } catch (Exception e) {
                e.printStackTrace();
                drawnImage = null;
                setPreferredSize(new Dimension(100, 100));
                revalidate();
                repaint();
            }
        }

    }

    @Override
    public void disconnect() {
        bgThread.destroy();
        if(cam != null) cam.dispose();
        super.disconnect();
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (drawnImage != null) {
            if (!resized) {
                setPreferredSize(new Dimension(drawnImage.getWidth(), drawnImage.getHeight()));
                revalidate();
            }
            int width = getBounds().width;
            int height = getBounds().height;
            double scale = Math.min((double) width / (double) drawnImage.getWidth(), (double) height / (double) drawnImage.getHeight());
            g.drawImage(drawnImage, (int) (width - (scale * drawnImage.getWidth())) / 2, (int) (height - (scale * drawnImage.getHeight())) / 2,
                    (int) ((width + scale * drawnImage.getWidth()) / 2), (int) (height + scale * drawnImage.getHeight()) / 2,
                    0, 0, drawnImage.getWidth(), drawnImage.getHeight(), null);
        } else {
            g.setColor(Color.PINK);
            g.fillRect(0, 0, getBounds().width, getBounds().height);
            g.setColor(Color.BLACK);
            g.drawString("NO CONNECTION", 10, 10);
        }
    }
}
