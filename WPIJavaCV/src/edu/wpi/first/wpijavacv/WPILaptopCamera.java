/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpijavacv;

import static com.googlecode.javacv.cpp.opencv_highgui.*;
/**
 * A class used to gather images from cameras connected to the laptop
 * @author Greg
 */
public class WPILaptopCamera extends WPIDisposable {
    CvCapture cam;

    public WPILaptopCamera() {
        cam = cvCreateCameraCapture(0);
    }

    public WPIColorImage getCurrentFrame(){
        return new WPIColorImage(cvQueryFrame(cam));
    }

    @Override
    protected void disposed() {
    }

}
