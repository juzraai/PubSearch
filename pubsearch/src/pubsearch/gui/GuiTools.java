package pubsearch.gui;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Segédfüggvények JavaFX-es GUI-hoz.
 *
 * @author Zsolt
 */
public class GuiTools {

    public static final Reflection reflection = new Reflection();
    public static final DropShadow shadow = new DropShadow();
    public static final Blend reflAndShadow = new Blend();
    
    static {
        reflection.setFraction(0.75);
        shadow.setOffsetX(3.0f);
        shadow.setOffsetY(3.0f);
        shadow.setColor(Color.BLACK);
        reflAndShadow.setMode(BlendMode.OVERLAY);
        reflAndShadow.setBottomInput(reflection);
        reflAndShadow.setTopInput(shadow);
    }
    /**
     * Hozzáad egy stílusosztályt egy/több Node-hoz.
     *
     * @param styleClass A stílusosztály neve.
     * @param nodes Node(-ok).
     */
    public static void addStyleClassToNodes(String styleClass, Node... nodes) {
        for (Node n : nodes) {
            n.getStyleClass().add(styleClass);
        }
    }

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
