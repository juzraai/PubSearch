package pubsearch.gui.window;

import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pubsearch.gui.GuiTools;

/**
 * Abstract window for PubSearch - gathers common functionalities.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public abstract class AWindow extends Stage {

    protected final ResourceBundle texts = ResourceBundle.getBundle("pubsearch.gui.texts.texts");
    protected static final String CSS_FILE = "pubsearch/gui/style.css";

    /**
     * Sets up the window.
     * @param title Window title.
     * @param resizable If true, window will be able to resized by the user.
     * @param modal If true, other windows of the application cannot be accessed
     * until this one is not closed.
     */
    public AWindow(String title, boolean resizable, boolean modal) {
        super();
        try {
            setTitle(texts.getString(title));
        } catch (Exception e) {
            setTitle(title);
        }
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
     * Centerizes the window.
     * @param event Window event object which triggered this method.
     */
    protected void onShownAction(WindowEvent event) {
        GuiTools.centerizeStage((Stage) this);
    }

    /**
     * Sets the CSS file for the window.
     */
    protected void setCSS() {
        getScene().getStylesheets().add(CSS_FILE);
    }
}
