/**
 * This is the GameServer class. This receives and sends data to different clients.
 * It also stores the amount of lives of players 1 and 2
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
import java.net.*;
import java.io.*;

public class GameServer {
    private ServerSocket ss;
    private int numPlayers;
    private ServerSideConnection player1;
    private ServerSideConnection player2;
    private boolean sentOpen;
    private int aplayerOneLives, aplayerTwoLives;

    /**
     * This is the constructor of the GameServer. It initializes the ServerSocket
     * and the player lives.
     */
    public GameServer() {
        System.out.println("----Game GameServer----");
        numPlayers = 0;
        sentOpen = false;
        aplayerOneLives = 9;
        aplayerTwoLives = 9;

        try {
            ss = new ServerSocket(9393);
        } catch (IOException ex) {
            System.out.println("IOException from GameServer Constructor");
        }
    }

    /**
     * This method waits for two clients to connect to the ServerSocket
     */
    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            while (numPlayers < 2) {
                Socket s = ss.accept();
                numPlayers++;
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers);
                System.out.println("Connected: Player # " + numPlayers);

                if (numPlayers == 1) {
                    player1 = ssc;
                } else if (numPlayers == 2) {
                    player2 = ssc;
                }

                Thread t = new Thread(ssc);
                t.start();
            }

            System.out.println("We now have 2 players. No longer accepting connections.");
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    /**
     * This method receives and sends out data to and from the clients connected
     */
    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dataIn;
        private DataOutputStream dataOut;
        private int playerID;

        /**
         * This constructor instantiates the data input and output.
         * It catches an error
         * @param s
         * @param id
         */
        public ServerSideConnection(Socket s, int id) {
            socket = s;
            playerID = id;

            try {
                dataIn = new DataInputStream(socket.getInputStream());
                dataOut = new DataOutputStream(socket.getOutputStream());
            } catch (IOException ex) {
                System.out.println("IOException from run() SSC constructor");
            }
        }

        /**
         * This method reads the data received. It also decides what to do with the received values
         */
        @Override
        public void run() {
            try {
                dataOut.writeInt(playerID);
                dataOut.flush();
                while (true) {
                    if (!sentOpen && numPlayers == 2) {
                        player1.sendOpenCommand();
                        player2.sendOpenCommand();
                        sentOpen = true;
                    }

                    int x = dataIn.readInt();
                    int y = dataIn.readInt();
                    int mouseX = dataIn.readInt();
                    int mouseY = dataIn.readInt();
                    int blinkX = dataIn.readInt();
                    int blinkY = dataIn.readInt();
                    int fromWho = dataIn.readInt();
                    int playerOneLives = dataIn.readInt();
                    int playerTwoLives = dataIn.readInt();



                    if (mouseX == -1 && mouseY == -1 && x == -1 && y == -1 && playerOneLives == -1 && playerTwoLives == -1) {
                        if (playerID == 1) {
                            player2.sendBlink(blinkX, blinkY, fromWho);
                        } else {
                            player1.sendBlink(blinkX, blinkY, fromWho);
                        }

                    } else if (mouseX == -1 && mouseY == -1 && blinkX == -1 && blinkY == -1 && playerOneLives == -1 && playerTwoLives == -1) {
                        if (playerID == 1) {
                            player2.sendMovement(x, y, fromWho);
                        } else if (playerID == 2) {
                            player1.sendMovement(x, y, fromWho);
                        }
                    } else if (blinkX == -1 && blinkY == -1 && playerOneLives == -1 && playerTwoLives == -1) {
                        if (playerID == 1) {
                            player2.sendShuriken(x, y, mouseX, mouseY, fromWho);
                        } else if (playerID == 2) {
                            player1.sendShuriken(x, y, mouseX, mouseY, fromWho);

                        }
                    } else if(playerOneLives == 10) {
                        aplayerOneLives--;
                        player1.sendLives();
                        player2.sendLives();
                    } else  if(playerTwoLives == 10) {
                       aplayerTwoLives--;
                        player1.sendLives();
                        player2.sendLives();
                    }



                }

            } catch (IOException ex) {
                System.out.println("IOException from run() SSC");
            }
        }

        /**
         * This method allows the canvas to move from the home page
         * to the game canvas
         */
        public void sendOpenCommand() {
            try {
                dataOut.writeInt(1);
                dataOut.writeInt(aplayerOneLives);
                dataOut.writeInt(aplayerTwoLives);
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from sendOpenCommand() ssc");
            }
        }

        /**
         * This method sends the received player movement back to the clients
         * @param x
         * @param y
         * @param fromWho
         */
        public void sendMovement(int x, int y, int fromWho) {
            try {
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(fromWho);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.flush();

            } catch (IOException ex) {
                System.out.println("IOException from sendMovement() ssc");
            }
        }

        /**
         * This method sends the received shuriken coordinates back to the clients
         * @param x
         * @param y
         * @param mouseX
         * @param mouseY
         * @param fromWho
         */
        public void sendShuriken(int x, int y, int mouseX, int mouseY, int fromWho) {
            try {
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.writeInt(mouseX);
                dataOut.writeInt(mouseY);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(fromWho);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.flush();

            } catch (IOException ex) {
                System.out.println("IOException from sendMovement() ssc");
            }
        }

        /**
         * This method sends the received blink coordinates back to the clients
         * @param x
         * @param y
         * @param fromWho
         */
        public void sendBlink(int x, int y, int fromWho) {
            try {
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(x);
                dataOut.writeInt(y);
                dataOut.writeInt(fromWho);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from sendMovement() ssc");
            }
        }

        /**
         * This method updates the amount of lives the players have left
         */
        public void sendLives() {
            try {
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(-1);
                dataOut.writeInt(aplayerOneLives);
                dataOut.writeInt(aplayerTwoLives);
                dataOut.flush();
            } catch (IOException ex) {
                System.out.println("IOException from sendMovement() ssc");
            }
        }
    }

    /**
     * This is the main method of the Server class
     * @param args
     */
    public static void main(String[] args) {
        GameServer s = new GameServer();
        s.acceptConnections();
    }

}