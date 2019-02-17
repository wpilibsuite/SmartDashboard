package edu.wpi.first.smartdashboard.gui.elements;

import javax.imageio.ImageIO;

public class ArctosLogo extends edu.wpi.first.smartdashboard.gui.elements.Image {
    @Override
    public void init() {
        super.init();
        try {
          var imgStream = ClassLoader.getSystemClassLoader().getResourceAsStream("arctos1.PNG");
          image = ImageIO.read(imgStream);
        }
        catch(Exception e) {
          System.out.println("I don't care");
        }
        revalidate();
        repaint();
    }
}