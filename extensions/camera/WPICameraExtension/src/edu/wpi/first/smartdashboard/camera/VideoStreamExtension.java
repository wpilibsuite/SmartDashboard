/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.camera;

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
public class VideoStreamExtension extends StaticWidget {

    public static final String NAME = "Video Stream";
    private boolean connected = false;

    public class GCThread extends Thread {

        boolean destroyed = false;

        @Override
        public void run() {
            while (!destroyed) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                }
                System.gc();
            }
        }

        public void destroy() {
            destroyed = true;
            interrupt();
        }
    }

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
                    cam = new WPIFFmpegVideo(pathProperty.getSaveValue());
                }
                try {
                    image = cam.getNewImage(5.0);

                    if (image instanceof WPIColorImage) {
                        drawnImage = processImage((WPIColorImage) image).getBufferedImage();
                        repaint();

                    } else if (image instanceof WPIGrayscaleImage) {
                        drawnImage = processImage((WPIGrayscaleImage) image).getBufferedImage();
                        repaint();
                    }
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
    private WPIFFmpegVideo cam;
    private BufferedImage drawnImage;
    private BGThread bgThread = new BGThread();
    private GCThread gcThread = new GCThread();
    public final StringProperty pathProperty = new StringProperty(this, "Video Path", "http://localhost:8080/something");

    @Override
    public void init() {
        setPreferredSize(new Dimension(100, 100));
        bgThread.start();
        gcThread.start();
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
                cam = new WPIFFmpegVideo(pathProperty.getSaveValue());
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
        gcThread.destroy();
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

    public WPIImage processImage(WPIColorImage rawImage) {
        return rawImage;
    }

    public WPIImage processImage(WPIGrayscaleImage rawImage) {
        return rawImage;
    }
}
