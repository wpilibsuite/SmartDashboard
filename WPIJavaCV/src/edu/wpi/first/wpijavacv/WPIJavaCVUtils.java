/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpijavacv;

import static com.googlecode.javacv.cpp.opencv_highgui.*;

/**
 *  A class with utility functions. Use this instead of Thread.sleep()
 * @author Greg Granito
 */
public class WPIJavaCVUtils {

    /**
     * Waits for the specified key to be pressed
     * @param key the key (case sensitive)
     */
    public static void waitForKey(char key){
        while(cvWaitKey() != key);
    }

    /**
     * waits until any key has been pressed
     * @return the key that was pressed
     */
    public static char waitForAnyKey(){
        return (char)cvWaitKey();
    }

    /**
     * Waits until the timeout or until a key is pressed
     * @param key the case sensitive key
     * @param timeoutMillis the timeout
     * @return returns whether the key was pressed (false if the timeout occurs)
     */
    public static boolean keyIsPressed(char key, int timeoutMillis){
        return cvWaitKey(timeoutMillis) == key;
    }
}
