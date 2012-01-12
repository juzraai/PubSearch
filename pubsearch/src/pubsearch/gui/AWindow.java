package pubsearch.gui;

import pubsearch.Tools;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Absztrakt ablak - a közös működés kigyűjtése.
 *
 * @author Zsolt
 */
public abstract class AWindow extends Stage {

    protected static final String CSS_FILE = "pubsearch/gui/style.css";
    protected static final Reflection reflection = new Reflection();
    protected static final DropShadow shadow = new DropShadow();
    protected static final Blend reflAndShadow = new Blend();

    static {
        reflection.setFraction(0.75);
        shadow.setOffsetX(3.0f);
        shadow.setOffsetY(3.0f);
        shadow.setColor(Color.BLACK);
        reflAndShadow.setMode(BlendMode.OVERLAY);
        reflAndShadow.setBottomInput(reflection);
        reflAndShadow.setTopInput(shadow);
    }

    public AWindow(String title, boolean resizable, boolean modal) {
        super();
        setTitle(title);
        setResizable(resizable);
        if (modal) {
            initModality(Modality.APPLICATION_MODAL);
        }
        setOnShown(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                AWindow.this.onShownAction(event);
            }
        });
    }

    /**
     * Eseménykezelő. Esemény: az ablak megjelent a képernyőn. Tevékenység: középre igazítja az ablakot.
     * @param event Esemény paraméterei.
     */
    protected void onShownAction(WindowEvent event) {
        centerizeStage((Stage) this);
    }

    protected void setCSS() {
        getScene().getStylesheets().add(CSS_FILE);
    }
    
    /**
     * Középre igazít egy Stage-et (ablakot) a képernyőn.
     *
     * @param stage a középre igazítandó Stage
     */
    private static void centerizeStage(Stage stage) {
        Rectangle2D screen = Screen.getPrimary().getVisualBounds();
        stage.setX((int) ((screen.getMaxX() - stage.getWidth()) / 2));
        stage.setY((int) ((screen.getMaxY() - stage.getHeight()) / 2));
    }
}
