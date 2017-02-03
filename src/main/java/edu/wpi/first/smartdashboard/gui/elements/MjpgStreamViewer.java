package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.IntegerProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.imageio.ImageIO;

/**
 * @author Greg Granito
 */
public abstract class MjpgStreamViewer extends StaticWidget {

  protected static final String STREAM_PREFIX = "mjpg:";
  private static final int[] START_BYTES = new int[]{0xFF, 0xD8};
  private static final int[] END_BYTES = new int[]{0xFF, 0xD9};
  private static final int MS_TO_ACCUM_STATS = 1000;
  private static final double BPS_TO_MBPS = 8.0 / 1024.0 / 1024.0;

  public final IntegerProperty rotateProperty = new IntegerProperty(this, "Degrees Rotation", 0);

  private double rotateAngleRad = 0;
  private long lastFPSCheck = 0;
  private int lastFPS = 0;
  private int fpsCounter = 0;
  private long bpsAccum = 0;
  private double lastMbps = 0;
  private BufferedImage imageToDraw;
  private BGThread bgThread = new BGThread();
  private boolean cameraChanged = true;

  public abstract Stream<String> streamPossibleCameraUrls();

  public void cameraChanged() {
    cameraChanged = true;
  }

  private boolean isCameraChanged() {
    if (cameraChanged) {
      cameraChanged = false;
      return true;
    }
    return false;
  }

  public void onInit() {
    // Override me!
  }

  public void onPropertyChanged(Property property) {
    // Override me!
  }

  @Override
  public final void init() {
    onInit();

    setPreferredSize(new Dimension(160, 120));
    rotateAngleRad = Math.toRadians(rotateProperty.getValue());
    bgThread.start();
    revalidate();
    repaint();
  }

  @Override
  public final void propertyChanged(final Property property) {
    if (property == rotateProperty) {
      rotateAngleRad = Math.toRadians(rotateProperty.getValue());
    }

    onPropertyChanged(property);
  }

  @Override
  public final void disconnect() {
    bgThread.interrupt();
    super.disconnect();
  }

  @Override
  protected final void paintComponent(Graphics g) {
    BufferedImage drawnImage = imageToDraw;

    if (drawnImage != null) {
      // cast the Graphics context into a Graphics2D
      Graphics2D g2d = (Graphics2D) g;

      // get the existing Graphics transform and copy it so that we can perform scaling and rotation
      AffineTransform origXform = g2d.getTransform();
      AffineTransform newXform = (AffineTransform) (origXform.clone());

      // find the center of the original image
      int origImageWidth = drawnImage.getWidth();
      int origImageHeight = drawnImage.getHeight();
      int imageCenterX = origImageWidth / 2;
      int imageCenterY = origImageHeight / 2;

      // perform the desired scaling
      double panelWidth = getBounds().width;
      double panelHeight = getBounds().height;
      double panelCenterX = panelWidth / 2.0;
      double panelCenterY = panelHeight / 2.0;
      double rotatedImageWidth = origImageWidth * Math.abs(Math.cos(rotateAngleRad))
          + origImageHeight * Math.abs(Math.sin(rotateAngleRad));
      double rotatedImageHeight = origImageWidth * Math.abs(Math.sin(rotateAngleRad))
          + origImageHeight * Math.abs(Math.cos(rotateAngleRad));

      // compute scaling needed
      double scale = Math.min(panelWidth / rotatedImageWidth, panelHeight / rotatedImageHeight);

      // set the transform before drawing the image
      // 1 - translate the origin to the center of the panel
      // 2 - perform the desired rotation (rotation will be about origin)
      // 3 - perform the desired scaling (will scale centered about origin)
      newXform.translate(panelCenterX, panelCenterY);
      newXform.rotate(rotateAngleRad);
      newXform.scale(scale, scale);
      g2d.setTransform(newXform);

      // draw image so that the center of the image is at the "origin"; the transform will take
      // care of the rotation and scaling
      g2d.drawImage(drawnImage, -imageCenterX, -imageCenterY, null);

      // restore the original transform
      g2d.setTransform(origXform);

      g.setColor(Color.PINK);
      g.drawString("FPS: " + lastFPS, 10, 10);
      g.drawString("Mbps: " + String.format("%.2f", lastMbps), 10, 25);
    } else {
      g.setColor(Color.PINK);
      g.fillRect(0, 0, getBounds().width, getBounds().height);
      g.setColor(Color.BLACK);
      g.drawString("NO CONNECTION", 10, 10);
    }
  }

  public class BGThread extends Thread {

    private InputStream stream;

    public BGThread() {
      super("Camera Viewer Background");
    }

    @Override
    public void interrupt() {
      try {
        if (stream != null) {
          stream.close();
        }
      } catch (IOException ex) {
        ex.printStackTrace();
      }

      super.interrupt();
    }

    @Override
    public void run() {
      ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
      long lastRepaint = 0;

      while (!interrupted()) {
        stream = getCameraStream();
        try {
          while (!interrupted() && !isCameraChanged() && stream != null) {
            while (System.currentTimeMillis() - lastRepaint < 10) {
              stream.skip(stream.available());
              Thread.sleep(1);
            }
            stream.skip(stream.available());

            imageBuffer.reset();
            readUntil(stream, START_BYTES);
            Arrays.stream(START_BYTES).forEachOrdered(imageBuffer::write);
            readUntil(stream, END_BYTES, imageBuffer);

            fpsCounter++;
            bpsAccum += imageBuffer.size();
            if (System.currentTimeMillis() - lastFPSCheck > MS_TO_ACCUM_STATS) {
              lastFPSCheck = System.currentTimeMillis();
              lastFPS = fpsCounter;
              lastMbps = bpsAccum * BPS_TO_MBPS;
              fpsCounter = 0;
              bpsAccum = 0;
            }

            lastRepaint = System.currentTimeMillis();
            ByteArrayInputStream tmpStream = new ByteArrayInputStream(imageBuffer.toByteArray());
            imageToDraw = ImageIO.read(tmpStream);
            repaint();
          }

        } catch (ArrayIndexOutOfBoundsException ex) {
          // Something really bad happened but we want to recover
          ex.printStackTrace();
        } catch (IOException ex) {
          imageToDraw = null;
          repaint();
          System.out.println(ex.getMessage());
          cameraChanged();
        } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
          throw new RuntimeException(ex);
        } finally {
          try {
            if (stream != null) {
              stream.close();
            }
          } catch (IOException ex) {
            ex.printStackTrace();
          }
        }
      }
    }

    private InputStream getCameraStream() {
      while (!interrupted()) {
        for (String streamUrl : streamPossibleCameraUrls()
            .filter(s -> s.startsWith(STREAM_PREFIX))
            .map(s -> s.substring(STREAM_PREFIX.length()))
            .collect(Collectors.toSet())) {
          System.out.println("Trying to connect to: " + streamUrl);
          try {
            URL url = new URL(streamUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(500);
            connection.setReadTimeout(5000);
            InputStream stream = connection.getInputStream();

            System.out.println("Connected to: " + streamUrl);
            return stream;
          } catch (IOException e) {
            imageToDraw = null;
            repaint();
            try {
              Thread.sleep(500);
            } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
              throw new RuntimeException(ex);
            }
          }
        }
      }
      return null;
    }

    private void readUntil(InputStream stream, int[] bytes) throws IOException {
      readUntil(stream, bytes, null);
    }

    private void readUntil(InputStream stream, int[] bytes, ByteArrayOutputStream buffer)
        throws IOException {
      for (int i = 0; i < bytes.length; ) {
        int b = stream.read();
        if (b == -1) {
          throw new IOException("End of Stream reached");
        }
        if (buffer != null) {
          buffer.write(b);
        }
        if (b == bytes[i]) {
          i++;
        } else {
          i = 0;
        }
      }
    }
  }
}
