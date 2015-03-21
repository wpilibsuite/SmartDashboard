package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.*;
import edu.wpi.first.smartdashboard.robot.Robot;

import javax.imageio.ImageIO;
import java.awt.*;
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
 * @author Ryan Cahoon
 */
public class WebcamViewerExtension extends StaticWidget implements Runnable {

    public static final String NAME = "USB Webcam Viewer";

    public final IntegerProperty fpsProperty = new IntegerProperty(this, "FPS", 30);
    public final MultiProperty sizeProperty;

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

    public WebcamViewerExtension() {
        super();
        sizeProperty = new MultiProperty(this, "Size");
        sizeProperty.add("640x480", SIZE_640x480);
        sizeProperty.add("320x240", SIZE_320x240);
        sizeProperty.add("160x120", SIZE_160x120);
        sizeProperty.setDefault("640x480");
    }
    
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


    static final int[] huffman_table_int = new int[] {
        0xFF, 0xC4, 0x01, 0xA2, 0x00, 0x00, 0x01, 0x05, 0x01, 0x01,
        0x01, 0x01, 0x01, 0x01, 0x00, 0x00, 0x00, 0x00, 0x00,
        0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06,
        0x07, 0x08, 0x09, 0x0A, 0x0B, 0x01, 0x00, 0x03, 0x01,
        0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x01, 0x00,
        0x00, 0x00, 0x00, 0x00, 0x00, 0x01, 0x02, 0x03, 0x04,
        0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x10, 0x00,
        0x02, 0x01, 0x03, 0x03, 0x02, 0x04, 0x03, 0x05, 0x05,
        0x04, 0x04, 0x00, 0x00, 0x01, 0x7D, 0x01, 0x02, 0x03,
        0x00, 0x04, 0x11, 0x05, 0x12, 0x21, 0x31, 0x41, 0x06,
        0x13, 0x51, 0x61, 0x07, 0x22, 0x71, 0x14, 0x32, 0x81,
        0x91, 0xA1, 0x08, 0x23, 0x42, 0xB1, 0xC1, 0x15, 0x52,
        0xD1, 0xF0, 0x24, 0x33, 0x62, 0x72, 0x82, 0x09, 0x0A,
        0x16, 0x17, 0x18, 0x19, 0x1A, 0x25, 0x26, 0x27, 0x28,
        0x29, 0x2A, 0x34, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
        0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53,
        0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63, 0x64,
        0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75,
        0x76, 0x77, 0x78, 0x79, 0x7A, 0x83, 0x84, 0x85, 0x86,
        0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95, 0x96,
        0x97, 0x98, 0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5, 0xA6,
        0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5, 0xB6,
        0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5, 0xC6,
        0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5, 0xD6,
        0xD7, 0xD8, 0xD9, 0xDA, 0xE1, 0xE2, 0xE3, 0xE4, 0xE5,
        0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF1, 0xF2, 0xF3, 0xF4,
        0xF5, 0xF6, 0xF7, 0xF8, 0xF9, 0xFA, 0x11, 0x00, 0x02,
        0x01, 0x02, 0x04, 0x04, 0x03, 0x04, 0x07, 0x05, 0x04,
        0x04, 0x00, 0x01, 0x02, 0x77, 0x00, 0x01, 0x02, 0x03,
        0x11, 0x04, 0x05, 0x21, 0x31, 0x06, 0x12, 0x41, 0x51,
        0x07, 0x61, 0x71, 0x13, 0x22, 0x32, 0x81, 0x08, 0x14,
        0x42, 0x91, 0xA1, 0xB1, 0xC1, 0x09, 0x23, 0x33, 0x52,
        0xF0, 0x15, 0x62, 0x72, 0xD1, 0x0A, 0x16, 0x24, 0x34,
        0xE1, 0x25, 0xF1, 0x17, 0x18, 0x19, 0x1A, 0x26, 0x27,
        0x28, 0x29, 0x2A, 0x35, 0x36, 0x37, 0x38, 0x39, 0x3A,
        0x43, 0x44, 0x45, 0x46, 0x47, 0x48, 0x49, 0x4A, 0x53,
        0x54, 0x55, 0x56, 0x57, 0x58, 0x59, 0x5A, 0x63, 0x64,
        0x65, 0x66, 0x67, 0x68, 0x69, 0x6A, 0x73, 0x74, 0x75,
        0x76, 0x77, 0x78, 0x79, 0x7A, 0x82, 0x83, 0x84, 0x85,
        0x86, 0x87, 0x88, 0x89, 0x8A, 0x92, 0x93, 0x94, 0x95,
        0x96, 0x97, 0x98, 0x99, 0x9A, 0xA2, 0xA3, 0xA4, 0xA5,
        0xA6, 0xA7, 0xA8, 0xA9, 0xAA, 0xB2, 0xB3, 0xB4, 0xB5,
        0xB6, 0xB7, 0xB8, 0xB9, 0xBA, 0xC2, 0xC3, 0xC4, 0xC5,
        0xC6, 0xC7, 0xC8, 0xC9, 0xCA, 0xD2, 0xD3, 0xD4, 0xD5,
        0xD6, 0xD7, 0xD8, 0xD9, 0xDA, 0xE2, 0xE3, 0xE4, 0xE5,
        0xE6, 0xE7, 0xE8, 0xE9, 0xEA, 0xF2, 0xF3, 0xF4, 0xF5,
        0xF6, 0xF7, 0xF8, 0xF9, 0xFA
    };

    static final byte[] huffman_table;
    static {
        huffman_table = new byte[huffman_table_int.length];
        for (int i = 0; i < huffman_table.length; ++i) {
            huffman_table[i] = (byte) huffman_table_int[i];
        }
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

                final int framesize = (Integer) sizeProperty.getValue();

                /* Send the request */
                outputStream.writeInt(this.fpsProperty.getValue());
                outputStream.writeInt(HW_COMPRESSION);
                outputStream.writeInt(framesize);
                outputStream.flush();

                /* Get the response from the robot */
                while (!Thread.interrupted() &&
                        framesize == (Integer) sizeProperty.getValue()) {
                    /* Each frame has a header with 4 magic bytes and the number of bytes in the image */
                    byte[] magic = new byte[4];
                    inputStream.readFully(magic);
                    int size = inputStream.readInt();

                    assert Arrays.equals(magic, MAGIC_NUMBERS);


                    /* Get the image data itself, and make sure that it's a valid JPEG image (it starts with
                     * [0xff,0xd8] and ends with [0xff,0xd9] */
                    byte[] data = new byte[size+huffman_table.length];
                    inputStream.readFully(data, 0, size);

                    assert size >= 4 && (data[0] & 0xff) == 0xff && (data[1] & 0xff) == 0xd8 &&
                        (data[size - 2] & 0xff) == 0xff && (data[size - 1] & 0xff) == 0xd9;;

                    int pos = 2;
                    boolean has_dht = false;
                    while (!has_dht) {
                        assert pos+4 <= size;
                        assert (data[pos] & 0xff) == 0xff;

                        if ((data[pos+1] & 0xff) == 0xc4)
                            has_dht = true;
                        else if ((data[pos+1] & 0xff) == 0xda)
                            break;

                        // Skip to the next marker.
                        int marker_size = ((data[pos+2] & 0xff) << 8) + (data[pos+3] & 0xff);
                        pos += marker_size+2;
                    }
                    if (!has_dht) {
                        System.arraycopy(data, pos, data, pos+huffman_table.length, size-pos);
                        System.arraycopy(huffman_table, 0, data, pos, huffman_table.length);
                        size += huffman_table.length;
                    }

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
