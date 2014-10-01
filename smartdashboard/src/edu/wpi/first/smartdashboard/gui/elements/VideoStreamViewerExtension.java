package edu.wpi.first.smartdashboard.gui.elements;

import edu.wpi.first.smartdashboard.gui.DashboardPrefs;
import edu.wpi.first.smartdashboard.gui.StaticWidget;
import edu.wpi.first.smartdashboard.properties.IPAddressProperty;
import edu.wpi.first.smartdashboard.properties.Property;
import edu.wpi.first.smartdashboard.properties.StringProperty;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

/**
 *
 * @author Greg Granito
 */
public class VideoStreamViewerExtension extends StaticWidget {

    public static final String NAME = "Simple Camera Viewer";
    

    private static final int[] START_BYTES = new int[]{0xFF, 0xD8};
    private static final int[] END_BYTES = new int[]{0xFF, 0xD9};
    
    private boolean ipChanged = true;
    private String ipString = null;
    private long lastFPSCheck = 0;
    private int lastFPS = 0;
    private int fpsCounter = 0;
    public class BGThread extends Thread {

        boolean destroyed = false;

        public BGThread() {
            super("Camera Viewer Background");
        }

        long lastRepaint = 0;
        @Override
        public void run() {
            URLConnection connection = null;
            InputStream stream = null;
            ByteArrayOutputStream imageBuffer = new ByteArrayOutputStream();
            while (!destroyed) {
                try{
                    System.out.println("Connecting to camera");
                    ipChanged = false;
                    URL url = new URL("http://"+ipString+"/mjpg/video.mjpg");
                    connection = url.openConnection();
                    connection.setReadTimeout(250);
                    stream = connection.getInputStream();
                    
                    while(!destroyed && !ipChanged){
                        while(System.currentTimeMillis()-lastRepaint<10){
                            stream.skip(stream.available());
                            Thread.sleep(1);
                        }
                        stream.skip(stream.available());
                        
                        imageBuffer.reset();
                        for(int i = 0; i<START_BYTES.length;){
                            int b = stream.read();
                            if(b==START_BYTES[i])
                                i++;
                            else
                                i = 0;
                        }
                        for(int i = 0; i<START_BYTES.length;++i)
                            imageBuffer.write(START_BYTES[i]);
                        
                        for(int i = 0; i<END_BYTES.length;){
                            int b = stream.read();
                            imageBuffer.write(b);
                            if(b==END_BYTES[i])
                                i++;
                            else
                                i = 0;
                        }
                        
                        fpsCounter++;
                        if(System.currentTimeMillis()-lastFPSCheck>500){
                            lastFPSCheck = System.currentTimeMillis();
                            lastFPS = fpsCounter*2;
                            fpsCounter = 0;
                        }
                        
                        lastRepaint = System.currentTimeMillis();
                        ByteArrayInputStream tmpStream = new ByteArrayInputStream(imageBuffer.toByteArray());
                        imageToDraw = ImageIO.read(tmpStream);
                        System.out.println(System.currentTimeMillis()-lastRepaint);
                        repaint();
                    }
                
                } catch(Exception e){
                    imageToDraw = null;
                    repaint();
                    e.printStackTrace();
                }
                
                if(!ipChanged){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException ex) {}
                }
            }

        }

        @Override
        public void destroy() {
            destroyed = true;
        }
    }
    private BufferedImage imageToDraw;
    private BGThread bgThread = new BGThread();
    private final int team = DashboardPrefs.getInstance().team.getValue();
    public final IPAddressProperty ipProperty = new IPAddressProperty(this, "Camera IP Address", new int[]{10, (DashboardPrefs.getInstance().team.getValue() / 100), (DashboardPrefs.getInstance().team.getValue() % 100), 11});

    @Override
    public void init() {
        setPreferredSize(new Dimension(100, 100));
        ipString = ipProperty.getSaveValue();
        bgThread.start();
        revalidate();
        repaint();
    }

    @Override
    public void propertyChanged(Property property) {
        if (property == ipProperty) {
            ipString = ipProperty.getSaveValue();
            ipChanged = true;
        }

    }

    @Override
    public void disconnect() {
        bgThread.destroy();
        super.disconnect();
    }

    @Override
    protected void paintComponent(Graphics g) {
        BufferedImage drawnImage = imageToDraw;
        if (drawnImage != null) {
            int width = getBounds().width;
            int height = getBounds().height;
            double scale = Math.min((double) width / (double) drawnImage.getWidth(), (double) height / (double) drawnImage.getHeight());
            g.drawImage(drawnImage, (int) (width - (scale * drawnImage.getWidth())) / 2, (int) (height - (scale * drawnImage.getHeight())) / 2,
                    (int) ((width + scale * drawnImage.getWidth()) / 2), (int) (height + scale * drawnImage.getHeight()) / 2,
                    0, 0, drawnImage.getWidth(), drawnImage.getHeight(), null);
            g.setColor(Color.PINK);
            g.drawString("FPS: "+lastFPS, 10, 10);
        } else {
            g.setColor(Color.PINK);
            g.fillRect(0, 0, getBounds().width, getBounds().height);
            g.setColor(Color.BLACK);
            g.drawString("NO CONNECTION", 10, 10);
        }
    }
}
