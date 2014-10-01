/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpijavacv;

import java.awt.image.BufferedImage;
import static com.googlecode.javacv.cpp.opencv_core.*;

/**
 * A color image
 * @author Greg Granito
 */
public class WPIColorImage extends WPIImage {

    private WPIGrayscaleImage red;
    private WPIGrayscaleImage blue;
    private WPIGrayscaleImage green;

    /**
     * creates a WpiColorImage based on the BufferedImage source
     * @param imageSrc the source image
     */
    public WPIColorImage(BufferedImage imageSrc) {
        super(imageSrc);
    }

    WPIColorImage(IplImage imageSrc) {
        super(imageSrc);
    }

    /**
     * Draws a WpiContour
     * @param c the contour
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawContour(WPIContour c, WPIColor color, int thickness) {
        cvDrawContours(image, c.getCVSeq(), color.toCvScalar(), color.toCvScalar(), 100, thickness, 8);
    }

    /**
     * Draws all the WpiContours in an array
     * @param c the contours
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawContours(WPIContour[] c, WPIColor color, int thickness){
        for(WPIContour con:c){
            drawContour(con, color, thickness);
        }
    }

    /**
     * Draws a line between the two specified WpiPoints
     * @param p1 Point 1
     * @param p2 Point 2
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawLine(WPIPoint p1, WPIPoint p2, WPIColor color, int thickness){
        cvLine(image, p1.getCvPoint(), p2.getCvPoint(), color.toCvScalar(), thickness, 8, 0);
    }

    /**
     * Draws a WpiPolygon
     * @param p the WpiPolygon
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawPolygon(WPIPolygon p, WPIColor color, int thickness){
        cvDrawContours(image, p.getCVSeq(), color.toCvScalar(), color.toCvScalar(), 100, thickness, 8);
    }

    /**
     * Draws a WpiPoint
     * @param p the WpiPoint
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawPoint(WPIPoint p, WPIColor color, int thickness){
        cvDrawCircle(image,p.getCvPoint(), thickness, color.toCvScalar(), CV_FILLED, 8, 0);
    }
    /**
     * Draws all the WpiPoints in an array
     * @param p the WpiPoint array
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawPoints(WPIPoint[] p, WPIColor color, int thickness){
        for(int i = 0; i< p.length; i++)
            drawPoint(p[i], color, thickness);
    }
    /**
     * Draws all the WpiPolygons in an array
     * @param p the WpiPolygon array
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawPolygons(WPIPolygon[] p, WPIColor color, int thickness){
        for(WPIPolygon polygon : p){
            if(polygon != null && !polygon.getCVSeq().isNull())
            drawPolygon(polygon, color, thickness);
        }
    }
    /**
     * Draws a rectangle
     * @param x the top left corner x coord
     * @param y the top left corner y coord
     * @param width the width of the rectangle
     * @param height the height of the rectangle
     * @param color the desired WPIColor
     * @param thickness the thickness in pixels
     */
    public void drawRect(int x, int y, int width, int height, WPIColor color, int thickness){
        cvDrawRect(image, cvPoint(x, y),cvPoint(x+width, y+height), color.toCvScalar(), thickness, 8, 0);
    }

    private void generateChannels() {
        if (red == null) {
            IplImage redChannel = IplImage.create(image.cvSize(), 8, 1);
            IplImage greenChannel = IplImage.create(image.cvSize(), 8, 1);
            IplImage blueChannel = IplImage.create(image.cvSize(), 8, 1);
            cvSplit(image, blueChannel, greenChannel, redChannel, null);
            red = new WPIGrayscaleImage(redChannel);
            blue = new WPIGrayscaleImage(blueChannel);
            green = new WPIGrayscaleImage(greenChannel);
        }
    }

    /**
     * Gets the red channel from the color image
     * @return a WpiGrayscaleImage that represents the red channel of the WPIColor image
     */
    public WPIGrayscaleImage getRedChannel() {
        generateChannels();
        return red;
    }

    /**
     * Gets the blue channel from the color image
     * @return a WpiGrayscaleImage that represents the blue channel of the WPIColor image
     */
    public WPIGrayscaleImage getBlueChannel() {
        generateChannels();
        return blue;
    }

    /**
     * Gets the green channel from the color image
     * @return a WpiGrayscaleImage that represents the green channel of the WPIColor image
     */

    public WPIGrayscaleImage getGreenChannel() {
        generateChannels();
        return green;
    }
}
