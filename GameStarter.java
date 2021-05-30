/**
 * This is the GameStarter class. It contains the main method for
 * the client and setups the GUI of the GameFrame
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
public class GameStarter {
    public static void main(String[] args) {
        GameFrame c = new GameFrame(1024, 768);
        c.setUpGUI();
    }
}
