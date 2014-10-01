/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpijavacv;

import com.googlecode.javacv.cpp.opencv_core.CvPoint;

/**
 * A class representing a point
 * @author Greg Granito
 */
public class WPIPoint {

    CvPoint p;
    /**
     * Creates a new WpiPoint with the specified x and y coordinates
     * @param x the x coord
     * @param y the y coord
     */
    public WPIPoint(int x, int y) {
        p = new CvPoint(x, y);
    }

    WPIPoint(CvPoint c) {
        p = c;
    }

    /**
     *
     * @return the x coord
     */
    public int getX(){
        return p.x();
    }

    /**
     *
     * @return the y coord
     */
    public int getY(){
        return p.y();
    }

    CvPoint getCvPoint(){
        return p;
    }

    @Override
    public String toString() {
        return p.toString();
    }


}
