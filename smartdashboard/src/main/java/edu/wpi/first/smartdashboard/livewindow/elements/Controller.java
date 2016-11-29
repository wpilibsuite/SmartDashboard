/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.smartdashboard.livewindow.elements;

/**
 * Used for all controllers.
 * @author Sam
 */
public interface Controller {
    
    /**
     * Resets this Controller. This is called when exiting LiveWindow mode or
     * when the "Reset" button is pressed. It's important to reset Controllers
     * when exiting the LiveWindow because we don't want to keep motors etc.
     * running when we can't control them.
     */
    public void reset();
}
