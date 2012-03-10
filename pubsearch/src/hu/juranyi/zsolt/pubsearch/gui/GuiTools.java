package hu.juranyi.zsolt.pubsearch.gui;

import javafx.geometry.Rectangle2D;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Some tool for JavaFX 2.0 based GUI.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
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
     * Centers a Stage (window) on screen.
     *
     * @param stage Stage to be centerized.
     */
    public static void centerizeStage(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX((int) ((screen.getMaxX() - stage.getWidth()) / 2));
        stage.setY((int) ((screen.getMaxY() - stage.getHeight()) / 2));
    }
}
