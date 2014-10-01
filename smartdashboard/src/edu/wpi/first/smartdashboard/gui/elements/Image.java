package edu.wpi.first.smartdashboard.gui.elements;

import java.awt.*;
import java.awt.image.*;
import java.io.*;

import javax.imageio.*;
import javax.swing.*;

import edu.wpi.first.smartdashboard.gui.*;
import edu.wpi.first.smartdashboard.properties.*;

/**
 *
 * @author Jeff Copeland
 */
public class Image extends StaticWidget {

    public final FileProperty img = new FileProperty(this, "Image File");
    public final BooleanProperty aspectKept = new BooleanProperty(this, "Maintain Aspect Ratio", false);
    private BufferedImage image;

    public Image() {
        setObstruction(false);
    }

    @Override
    public void init() {
        update(img, ".");
        img.addExtensionFilter("JPEG", ".jpg");
        img.addExtensionFilter("GIF", ".gif");
        img.addExtensionFilter("PNG", ".png");
        img.addExtensionFilter("Bitmap", ".bmp");
        img.addExtensionFilter("JPEG", ".jpeg");
        if (image == null) {
            setPreferredSize(new Dimension(100, 100));
        }
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == img) {
            try {
                image = ImageIO.read(new File(img.getValue()));
                setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
            } catch (IOException e) {
                image = null;
                JOptionPane.showMessageDialog(this, "Invalid File Type.", "Input Error", JOptionPane.WARNING_MESSAGE);
                setPreferredSize(new Dimension(100, 100));
            }
            revalidate();
            repaint();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        if (image == null) {
            g.fillRect(0, 0, getWidth(), getHeight());
        } else {
            if (aspectKept.getValue()) {
                int width = getBounds().width;
                int height = getBounds().height;
                double scale = Math.min((double) width / (double) image.getWidth(), (double) height / (double) image.getHeight());
                g.drawImage(image, (int) (width - (scale * image.getWidth())) / 2, (int) (height - (scale * image.getHeight())) / 2,
                        (int) ((width + scale * image.getWidth()) / 2), (int) (height + scale * image.getHeight()) / 2,
                        0, 0, image.getWidth(), image.getHeight(), null);
            } else {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), 0, 0, image.getWidth(), image.getHeight(), null);
            }
        }
    }
}
