package pubsearch.gui;

import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Segédfüggvények a GUI-hoz.
 *
 * @author Zsolt
 */
public class Tools {

    /**
     * Középre igazít egy Stage-et (ablakot) a képernyőn.
     *
     * @param stage a középre igazítandó Stage
     */
    public static void centerizeStage(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX((int) ((screen.getMaxX() - stage.getWidth()) / 2));
        stage.setY((int) ((screen.getMaxY() - stage.getHeight()) / 2));
    }

    /**
     * Megformáz egy nanoszekundumban megadott időt.
     * @param time
     * @return Az idő szövegként: MM:SS.MLSC,MCSC
     */
    public static String formatNanoTime(long time) {
        long nanosec = time % 1000;
        long microsec = (time / 1000) % 1000;
        long millisec = (time / 1000 / 1000) % 1000;
        long sec = (time / 1000 / 1000 / 1000) % 1000;
        long min = (time / 1000 / 1000 / 1000 / 60);
        
        return lpad(Long.toString(min), 2, '0') + ":"
                + lpad(Long.toString(sec), 2, '0') + "."
                + lpad(Long.toString(millisec), 3, '0') + ","
                + lpad(Long.toString(microsec), 3, '0');
    }

    /**
     * Balról kiegészít egy szöveget a megadott hosszúságra, a megadott karakterrel.
     * @param s A kiindulási szöveg.
     * @param n A célhossz.
     * @param c A kitöltő karakter.
     * @return A kiegészített szöveg.
     */
    public static String lpad(String s, int n, char c) {
        String z = new String(s);
        while (z.length() < n) {
            z = c + z;
        }
        return z;
    }
}
