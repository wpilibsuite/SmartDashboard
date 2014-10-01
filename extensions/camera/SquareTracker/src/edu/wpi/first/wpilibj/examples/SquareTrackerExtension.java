package edu.wpi.first.wpilibj.examples;

import edu.wpi.first.smartdashboard.camera.WPILaptopCameraExtension;
import edu.wpi.first.smartdashboard.robot.Robot;
import edu.wpi.first.wpijavacv.WPIBinaryImage;
import edu.wpi.first.wpijavacv.WPIColor;
import edu.wpi.first.wpijavacv.WPIColorImage;
import edu.wpi.first.wpijavacv.WPIContour;
import edu.wpi.first.wpijavacv.WPIImage;
import edu.wpi.first.wpijavacv.WPIPoint;
import edu.wpi.first.wpijavacv.WPIPolygon;
import edu.wpi.first.wpilibj.networking.NetworkTable;
import java.util.ArrayList;

/**
 *
 * @author Greg Granito
 */
public class SquareTrackerExtension extends WPILaptopCameraExtension {
    public static final String NAME = "Laptop Camera Square Tracker";

    NetworkTable table = NetworkTable.getTable("camera");
    WPIColor targetColor = new WPIColor(255, 0, 0);

    @Override
    public WPIImage processImage(WPIColorImage rawImage) {

        WPIBinaryImage blueBin = rawImage.getBlueChannel().getThresholdInverted(60);
        WPIBinaryImage greenBin = rawImage.getGreenChannel().getThresholdInverted(60);
        WPIBinaryImage redBin = rawImage.getRedChannel().getThresholdInverted(60);

        WPIBinaryImage finalBin = blueBin.getAnd(redBin).getAnd(greenBin);

        finalBin.erode(2);
        finalBin.dilate(6);

        WPIContour[] contours = finalBin.findContours();

        ArrayList<WPIPolygon> polygons = new ArrayList<WPIPolygon>();

        for(WPIContour c : contours){
            double ratio = ((double)c.getHeight()) / ((double)c.getWidth());
            if(ratio < 1.5 && ratio> 0.75){
                polygons.add(c.approxPolygon(45));
            }
        }

        ArrayList<WPIPolygon> possiblePolygons = new ArrayList<WPIPolygon>();

        for(WPIPolygon p : polygons){
            if(p.isConvex() && p.getNumVertices() == 4){
                possiblePolygons.add(p);
            }else{
                rawImage.drawPolygon(p, WPIColor.CYAN, 5);
            }
        }

        WPIPolygon square = null;
        int squareArea = 0;

        for(WPIPolygon p : possiblePolygons){
            rawImage.drawPolygon(p, WPIColor.GREEN, 5);
            for(WPIPolygon q : possiblePolygons){
                if(p == q) continue;

               int pCenterX = (p.getX() + (p.getWidth()/2));
               
               int qCenterX = q.getX() + (q.getWidth()/2);
               
               int pCenterY = (p.getY() + (p.getHeight()/2));

               int qCenterY = q.getY() + (q.getHeight()/2);

               rawImage.drawPoint(new WPIPoint(pCenterX, pCenterY), targetColor, 5);
               rawImage.drawPoint(new WPIPoint(qCenterX, qCenterY), targetColor, 5);

                if(Math.abs(pCenterX - qCenterX) < 20 &&
                    Math.abs(pCenterY - qCenterY) < 20){
                    int pArea = Math.abs(p.getArea());
                    int qArea = Math.abs(q.getArea());
                    if(pArea > qArea){
                        square = p;
                        squareArea = pArea;
                    }else{
                        square = q;
                        squareArea = qArea;
                    }
                    break;
                }
            }
        }

        if(square != null){
            double x = square.getX() + (square.getWidth()/2);
            x = (2 * (x/rawImage.getWidth())) - 1;
            
            double area = ((double)squareArea) /  ((double)(rawImage.getWidth() * rawImage.getHeight()));
            
            synchronized(table) {
            table.beginTransaction();
                table.putBoolean("found", true);
                table.putDouble("x", x);
                table.putDouble("area", area);
            table.endTransaction();
            }

            Robot.getTable().putBoolean("found", true);
            Robot.getTable().putDouble("X", x);
            Robot.getTable().putDouble("Area", area);
            rawImage.drawPolygon(square, targetColor, 7);
        }else{
            table.putBoolean("found", false);
            Robot.getTable().putBoolean("found", false);
        }

        return rawImage;
    }
}
