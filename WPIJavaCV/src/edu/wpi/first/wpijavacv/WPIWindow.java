/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpijavacv;

import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 * A class that can be used for displaying images
 * @author Greg Granito
 */
public class WPIWindow {

    private static int count = 0;

    private String name;

    /**
     * Creates a new window with a default name that will be in the format
     * "Window " + windowNumber
     */
    public WPIWindow() {
        count++;

        name = "Window " +count;
        cvNamedWindow(name);

    }

    /**
     * Creates a new window with the specified name must be unique
     * (including the windows named using the default constructor)
     * @param name the desired name
     */
    public WPIWindow(String name) {
        this.name = name;
    }


   /**
    * Shows the specified image, must be the same resolution and depth
    * as the first image the window shows
    * @param image the image
    */
    public void showImage(WPIImage image) {

        if(image != null)
        cvShowImage(name, image.image);
        else cvShowImage(name, null);
    }
}
