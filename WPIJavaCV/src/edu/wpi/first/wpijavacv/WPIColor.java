/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.wpi.first.wpijavacv;

import java.awt.Color;
import static com.googlecode.javacv.cpp.opencv_core.*;
/**
 * A class of colors used for drawing function
 * @author Greg Granito
 */
public class WPIColor {
    public static final WPIColor BLACK = new WPIColor(CvScalar.BLACK, Color.BLACK);
    public static final WPIColor BLUE = new WPIColor(CvScalar.BLUE, Color.BLUE);
    public static final WPIColor CYAN = new WPIColor(CvScalar.CYAN, Color.CYAN);
    public static final WPIColor GRAY = new WPIColor(CvScalar.GRAY, Color.GRAY);
    public static final WPIColor GREEN = new WPIColor(CvScalar.GREEN, Color.GREEN);
    public static final WPIColor MAGENTA = new WPIColor(CvScalar.MAGENTA, Color.MAGENTA);
    public static final WPIColor ONE = new WPIColor(CvScalar.ONE);
    public static final WPIColor ONEHALF = new WPIColor(CvScalar.ONEHALF);
    public static final WPIColor RED = new WPIColor(CvScalar.RED, Color.RED);
    public static final WPIColor WHITE = new WPIColor(CvScalar.WHITE, Color.WHITE);
    public static final WPIColor YELLOW = new WPIColor(CvScalar.YELLOW, Color.YELLOW);
    public static final WPIColor ZERO = new WPIColor(CvScalar.ZERO);

    private final CvScalar scalar;
    private Color color;

    WPIColor(CvScalar scalar) {
        this.scalar = scalar;
    }

    WPIColor(CvScalar scalar, Color color) {
        this(scalar);
        this.color = color;
    }

    /**
     * Creates a new WPIColor with the specified rgb values
     * @param red red value, 0 - 255
     * @param green green value, 0 - 255
     * @param blue blue value, 0 - 255
     */
    public WPIColor(int red, int green, int blue){
        this(CV_RGB(red, green, blue));
    }

    public WPIColor(Color color) {
        this(CV_RGB(color.getRed(), color.getGreen(), color.getBlue()), color);
    }

    CvScalar toCvScalar(){
        return scalar;
    }

    public Color toColor() {
        if (color == null) {
            color = new Color((int) scalar.red(), (int) scalar.green(), (int) scalar.blue());
        }
        return color;
    }
}
