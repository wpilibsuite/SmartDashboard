package edu.wpi.first.wpijavacv;

/**
 * A class used to gather images from the robot's camera.
 * @author Joe Grinstead and Greg Granito
 */
public class WPICamera extends WPIFFmpegVideo {

    private static final int DEFAULT_ENDING_IP = 11;

    public WPICamera(String loginName, String password, int team) {
        this(loginName + ":" + password + "@10." + (team / 100) + "." + (team % 100) + "." + DEFAULT_ENDING_IP);
    }

    public WPICamera(int team) {
        this("10." + (team / 100) + "." + (team % 100) + "." + DEFAULT_ENDING_IP);
    }

    public WPICamera(String loginName, String password, String ip) {
        this(loginName + ":" + password + "@" + ip);
    }

    public WPICamera(String ip) {
        super("http://" + ip + "/mjpg/video.mjpg");
    }
}
