/**
 * This is the GameFrame class. This receives and sends data to the server.
 * It also contains the start screen and adds the canvas to the container
 * once initialized
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
import java.io.*;

import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.*;
import java.net.*;

public class GameFrame extends JFrame {
    private int width;
    private int height;
    private Container contentPane;
    private JTextField textField;
    private JButton join;
    private ClientSideConnection csc;
    private GameCanvas gameCanvas;

    // game
    private int playerID;
    private int otherPlayer;

    private CustomPanel customPanel;

    private int playerOneLives, playerTwoLives;


    /**
     * This constructor instantiates the contentPane, as well as gets the
     * width and height in the parameter.
     * @param w
     * @param h
     */
    public GameFrame(int w, int h) {
        width = w;
        height = h;
        contentPane = this.getContentPane();
        textField = new JTextField(20);
        join = new JButton("Join GameServer");

    }

    /**
     * This method sets up the GUI. It initially outputs the start screen
     */
    public void setUpGUI() {
        this.setSize(width, height);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //contentPane.setLayout(new GridLayout(3,1));
        JPanel panel = new JPanel(new GridBagLayout());
        JPanel panel2 = new JPanel(new GridLayout(2, 1));

        GridBagConstraints gridBagConstraints = new GridBagConstraints();

        customPanel = new CustomPanel();

        ///*
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 3;
        gridBagConstraints.weighty = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        //*/
        panel.add(customPanel, gridBagConstraints);


        panel2.add(textField);
        panel2.add(join);

        ///*
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.weightx = 1;
        gridBagConstraints.weighty = 1;
        gridBagConstraints.fill = GridBagConstraints.CENTER;
        //*/

        panel.add(panel2, gridBagConstraints);

        contentPane.add(panel, BorderLayout.CENTER);
        join.addActionListener(new ButtonClick());
//        pack();
        this.setVisible(true);
    }

    /**
     * This class draws an image in a JPanel. This allows the image to be
     * outputted on the setupGUI
     */
    private class CustomPanel extends JPanel {
        private BufferedImage cover;

        public CustomPanel() {
            try {
                cover = ImageIO.read(new FileInputStream("cover.jpg"));
            } catch (IOException ex) {
                System.out.println("IOException at CustomPanel constructor");
            }
        }

        @Override
        public Dimension getPreferredSize() {
            return cover == null ? new Dimension(400, 300) : new Dimension(cover.getWidth(), cover.getHeight());
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(cover, 0, 0, this);
        }

    }

    /**
     * This class instantiates the csc. It also calls the setupListenrs method
     */
    private class ButtonClick implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            csc = new ClientSideConnection(textField.getText());
            setUpListeners();
            join.setEnabled(false);
        }
    }

    /**
     * This method starts a thread that runs setUpMovementListeners
     */
    public void setUpListeners() {

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (csc.receiveOpenCommand() == 1) {
                    setUpMovementListeners();
                }
            }
        });
        t.start();
    }

    /**
     * This method captures the values that needs to be passed to the server
     */
    public void setUpMovementListeners() {
        Thread listeningToMovement = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    int[] moves = csc.receiveMovement();
                    int x = moves[0];
                    int y = moves[1];
                    int mouseX = moves[2];
                    int mouseY = moves[3];
                    int blinkX = moves[4];
                    int blinkY = moves[5];
                    int palayerID = moves[6];
                    int playerOneLives = moves[7];
                    int playerTwoLives = moves[8];


                    if (mouseX == -1 && mouseY == -1 && x == -1 && y == -1 && playerOneLives == -1 && playerTwoLives == -1) {
                        gameCanvas.passBlink(blinkX, blinkY, palayerID);
                    } else if (mouseX == -1 && mouseY == -1 && blinkX == -1 && blinkY == -1 && playerOneLives == -1 && playerTwoLives == -1) {
                        gameCanvas.passOnToCharacters(x, y, palayerID);
                    } else if (blinkX == -1 && blinkY == -1 && playerOneLives == -1 && playerTwoLives == -1) {
                        gameCanvas.passShurikenToCharacters(x, y, mouseX, mouseY, palayerID);
                    } else if (playerOneLives >= 0 && playerTwoLives >= 0) {
                        gameCanvas.updateLife(playerOneLives, playerTwoLives);
                    }


                }
            }
        });
        listeningToMovement.start();
    }

    /**
     * This method sends and receives data to and from the server.
     * It also updates the playerlives that the client tracks
     */
    public class ClientSideConnection {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private String host;

        /**
         * This is the contsructor of the CSC. It initializes the socket and
         * the data in/out stream.
         * @param host
         */
        public ClientSideConnection(String host) {
            System.out.println("---GameFrame---");
            if (host.isEmpty()) {
                this.host = "localhost";
            } else {
                this.host = host;
            }
            try {
                socket = new Socket(this.host, 9393);
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
                playerID = dataIn.readInt();
                System.out.println("Connected to server as Player #" + playerID + ".");
                setTitle("Player #" + playerID
                );
//                contentPane.setVisible(false);
            } catch (IOException ex) {
                System.out.println("IO Exception from CSC constructor");
            }
        }


        /**
         * This method updates the GUI. It initializes the game canvas and moves it from the start
         * screen to the game screen
         *
         * @return
         */
        public int receiveOpenCommand() {
            int n = -1;

            try {
                n = dataIn.readInt();
                playerOneLives = dataIn.readInt();
                playerTwoLives = dataIn.readInt();
                if (n == 1) {
                    contentPane.removeAll();
                    gameCanvas = new GameCanvas(width, height, new Color(255, 255, 255), contentPane, csc, playerID, playerOneLives, playerTwoLives);
                    contentPane.setLayout(new BorderLayout());
                    contentPane.add(gameCanvas);
                    contentPane.revalidate();
                    contentPane.repaint();

                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return n;
        }

        /**
         * This method receives the sent player movement from the server.
         * @return
         */
        public int[] receiveMovement() {
            int x = 0;
            int y = 0;
            int mouseX = 0;
            int mouseY = 0;
            int blinkX = 0;
            int blinkY = 0;
            int playerID = 0;
            int playerOneLives = 0;
            int playerTwoLives = 0;
            try {
                x = dataIn.readInt();
                y = dataIn.readInt();
                mouseX = dataIn.readInt();
                mouseY = dataIn.readInt();
                blinkX = dataIn.readInt();
                blinkY = dataIn.readInt();
                playerID = dataIn.readInt();
                playerOneLives = dataIn.readInt();
                playerTwoLives = dataIn.readInt();
            } catch (Exception e) {
                e.printStackTrace();
            }

            int[] arrInt = new int[9];
            arrInt[0] = x;
            arrInt[1] = y;
            arrInt[2] = mouseX;
            arrInt[3] = mouseY;
            arrInt[4] = blinkX;
            arrInt[5] = blinkY;
            arrInt[6] = playerID;
            arrInt[7] = playerOneLives;
            arrInt[8] = playerTwoLives;
            return arrInt;
        }

        /**
         * This method sends the updated player movement to the server.
         * @param x
         * @param y
         * @param playerID
         */
        public void sendMovement(int x, int y, int playerID) {
            try {
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(playerID);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


        /**
         * This method sends the shuriken coordinates to the server
         * @param x
         * @param y
         * @param mouseX
         * @param mouseY
         * @param playerID
         */
        public void sendShuriken(int x, int y, int mouseX, int mouseY, int playerID) {
            try {
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.writeInt(mouseX);
                dataOut.writeInt(mouseY);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(playerID);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * This method sends blink coordinates to the server
         *
         * @param blinkX
         * @param blinkY
         * @param playerID
         */
        public void sendBlink(int blinkX, int blinkY, int playerID) {
            try {
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(blinkX);
                dataOut.writeInt(blinkY);
                dataOut.writeInt(playerID);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * This method lets the server know that the life of player one has been reduced
         */
        public void subtractPlayerOneLives() {
            try {
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(10);
                dataOut.writeInt(5);
                dataOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        /**
         * This method lets the server know that the life of player two has been reduced
         */
        public void subtractPlayerTwoLives() {
            try {
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(5);
                dataOut.writeInt(10);
                dataOut.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}