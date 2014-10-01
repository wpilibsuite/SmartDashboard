/*
 * To change this template, choose Tools | Templates
 * getAnd open the template in the editor.
 */
package edu.wpi.first.wpijavacv;

import static com.googlecode.javacv.cpp.opencv_core.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;

/**
 * A grayscale image
 * 
 * @author Greg Granito
 */
public class WPIGrayscaleImage extends WPIImage {

    WPIGrayscaleImage(IplImage imageSrc) {
        super(imageSrc);
    }

    /**
     * Returns a black and white image where every pixel that is higher (in the 0-255 scale) than the given threshold is <bold>white</bold>,
     * and everything below is <bold>black</bold>.
     * @param threshold a value 0-255. if a pixel has a value below the theshold, it becomes black
     * if the pixel value is above or equal to the threshold, the pixel becomes white
     * @return a new {@link WPIBinaryImage} that represents the threshold
     */
    public WPIBinaryImage getThreshold(int threshold) {
        validateDisposed();

        IplImage bin = IplImage.create(image.cvSize(), 8, 1);
        cvThreshold(image, bin, threshold, 255, CV_THRESH_BINARY);
        return new WPIBinaryImage(bin);
    }

    /**
     * Returns a black and white image where every pixel that is higher (in the 0-255 scale) than the given threshold is <bold>black</bold>,
     * and everything below is <bold>white</bold>.
     *
     * In other words, this will return the inverted image of {@link WpiGrayscaleImage#getThreshold(int) getThreshold(...)} but is
     * more efficient than calling {@link WpiGrayscaleImage#getThreshold(int) getThreshold(...)}.{@link WPIBinaryImage#getInverse() getInverse()}
     * @param threshold a value 0-255. if a pixel has a value below the theshold, it becomes black
     * if the pixel value is above or equal to the threshold, the pixel becomes white
     * @return a new {@link WPIBinaryImage} that represents the threshold
     */
    public WPIBinaryImage getThresholdInverted(int threshold) {
        validateDisposed();

        IplImage bin = IplImage.create(image.cvSize(), 8, 1);
        cvThreshold(image, bin, threshold, 255, CV_THRESH_BINARY_INV);
        return new WPIBinaryImage(bin);
    }
}
