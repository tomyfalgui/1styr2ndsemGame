/**
 * This is the Shuriken class. The shuriken sprite is instantiated
 * and drawn in this class.
 *
 * @author Anton Gerard S. Benitez, Tomas Alejandro V. Falgui III
 * @version May 19, 2019
 */

/*
I have not discussed the Java language code
in my program with anyone other than my instructor
or the teaching assistants assigned to this course.

I have not used Java language code obtained
from another student, or any other unauthorized
source, either modified or unmodified.

If any Java language code or documentation
used in my program was obtained from another source,
such as a text book or webpage, those have been
clearly noted with a proper citation in the comments
of my code.
*/
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import javax.swing.*;

public class Shuriken extends GameObject {
    private double x,y;
    private double playerX, playerY, mouseX, mouseY;
    private int deg;

    /**
     * This is the Shuriken constructor. It instantiates the shuriken with
     * 4 arguments to calculate
     * @param playerX
     * @param playerY
     * @param mouseX
     * @param mouseY
     */
    public Shuriken(double playerX, double playerY, double mouseX, double mouseY) {
        super(playerX,playerY);

        this.playerX = super.x+32;
        this.playerY = super.y+32;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        this.x = playerX;
        this.y = playerY;
        deg = 0;
    }

    /**
     * This method draws the image of the shuriken sprite and rotates the shuriken
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        AffineTransform reset = g2d.getTransform();
        ImageIcon shurikenIcon = new ImageIcon("shuriken.png");
        Image shuriken = shurikenIcon.getImage();

        g2d.rotate(Math.toDegrees(deg), x+10, y+10);
        g2d.drawImage(shuriken, (int) x, (int) y,null);
        g2d.setTransform(reset);

    }

    /**
     * This method updates the x,y values of the shuriken
     */
    public void moveShuriken() {
        double bulletVelocity=10.0;
        double angle = Math.atan2(mouseX - playerX, mouseY - playerY);
        double xVelocity = (bulletVelocity) * Math.sin(angle);
        double yVelocity = (bulletVelocity) * Math.cos(angle);
        x = xVelocity+ x;
        y = yVelocity+ y;
        deg += 10;

    }

    /**
     * This returns x value of the shuriken
     * @return
     */
    public double getX() {
        return x;
    }

    /**
     * This returns y value of the shuriken
     * @return
     */
    public double getY(){ return y;}

}
