package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.robot.Robot;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.EOFException;
import java.net.Socket;
import java.net.ConnectException;
import java.util.Arrays;

/**
 * SmartDashboard extension for viewing a MJPEG stream from the robot,
 * typically forwarded from a USB webcam, or images processed by a user
 * program. This is mostly compatible with the LabVIEW dashboard in
 * "HW Compression" mode.
 *
 * @author Tom Clark
 */
public class WebcamViewerExtension extends StaticWidget implements Runnable {

    public static final String NAME = "USB Webcam Viewer";

    public final IntegerProperty fpsProperty = new IntegerProperty(this, "FPS", 30);

    private final static int PORT = 1180;
    private final static byte[] MAGIC_NUMBERS = { 0x01, 0x00, 0x00, 0x00 };
    private final static int SIZE_640x480 = 0;
    private final static int SIZE_320x240 = 1;
    private final static int SIZE_160x120 = 2;
    private final static int HW_COMPRESSION = -1;

    private BufferedImage frame = null;
    private final Object frameMutex = new Object();;
    private String errorMessage = null;

    private Socket socket;
    private Thread thread;


    /** {@inheritDoc} */
    @Override
    public void init() {
        setPreferredSize(new Dimension(320, 240));

        this.thread = new Thread(this);
        this.thread.start();

        ImageIO.setUseCache(false);
    }


    /** {@inheritDoc} */
    @Override
    public void disconnect() {
        this.thread.stop();

        if (this.socket != null) {
            try {
                this.socket.close();
            }
            catch (IOException e) {}
        }
    }


    /** {@inheritDoc} */
    @Override
    public void propertyChanged(Property property) {
        /* Close and reopen the stream with the new settings */
        this.thread.interrupt();
    }


    /**
     * Continuously request and receive frames from the roboRIO
     */
    @Override
    public void run() {
        for (;;) {
            try {
                this.socket = new Socket(Robot.getHost(), PORT);
                DataInputStream inputStream = new DataInputStream(this.socket.getInputStream());
                DataOutputStream outputStream = new DataOutputStream(this.socket.getOutputStream());

                /* Send the request */
                outputStream.writeInt(this.fpsProperty.getValue());
                outputStream.writeInt(HW_COMPRESSION);
                outputStream.writeInt(SIZE_640x480);
                outputStream.flush();

                /* Get the response from the robot */
                while (!Thread.interrupted()) {
                    /* Each frame has a header with 4 magic bytes and the number of bytes in the image */
                    byte[] magic = new byte[4];
                    inputStream.readFully(magic);
                    int size = inputStream.readInt();

                    assert Arrays.equals(magic, MAGIC_NUMBERS);


                    /* Get the image data itself, and make sure that it's a valid JPEG image (it starts with
                     * [0xff,0xd8] and ends with [0xff,0xd9] */
                    byte[] data = new byte[size];
                    inputStream.readFully(data);

                    assert data.length >= 4 && data[0] == 0xff && data[1] == 0xd8
                            && data[data.length - 2] == 0xff && data[data.length - 1] == 0xd9;


                    /* Decode the data and re-paint the component with the new frame */
                    synchronized (this.frameMutex) {
                        if (this.frame != null) {
                            this.frame.flush();
                        }

                        this.frame = ImageIO.read(new ByteArrayInputStream(data));
                        this.errorMessage = null;
                        this.repaint();
                    }
                }
            }
            catch (ConnectException e) {
                if (this.errorMessage == null) {
                    this.errorMessage = e.getMessage();
                }
            }
            catch (EOFException e) {
                if (this.errorMessage == null) {
                    this.errorMessage = "Robot stopped returning images";
                }
            }
            catch (IOException e) {
                if (this.errorMessage == null) {
                    this.errorMessage = e.getMessage();
                }
            }
            finally {
                if (this.socket != null) {
                    try {
                        this.socket.close();
                    }
                    catch (IOException e) {}
                }

                this.repaint();

                try {
                    Thread.sleep(1000);
                }
                catch (InterruptedException e1) {}
            }
        }
    }


    /**
     * Draw the latest image to the screen, blocking if one is being received,
     * or showing an error message if there's some problem with the image.
     *
     * @param g the <code>Graphics</code> context in which to paint
     */
    @Override
    protected void paintComponent(Graphics g) {
        int imageX = 0,
            imageY = 0,
            imageWidth = this.getWidth(),
            imageHeight = this.getHeight();

        synchronized (this.frameMutex) {
            /* Adjust the image size and location depending on how the aspect ratio matches up with the aspect ratio
             * of this component. */
            if (this.frame != null) {
                float thisAspectRatio = (float) this.getWidth() / this.getHeight();
                float imageAspectRatio = (float) this.frame.getWidth(null) / this.frame.getHeight(null);

                if (imageAspectRatio < thisAspectRatio) {
                    imageWidth = (int) (this.getHeight() * imageAspectRatio);
                    imageX = (this.getWidth() - imageWidth) / 2;
                } else {
                    imageHeight = (int) (this.getWidth() / imageAspectRatio);
                    imageY = (this.getHeight() - imageHeight) / 2;
                }

                g.drawImage(this.frame, imageX, imageY, imageWidth, imageHeight, null, null);
            }

            /* If there's some problem getting the image, show the error on the screen */
            if (this.errorMessage != null) {
                g.setClip(imageX, imageY, imageWidth, imageHeight);

                g.setColor(Color.pink);
                g.fillRect(imageX, imageY + imageHeight - 18, imageWidth, 18);
                g.setColor(Color.black);

                Font font = g.getFont();

                g.setFont(font.deriveFont(Font.BOLD));
                g.drawString("Error: ", imageX + 2, imageY + imageHeight - 6);
                g.setFont(font);
                g.drawString(this.errorMessage, imageX + 40, imageY + imageHeight - 6);
            }
        }
    }
}
