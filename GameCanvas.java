/**
 * This is the GameCanvas class. This is where the characters are being drawn.
 * This class also checks the number of lives the character has left
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
import javax.swing.*;
import java.awt.*;
import java.io.*;


public class GameCanvas extends JComponent {
    private int width, height;
    private Color bgColor;
    private Container contentPane;
    private Player c1, c2;
    private int playerID;
    private int p1lives, p2lives;
    private GraphicsEnvironment ge;
    private GameFrame.ClientSideConnection csc;

    private ImageIcon background;
    private Image bg;

    /**
     * This is the constructor of the GameCanvas class. It instantiates the 9 parameters it calls
     * and instantiates character 1 and 2
     * @param w
     * @param h
     * @param c
     * @param contentPane
     * @param csc
     * @param playerID
     * @param playerOneLives
     * @param playerTwoLives
     */
    public GameCanvas(int w, int h, Color c, Container contentPane, GameFrame.ClientSideConnection csc, int playerID, int playerOneLives, int playerTwoLives) {
        width = w;
        height = h;
        bgColor = c;
        this.playerID = playerID;
        p1lives = playerOneLives;
        p2lives = playerTwoLives;
        this.csc = csc;
        c1 = new Player(100, 383 - 32, contentPane, csc, 1, playerID, this, playerOneLives, playerTwoLives);
        c2 = new Player(1024 - 132, 383 - 32, contentPane, csc, 2, playerID, this, playerOneLives, playerTwoLives);


        this.contentPane = contentPane;


        try {
            ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("PressStart2P-Regular.ttf")));
        } catch (IOException | FontFormatException e) {
            //Handle exception
        }

        background = new ImageIcon("woodenfloor.jpg");
        bg = background.getImage();
    }

    /**
     * This method draws the canvas
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHints(rh);

        g2d.drawImage(bg, 0, 0, null);

        if (getC1Lives() > 0 && getC2Lives() > 0) {
            c1.draw(g2d);
            c2.draw(g2d);
        } else if (getC1Lives() == 0 && playerID == 1) {
            c1.loser(g2d);
            c2.loser(g2d);
        } else if (getC2Lives() == 0 && playerID == 2) {
            c2.loser(g2d);
            c1.loser(g2d);
        } else if (getC1Lives() == 0 && playerID == 2) {
            c2.winner(g2d);
            c1.winner(g2d);
        } else if (getC2Lives() == 0 && playerID == 1) {
            c1.winner(g2d);
            c2.winner(g2d);
        }


    }

    /**
     * This method passes the x,y movements of the other character to the canvas
     * @param x
     * @param y
     * @param toID
     */
    public void passOnToCharacters(int x, int y, int toID) {
        if (toID == 1) {
            c1.moveX(x);
            c1.moveY(y);
        } else {
            c2.moveX(x);
            c2.moveY(y);
        }
        contentPane.repaint();
    }

    /**
     * This method passes the x,y coordinates of the shurikens thrown
     * @param x
     * @param y
     * @param mouseX
     * @param mouseY
     * @param toID
     */
    public void passShurikenToCharacters(int x, int y, int mouseX, int mouseY, int toID) {
        if (toID == 1) {
            c1.throwShuriken(x, y, mouseX, mouseY);

        } else {
            c2.throwShuriken(x, y, mouseX, mouseY);

        }
    }

    /**
     * THis method passes the new x,y coordinates of the enemy when it blinked
     * @param blinkX
     * @param blinkY
     * @param toID
     */
    public void passBlink(int blinkX, int blinkY, int toID) {
        if (toID == 1) {
            c1.blink(blinkX, blinkY);
        } else {
            c2.blink(blinkX, blinkY);
        }
        contentPane.repaint();
    }

    /**
     * This method updates the amount of lives player 1 and 2 has.
     * @param playerOneLives
     * @param playerTwoLives
     */
    public void updateLife(int playerOneLives, int playerTwoLives) {

        p1lives = playerOneLives;
        p2lives = playerTwoLives;
        c1.updatePlayerOneLives(playerOneLives);
        c1.updatePlayerTwoLives(playerTwoLives);
        c2.updatePlayerOneLives(playerOneLives);
        c2.updatePlayerTwoLives(playerTwoLives);


    }

    /**
     * returns x value of character 1
     * @return
     */
    public int getC1x() {
        return c1.getX();
    }

    /**
     * returns y value of character 1
     * @return
     */
    public int getC1y() {
        return c1.getY();
    }

    /**
     * returns x value of character 2
     * @return
     */
    public int getC2x() {
        return c2.getX();
    }

    /**
     * returns y value of character 2
     * @return
     */
    public int getC2y() {
        return c2.getY();
    }

    /**
     * retuns amount of lives p1 has
     * @return
     */
    public int getC1Lives() {
        return p1lives;
    }

    /**
     * returns amount of lives p2 has
     * @return
     */
    public int getC2Lives() {
        return p2lives;
    }
}