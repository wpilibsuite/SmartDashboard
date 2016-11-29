package edu.wpi.first.smartdashboard.net;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.imageio.ImageIO;

/**
 * Fetches images from the robot's PCVideoServer
 * 
 * @author pmalmsten
 */
public class TCPImageFetcher {
    public static final int MAX_IMG_SIZE_BYTES = 500000;
    public static final int READ_TIMEOUT_MS = 3000;
    public static final int VIDEO_TO_PC_PORT = 1180;
    private Socket m_sock = null;
    private InputStream m_sockistream = null;
    private byte[] m_imgBuffer = null;
    private int m_maxImgBufferSize = 0;
    private ByteArrayInputStream m_baistream = null;
    private DataInputStream m_daistream = null;
    private boolean m_initialized = false;
    private byte[] m_address = null;

    /**
     * Creates a new TCPImageFetcher which will attempt to read from the
     * given team's robot
     *
     * @param teamNumber The team number to use
     * @throws UnknownHostException
     * @throws IOException
     */
    public TCPImageFetcher(int teamNumber)  {
        byte high = (byte) (teamNumber / 100);
        byte low = (byte) (teamNumber % 100);
        m_address = new byte[] {10, high, low, 2};
    }

    /**
     * Initializes a TCP connection
     * 
     * @param addr The address of the remote device
     * @param port The port to connect to
     * @throws IOException
     */
    private void init() throws IOException {
        m_sock = new Socket(InetAddress.getByAddress(m_address), VIDEO_TO_PC_PORT);
        m_sock.setSoTimeout(READ_TIMEOUT_MS);
        m_sockistream = m_sock.getInputStream();
        m_daistream = new DataInputStream(m_sockistream);
        m_initialized = true;
    }

    /**
     * Reads and returns an image from the associated socket connection. Blocks
     * until a valid image arrives.
     * @return The image received
     */
    public BufferedImage fetch() throws IOException {
        if(!m_initialized)
            init();

        try {
            byte[] header = new byte[4];

            while(true) {
                blockingRead(m_sockistream, header, 4);

                // Look for header 1,0,0,0
                if(!((header[0] == 1) && ((header[1] + header[2] + header[3]) == 0))) {
                    continue;
                }

                // wait for length integer (4 bytes)
                while(m_sockistream.available() < 4) {}

                // Read int length of data to follow
                int imgDataLen = m_daistream.readInt();
                //System.out.println(" Data Len: " + imgDataLen + "Hex:" + Integer.toHexString(imgDataLen));

                // Read in the expected number of bytes
                resizeBuffer(imgDataLen);
                blockingRead(m_sockistream, m_imgBuffer, imgDataLen);
                m_baistream.reset();

                // Read the image
                return ImageIO.read(m_baistream);
            }
        } catch(IOException ex) {
            m_sock.close();
            m_initialized = false;
            throw ex;
        }
    }

    /**
     * Ensures that the image buffer byte array is always an appropriate size
     * @param size Requested size for the image buffer
     */
    private void resizeBuffer(int size) {
        if(size > m_maxImgBufferSize) {
            if(size > MAX_IMG_SIZE_BYTES)
                size = MAX_IMG_SIZE_BYTES;

            m_maxImgBufferSize = size + 100;
            m_imgBuffer = new byte[m_maxImgBufferSize];
            m_baistream = new ByteArrayInputStream(m_imgBuffer);
        }
    }

    /**
     * Guarantees that the requested number of bytes are read from the given
     * input stream and are written to the given buffer before returning.
     * @param istream Stream to read from.
     * @param buf Array to write to.
     * @param requestedBytes Requested number of bytes to read and store.
     * @throws IOException
     */
    private void blockingRead(InputStream istream, byte[] buf, int requestedBytes) throws IOException {
        int offset = 0;
        while(offset < requestedBytes) {
            int read = istream.read(buf, offset, requestedBytes - offset);
                
            if(read < 0) {
                throw new IOException("Connection interrupted");
            }
            
            offset += read;
        }
    }
}
