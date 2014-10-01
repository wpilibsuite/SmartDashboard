package edu.wpi.first.smartdashboard.camera;

import com.googlecode.javacv.FFmpegFrameGrabber;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import edu.wpi.first.wpijavacv.WPIDisposable;
import edu.wpi.first.wpijavacv.WPIImage;
import java.awt.image.BufferedImage;

/**
 * A class used to gather images from the robot's camera.
 * @author Joe Grinstead and Greg Granito
 */
public class WPIFFmpegVideoViewer extends WPIDisposable {

    private FFmpegFrameGrabber grabber;
    private IplImage image;
    private boolean readImage = true;
    private boolean badConnection = false;
    private final Object imageLock = new Object();
    private final Object grabberLock = new Object();

    public WPIFFmpegVideoViewer(final String path) {
        new Thread() {

            @Override
            public void run() {
                grabber = new FFmpegFrameGrabber(path);
		grabber.setFrameRate(1.0);
                try {
                    grabber.start();

                    while (!isDisposed()) {
                        try {
                            IplImage newest;
                            synchronized (grabberLock) {
                                if (isDisposed()) {
                                    return;
                                }
                                newest = grabber.grab();
                            }
                            if (isNull(newest)) {
                                synchronized (imageLock) {
                                    badConnection = true;
                                    imageLock.notify();
                                }
                                return;
                            } else {
                                synchronized (imageLock) {
                                    if (image == null) {
                                        image = cvCreateImage(newest.cvSize(), newest.depth(), newest.nChannels());
                                    }
                                    cvCopy(newest, image);
                                    readImage = false;
                                    badConnection = false;
                                    imageLock.notify();
                                }
                            }
                        } catch (Exception ex) {
                            synchronized (imageLock) {
                                badConnection = true;
                                imageLock.notify();
                            }
                            ex.printStackTrace();
                            return;
                        }
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException ex) {
                        }
                    }
                } catch (Exception ex) {
                    synchronized (imageLock) {
                        badConnection = true;
                        imageLock.notify();
                    }
                    ex.printStackTrace();
                }
            }
        }.start();
    }
    
    public BufferedImage getNewImage(double timeout) throws BadConnectionException {
        validateDisposed();

        synchronized (imageLock) {
            readImage = true;
            while (readImage && !badConnection) {
                try {
                    badConnection = true;
                    imageLock.wait((long) (timeout * 1000));
                } catch (InterruptedException ex) {
                }
            }
            readImage = true;
            
            
            if (badConnection) {
                throw new BadConnectionException();
            } else if (image == null) {
                return null;
            } else {
                return image.getBufferedImage();
            }
        }
    }

    @Override
    protected void disposed() {
        try {
            synchronized (imageLock) {
                if (!isNull(image)) {
                    image.release();
                }
                image = null;
            }
        } catch (Exception ex) {
        }
    }

    /**
     * An exception that occurs when the camera can not be reached.
     * @author Greg Granito
     */
    public static class BadConnectionException extends Exception {
    }

    @Override
    protected void finalize() throws Throwable {
        grabber.stop();
        super.finalize();
    }
}
