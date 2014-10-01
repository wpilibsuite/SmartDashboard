/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpijavacv;

import java.util.ArrayList;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 * A {@link WPIBinaryImage} object is a black and white image.
 * 
 * @author Greg Granito
 */
public class WPIBinaryImage extends WPIImage {

    /**
     * Instantiates a {@link WPIBinaryImage} from the given {@link IplImage}.
     * The resulting image will be directly wrapped around the given image, and so any modifications
     * to the {@link WPIBinaryImage} will reflect on the given image.
     * @param image the image to wrap
     */
    protected WPIBinaryImage(IplImage image) {
        super(image);
    }

    /**
     * Returns the result of "and-ing" the given image with the calling image.  Every pixel in the image
     * will be "and-ed" with the corresponding pixel in the other image to form a new image.  The result
     * of and-ing two pixels is pictured in the table below.
     *
     * <table border="1"><tr>
     * <th></th>
     * <th>White</th>
     * <th>Black</th>
     * </tr><tr>
     * <th>White</th>
     * <td>White</td>
     * <td>Black</td>
     * </tr><tr>
     * <th>Black</th>
     * <td>Black</td>
     * <td>Black</td>
     * </tr></table>
     *
     * @param image2 a second {@link WPIBinaryImage}
     * @return an image that is the pixel-wise and of the two images.
     */
    public WPIBinaryImage getAnd(WPIBinaryImage image2) {
        validateDisposed();

        IplImage result = IplImage.create(image.cvSize(), image.depth(), 1);
        cvAnd(image, image2.image, result, null);
        return new WPIBinaryImage(result);
    }

    /**
     * "and-s" this image with the given image.  Every pixel in this image
     * will be "and-ed" with the corresponding pixel in the other image.  The result
     * of and-ing two pixels is pictured in the table below.
     *
     * <table border="1"><tr>
     * <th></th>
     * <th>White</th>
     * <th>Black</th>
     * </tr><tr>
     * <th>White</th>
     * <td>White</td>
     * <td>Black</td>
     * </tr><tr>
     * <th>Black</th>
     * <td>Black</td>
     * <td>Black</td>
     * </tr></table>
     *
     * This will modify the image.  If you do now wish to modify the image, use {@link WPIBinaryImage#getAnd(wpijavacv.WPIBinaryImage) getAnd(...)}
     * instead.
     *
     * @param image2 a second {@link WPIBinaryImage}
     */
    public void and(WPIBinaryImage image2) {
        validateDisposed();

        cvAnd(image, image2.image, image, null);
    }

    /**
     * Returns the result of "or-ing" the given image with the calling image.  Every pixel in the image
     * will be "or-ed" with the corresponding pixel in the other image to form a new image.  The result
     * of or-ing two pixels is pictured in the table below.
     *
     * <table border="1"><tr>
     * <th></th>
     * <th>White</th>
     * <th>Black</th>
     * </tr><tr>
     * <th>White</th>
     * <td>White</td>
     * <td>White</td>
     * </tr><tr>
     * <th>Black</th>
     * <td>White</td>
     * <td>Black</td>
     * </tr></table>
     *
     * @param image2 a second {@link WPIBinaryImage}
     * @return an image that is the pixel-wise or of the two images.
     */
    public WPIBinaryImage getOr(WPIBinaryImage image2) {
        validateDisposed();

        IplImage result = IplImage.create(image.cvSize(), image.depth(), 1);
        cvOr(image, image2.image, result, null);
        return new WPIBinaryImage(result);
    }

    /**
     * "or-s" this image with the given image.  Every pixel in this image
     * will be "or-ed" with the corresponding pixel in the other image.  The result
     * of or-ing two pixels is pictured in the table below.  This will modify the image.
     *
     * <table border="1"><tr>
     * <th></th>
     * <th>White</th>
     * <th>Black</th>
     * </tr><tr>
     * <th>White</th>
     * <td>White</td>
     * <td>White</td>
     * </tr><tr>
     * <th>Black</th>
     * <td>White</td>
     * <td>Black</td>
     * </tr></table>
     *
     * This will modify the image.  If you do now wish to modify the image, use {@link WPIBinaryImage#getOr(wpijavacv.WPIBinaryImage) getOr(...)}
     * instead.
     *
     * @param image2 a second {@link WPIBinaryImage}
     */
    public void or(WPIBinaryImage image2) {
        validateDisposed();

        cvOr(image, image2.image, image, null);
    }

    /**
     * Inverts this image.  Everything which was white will now be black and
     * everything which was black will now be white.
     * This will modify the image.  If you do now wish to modify the image, use {@link WPIBinaryImage#getInverse() getInverse()} instead.
     * instead.
     */
    public void invert() {
        validateDisposed();

        cvInv(image, image);
    }

    /**
     * Returns an image that is the inverse of the calling one.  In other words, it returns a copy of this image except
     * that the copy will replace every black pixel with white one and every white pixel with a black one.
     * @return a new {@link WPIBinaryImage} that is the inverse of the image
     */
    public WPIBinaryImage getInverse() {
        validateDisposed();

        IplImage result = IplImage.create(image.cvSize(), image.depth(), 1);
        cvInv(image, result);
        return new WPIBinaryImage(result);
    }

    /**
     * Dilates the image the specified number of times.  Every time the image is dilated, every pixel which borders a white pixel
     * will itself become white.  The effect is that white pixels in the image begin to spread.
     * This is useful if you wish to remove small "holes" from the image. 
     *
     * This will modify the image.  If you do now wish to modify the image, use {@link WPIBinaryImage#getDilated(int) getDilated(...)} instead.
     * instead.
     *
     * @param iterations the number of times to perform the dilation.  For example, if this is 3, then every pixel within three
     * spaces of a white pixel will become white.
     */
    public void dilate(int iterations) {
        validateDisposed();

        cvDilate(image, image, null, iterations);
    }

    /**
     * Returns an image that is the result of dilating the specified number of times.  Every time an image is dilated, every pixel which borders a white pixel
     * will itself become white.  The effect is that white pixels in the image begin to spread.
     * This is useful if you wish to remove small "holes" from the image.
     * 
     * @param iterations the number of times to perform the dilation.  For example, if this is 3, then every pixel within three
     * @return returns a new {@link WPIBinaryImage} in which the surrounding pixels to each white pixel
     *          are changed to white
     */
    public WPIBinaryImage getDilated(int iterations) {
        validateDisposed();

        IplImage result = IplImage.create(image.cvSize(), image.depth(), 1);
        cvDilate(image, result, null, iterations);
        return new WPIBinaryImage(result);
    }

    /**
     * Erodes the image the specified number of times.  Every time the image is eroded, every pixel which borders a black pixel
     * will itself become black.  The effect is that black pixels in the image begin to spread.
     * This is useful if you wish to shrink or remove white blobs from an image.
     *
     * This will modify the image.  If you do now wish to modify the image, use {@link WPIBinaryImage#getEroded(int) getEroded(...)} instead.
     * instead.
     *
     * @param iterations the number of times to perform the erosion.  For example, if this is 3, then every pixel within three
     * spaces of a black pixel will become black.
     */
    public void erode(int iterations) {
        validateDisposed();

        cvErode(image, image, null, iterations);
    }

    /**
     * Returns an image that is the result of eroding the specified number of times.  Every time the image is eroded, every pixel which borders a black pixel
     * will itself become black.  The effect is that black pixels in the image begin to spread.
     * This is useful if you wish to shrink or remove white blobs from an image.
     * 
     * @param iterations the number of times to perform the erosion.  For example, if this is 3, then every pixel within three
     * spaces of a black pixel will become black.
     * @return a new {@link WPIBinaryImage} in which the surrounding pixels to each black pixel
     *          are changed to black
     */
    public WPIBinaryImage getEroded(int iterations) {
        validateDisposed();

        IplImage result = IplImage.create(image.cvSize(), image.depth(), 1);
        cvErode(image, result, null, iterations);
        return new WPIBinaryImage(result);
    }

    /**
     * Finds all the "contours" in the image.  A contour is basically an outline.
     * @return an array of {@link WpiContour} that is all of the edges in the image
     */
    public WPIContour[] findContours() {
        validateDisposed();

        IplImage tempImage = IplImage.create(image.cvSize(), image.depth(), 1);

        cvCopy(image, tempImage);

        final CvMemStorage storage = CvMemStorage.create();
        WPIMemoryPool pool = new WPIMemoryPool() {

            @Override
            protected void disposed() {
               cvClearMemStorage(storage);
               storage.release();
            }
        };

        CvSeq contours = new CvSeq();
        cvFindContours(tempImage, storage, contours, 256, CV_RETR_LIST, CV_CHAIN_APPROX_TC89_KCOS);
        ArrayList<WPIContour> results = new ArrayList();
        while (!isNull(contours)) {
            WPIContour contour = new WPIContour(cvCloneSeq(contours, storage));
            results.add(contour);
            pool.addToPool(contour);
            contours = contours.h_next();
        }

        tempImage.release();
        WPIContour[] array = new WPIContour[results.size()];
        return results.toArray(array);
    }
}
