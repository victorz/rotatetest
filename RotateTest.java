import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * This class tests mouse motion listening, and showcases rotating an image.
 *
 * @version 1.0
 * @author Victor Zamanian <tt>victor.zamanian@gmail.com</tt>
 */
public class RotateTest extends JComponent {

    BufferedImage  image;       // laser cannon image
    BufferedImage  canvas;      // we paint on this image
    private JFrame frame;       // the application frame
    private int    mouseX;      // mouse X-coordinate
    private int    mouseY;      // mouse Y-coordinate
    private int    angle;       /* angle between mouse pointer and
                                 * component centre */

    /**
     * Runs a new RotateTest application.
     */
    public RotateTest() {
        /* We take care of the
         * double-buffering ourselves
         * actually, so we can comment
         * this. */
//         setDoubleBuffered(true);

        setSize(600, 400); // set size of this component
        mouseX = getWidth() / 2; // set mouse X-coordinate
        mouseY = getHeight() / 2; // set mouse Y-coordinate
        angle = 0;

        frame = new JFrame("Rotating Test");
        frame.setSize(600, 400);
        frame.add(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        image = readImage("lasercannon.png");
	canvas = new BufferedImage(getWidth(), getHeight(),
                                   BufferedImage.TYPE_INT_RGB);
        final Graphics2D gfx = canvas.createGraphics();

        gfx.setColor(Color.white);
        gfx.fillRect(0, 0, getWidth(), getHeight());
        gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        addMouseMotionListener(new MouseMotionAdapter() {
                /* when moving the mouse over this component */
                public void mouseMoved(MouseEvent e) {
                    /* update mouse pointer position */
                    mouseX = e.getX();
                    mouseY = e.getY();

                    /* recalculate angle between the vertical axis of
                     * component centre and line from component centre
                     * and mouse pointer position */
                    angle = (int) Math.toDegrees(
		            Math.atan((mouseY - getHeight() / 2)
			            / (double) (mouseX - getWidth() / 2)));

                    /* if mouse pointer is left of component centre,
                     * we have to add 180 degrees to the angle since
                     * Math.atan() only gives us values from -pi/2 to
                     * pi/2. */
                    if (mouseX - getWidth() / 2 < 0) {
                        angle += 180;
                    }

                    /* We don't need to do the following, really,
                     * because Math.toDegrees(Math.atan(double)) + 180
                     * will always fall between -90 and 270 (90 +
                     * 180), which is equal to a total of 360 */

                    /* keep angle a positive integer between 0 and 359
                     * degrees */
//                     int deg = angle;
//                     deg = deg % 360;
//                     angle = (deg < 0) ? 360 + deg : deg;

                    /* do the painting when recieving a mouse event */
                    gfx.setColor(Color.white);
                    gfx.fillRect(0, 0, getWidth(), getHeight());
                    gfx.setColor(Color.red);
                    gfx.drawLine(getWidth() / 2, getHeight() / 2,
                                 mouseX, mouseY);
                    gfx.drawImage(rotateImage(image, angle),
                                  getWidth() / 2 - 12,
                                  getHeight() / 2 - 12, null);
                    repaint();
                }});

        /* show application frame */
        frame.setVisible(true);
    }

    public synchronized void paintComponent(Graphics g) {
	g.drawImage(canvas, 0, 0, null);
    }

    /**
     * Reads an image from a file name.
     *
     * @param filename the file name from which to read the image.
     * @return a BufferedImage representing the given filename, or
     * null if reading the file failed.
     */
    private static BufferedImage readImage(String filename) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(new File(filename));
        } catch (IOException e) {
            System.err.println(e.getMessage() + " " + filename);
        } finally {
            return image;
        }
    }

    /**
     * Rotates an image by given amount of degrees.
     *
     * @param img the image to be rotated.
     * @param angle the amount of degrees by which to rotate the image.
     * @return the given image rotated by the given amount of degrees,
     * or <code>null</code> if the given image is <code>null</code>.
     */
    public static BufferedImage rotateImage(BufferedImage img, int angle) {
        /* if the image is null, we do nothing and return it,
         * since... I guess a null image rotated by angle degrees
         * would be another null image */
	if (img == null) {
	    return null;
	}

        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = dimg.createGraphics();

        /* if width is odd number of pixels, set rotate origin to
         * middle pixel, else the left of the two middle pixels */
        if (w % 2 > 0) {
            w = w / 2 + 1;
        } else {
            w /= 2;
        }

        /* if height is odd number of pixels, set rotate origin to
         * middle pixel, else the left of the two middle pixels */
        if (h % 2 > 0) {
            h = h / 2 + 1;
        } else {
            h /= 2;
        }

        /* rotate image graphics */
        g.rotate(Math.toRadians(angle), w, h);
        /* draw image one rotated image graphics */
        g.drawImage(img, null, 0, 0);
        /* return rotated image */
        return dimg;
    }

    public static void main(String args[]) {
        new RotateTest();
    }
}
