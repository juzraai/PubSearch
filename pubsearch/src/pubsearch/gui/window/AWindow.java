package pubsearch.gui.window;

import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pubsearch.gui.GuiTools;

/**
 * Absztrakt ablak - a közös működés kigyűjtése.
 *
 * @author Zsolt
 */
public abstract class AWindow extends Stage {

    protected static final String CSS_FILE = "pubsearch/gui/style.css";

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
        GuiTools.centerizeStage((Stage) this);
    }

    protected void setCSS() {
        getScene().getStylesheets().add(CSS_FILE);
    }
}
