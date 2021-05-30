/**
 * This is the Player class. It contains the character sprite. It controls the movement of
 * the character –– blink, move –– and instantiates the shuriken throw done by the character.
 * This class also contains the losing and winning screen of the character.
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
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.Timer;



public class Player extends GameObject implements KeyListener {
    private int x;
    private int y;
    private JPanel contentPane;
    private static GameFrame.ClientSideConnection csc;


    private int designatedPlayer;
    private int playerID;

    private ArrayList<Shuriken> shurikens;

    private Runnable shurikenThrow;
    private int blinkToX;
    private int blinkToY;
    private boolean blinkAvailable;
    private Timer blinkCD;
    private GameCanvas canvas;
    private ImageIcon heartIcon;
    private Image heart;
    private int space;
    private int playerOneLives, playerTwoLives;


    //ninja head
    private ImageIcon ninjaHead;
    private Image head;

    // keys
    private ArrayList<Integer> keysClicked;

    private boolean shurikenAvailable;
    private Timer shurikenCD;

    /**
     * This is the constructor class. It instantiates 9 parameters, 3 ArrayLists, 1 thread, and 1 timer.
     * It also instantiates the loadImage() method for the heart lives.
     * @param x
     * @param y
     * @param contentPane
     * @param csc
     * @param designatedPlayer
     * @param playerID
     * @param canvas
     * @param playerOneLives
     * @param playerTwoLives
     */
    public Player(int x, int y, Container contentPane, GameFrame.ClientSideConnection csc, int designatedPlayer, int playerID, GameCanvas canvas, int playerOneLives, int playerTwoLives) {
        super(x,y);
        this.x = (int) super.x;
        this.y = (int) super.y;
        this.contentPane = (JPanel) contentPane;
        this.csc = csc;
        this.designatedPlayer = designatedPlayer;
        this.playerID = playerID;
        this.playerOneLives = playerOneLives;
        this.playerTwoLives = playerTwoLives;
        this.canvas = canvas;


        shurikens = new ArrayList<Shuriken>();
        shurikenAvailable = true;
        shurikenCD = new Timer(500, new ShurikenCoolDown());

        shurikenThrow = new ShurikenThrow();
        Thread st = new Thread(shurikenThrow);
        st.start();

        blinkAvailable = true;
        blinkCD = new Timer(5000, new BlinkCoolDown());
        keysClicked = new ArrayList<Integer>();
        contentPane.setFocusable(true);
        contentPane.requestFocus();
        contentPane.addKeyListener(this);

        loadImage();
    }

    /**
     * This method draws the character, the shuriken, and the lives.
     * @param g2d
     */
    public void draw(Graphics2D g2d) {
        space = 50;
        if (blinkAvailable) {
            if (playerID == designatedPlayer) {
                ninjaHeadSpriteControl(1, "Blue");
            } else {
                ninjaHeadSpriteControl(1, "Red");
            }
        } else {
            if (playerID == designatedPlayer) {
                ninjaHeadSpriteControl(2, "Blue");
            } else {
                ninjaHeadSpriteControl(2, "Red");
            }
        }

        g2d.drawImage(head, x, y, null);

        for (Iterator<Shuriken> shurikenIterator = shurikens.iterator(); shurikenIterator.hasNext(); ) {
            Shuriken shuriken = shurikenIterator.next();

            shuriken.draw(g2d);
            if (shuriken.getY() > 768 || shuriken.getY() < 0 || shuriken.getX() > 1024 || shuriken.getX() < 0) {
                try {
                    shurikenIterator.remove();
                } catch (java.util.ConcurrentModificationException exception) {
                }

            }

            if (playerID != 1 && designatedPlayer != 1) {
                int p1Distance = (int) Math.sqrt(
                        Math.pow(
                                ((shuriken.getX() + 10) - (canvas.getC1x() + 25)), 2)
                                +
                                Math.pow(
                                        ((shuriken.getY() + 10) - (canvas.getC1y() + 25)), 2)
                );

                if (p1Distance < 42) {
                    System.out.println("Hit p1");
                    csc.subtractPlayerOneLives();
                    shurikenIterator.remove();
                }
            } else if (playerID != 2 && designatedPlayer != 2) {
                int p2Distance = (int) Math.sqrt(
                        Math.pow(
                                ((shuriken.getX() + 10) - (canvas.getC2x() + 25)), 2)
                                +
                                Math.pow(
                                        ((shuriken.getY() + 10) - (canvas.getC2y() + 25)), 2)
                );

                if (p2Distance < 42) {
                    System.out.println("Hit p2");
                    csc.subtractPlayerTwoLives();
                    shurikenIterator.remove();
                }
            } else if (playerID == 1 && designatedPlayer == 2) {
                int p2Distance = (int) Math.sqrt(
                        Math.pow(
                                ((shuriken.getX() + 10) - (canvas.getC1x() + 25)), 2)
                                +
                                Math.pow(
                                        ((shuriken.getY() + 10) - (canvas.getC1y() + 25)), 2)
                );

                if (p2Distance < 42) {
                    System.out.println("Hit p2 display");
                    shurikenIterator.remove();
                }
            } else if (playerID == 2 && designatedPlayer == 1) {
                int p1Distance = (int) Math.sqrt(
                        Math.pow(
                                ((shuriken.getX() + 10) - (canvas.getC2x() + 25)), 2)
                                +
                                Math.pow(
                                        ((shuriken.getY() + 10) - (canvas.getC2y() + 25)), 2)
                );

                if (p1Distance < 42) {
                    System.out.println("Hit p1 display");
                    shurikenIterator.remove();
                }
            }
        }


        g2d.setFont(new Font("Press Start 2P Regular", Font.PLAIN, 13));
        g2d.setColor(Color.RED);
        g2d.drawString("Lives", 50, 45);


        if (playerID == 2 && designatedPlayer != playerID) {
            for (int i = 0; i < playerTwoLives; i++) {
                g2d.drawImage(heart, space, 50, null);
                space += 60;
            }
        }

        if (playerID == 1 && designatedPlayer != playerID) {
            for (int i = 0; i < playerOneLives; i++) {
                g2d.drawImage(heart, space, 50, null);
                space += 60;
            }
        }
    }

    /**
     * This method is called if the player loses. It displays a "YOU LOST!" image.
     * @param g2d
     */
    public void loser(Graphics2D g2d) {
        ImageIcon bg = new ImageIcon("losingScreen.jpg");
        Image bgbg = bg.getImage();
        g2d.drawImage(bgbg, 0, 0, null);

    }

    /**
     * This method is called if the player wins. It displays a "YOU WON!" image.
     * @param g2d
     */
    public void winner(Graphics2D g2d) {
        ImageIcon bg = new ImageIcon("winningScreen.jpg");
        Image bgbg = bg.getImage();
        g2d.drawImage(bgbg, 0, 0, null);
    }

    /**
     * This method moves the x position of the player
     * @param x
     */
    public void moveX(int x) {
        this.x += x;

        if (this.x <= 0) {
            this.x = 0;
        } else if (this.x + 30 >= 1024) {
            this.x = 1024 - 30;
        }
    }

    /**
     * This method moves the y position of the player
     * @param y
     */
    public void moveY(int y) {
        this.y += y;

        if (this.y <= 0) {
            this.y = 0;
        } else if (this.y + 30 >= 768) {
            this.y = 768 - 30;
        }
    }

    /**
     * This updates the amount of lives that player one has
     * @param newFirstLife
     */
    public void updatePlayerOneLives(int newFirstLife) {
        playerOneLives = newFirstLife;
    }

    /**
     * This updates the amount of lives that player two has
     * @param newFirstLife
     */
    public void updatePlayerTwoLives(int newFirstLife) {
        playerTwoLives = newFirstLife;
    }

    /**
     * This method checks which keys are pressed in order to send
     * corresponding movement values
     */
    public void movePlayer() {
        if (playerID == designatedPlayer) {
            if (keysClicked.contains(Integer.valueOf(KeyEvent.VK_W))) {
                moveY(-15);
                csc.sendMovement(0, -15, playerID);
                contentPane.repaint();
            }
            if (keysClicked.contains(Integer.valueOf(KeyEvent.VK_S))) {
                moveY(15);
                csc.sendMovement(0, 15, playerID);
                contentPane.repaint();
            }
            if (keysClicked.contains(Integer.valueOf(KeyEvent.VK_A))) {
                moveX(-15);
                csc.sendMovement(-15, 0, playerID);
                contentPane.repaint();
            }
            if (keysClicked.contains(Integer.valueOf(KeyEvent.VK_D))) {
                moveX(15);
                csc.sendMovement(15, 0, playerID);
                contentPane.repaint();
            }
            if (keysClicked.contains(Integer.valueOf(KeyEvent.VK_SPACE))) {
                if (blinkAvailable) {
                    blink(blinkToX, blinkToY);
                    csc.sendBlink(blinkToX, blinkToY, playerID);
                    contentPane.repaint();
                }
            }
            contentPane.repaint();
        }
    }


    /**
     * This method returns the current x value of the player
     * @return
     */
    public int getX() {
        return this.x;
    }

    /**
     * This method returns the current y value of the player
     * @return
     */
    public int getY() {
        return this.y;
    }


    /**
     * These methods check if the keys have been pressed or released
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {
        if (!keysClicked.contains(Integer.valueOf(e.getKeyCode()))) {
            keysClicked.add(Integer.valueOf(e.getKeyCode()));
        }
        movePlayer();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keysClicked.remove(Integer.valueOf(e.getKeyCode()));
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    /**
     * This class is called once the blink timer is done meaning
     * you can use the blink action again
     */
    private class BlinkCoolDown implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            blinkAvailable = true;
            blinkCD.stop();
        }
    }

    /**
     * This method teleports the player to wherever the mouse is. It also starts a
     * thread for the BlinkCoolDown
     * @param blinkX
     * @param blinkY
     */
    public void blink(int blinkX, int blinkY) {
        x = blinkX;
        y = blinkY;
        blinkAvailable = false;
        blinkCD.start();
        contentPane.repaint();
    }

    /**
     * This class is called once the shuriken timer is done meaning
     * you can throw shurikens again
     */
    private class ShurikenCoolDown implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            shurikenAvailable = true;
            shurikenCD.stop();
        }
    }

    /**
     * This method instantiates a new shuriken and it adds it to the
     * shuriken ArrayList.
     * @param x
     * @param y
     * @param mouseX
     * @param mouseY
     */
    public void throwShuriken(int x, int y, int mouseX, int mouseY) {
        Shuriken newShuriken = new Shuriken(x + 32, y + 32, mouseX, mouseY);

        Timer newTimer = new Timer(5, new Baby(newShuriken));
        shurikens.add(newShuriken);
        newTimer.start();
        shurikenAvailable = false;
        shurikenCD.start();
        contentPane.repaint();


    }


    /**
     * This method moves the shuriken and repaints the canvas to show the animation of the shuriken
     */
    class Baby implements ActionListener {

        Shuriken shuriken;

        public Baby(Shuriken s) {

            shuriken = s;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            shuriken.moveShuriken();
            contentPane.repaint();

        }
    }


    /**
     * This checks for the mouse position when blinking. It also calls the ThrowShuriken method
     * when the mouse is pressed.
     */
    private class ShurikenThrow implements MouseMotionListener, MouseListener, Runnable {
        @Override
        public void mouseMoved(MouseEvent e) {
            blinkToX = e.getX();
            blinkToY = e.getY();
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            //mouseY = e.getY();
            //mouseX = e.getX();
        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (playerID == designatedPlayer) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if(shurikenAvailable){
                        int mouseX = e.getX();
                        int mouseY = e.getY();
                        throwShuriken((int) x, (int) y, mouseX, mouseY);
                        csc.sendShuriken((int) x, (int) y, mouseX, mouseY, playerID);
                    }
                }

            }

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseClicked(MouseEvent e) {


        }


        public void run() {
            contentPane.addMouseListener(this);
            contentPane.addMouseMotionListener(this);
        }

    }


    /**
     * This loads the heart image
     */
    public void loadImage() {
        heartIcon = new ImageIcon("Heart.png");
        heart = heartIcon.getImage();
    }

    /**
     * This loads the player image. It controls if your character is blue or red. It also
     * changes the image when the player blinks
     * @param n
     * @param color
     */
    public void ninjaHeadSpriteControl(int n, String color) {
        String src = color + "NormalNinja" + n + ".png";
        ninjaHead = new ImageIcon(src);
        head = ninjaHead.getImage();
    }

}
