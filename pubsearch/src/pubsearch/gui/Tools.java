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
}
