package pubsearch.gui.control;

import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import pubsearch.gui.GuiTools;

/**
 * Makes easy to use the JavaFX 2.0 Label object. It has methods that can be called
 * in a "chain" right after the constructor, like this:<br />
 * <pre>Label myLabel = new LabelEx("Hello LabelEx!").bold().italic().effect(new Reflection());</pre>
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class LabelEx extends Label {

    public LabelEx(String text) {
        super(text);
    }

    /**
     * Adds "bold-text" style class to the Label.
     * @return Return the label object (this).
     */
    public LabelEx bold() {
        getStyleClass().add("bold-text");
        return this;
    }

    /**
     * Calls setEffect() with the given parameter.
     * @param e Effect to be set on this Label.
     * @return Return the label object (this).
     */
    public LabelEx effect(Effect e) {
        setEffect(e);
        return this;
    }

    /**
     * Adds "italic-text" style class to the Label.
     * @return Return the label object (this).
     */
    public LabelEx italic() {
        getStyleClass().add("italic-text");
        return this;
    }

    public LabelEx shadow() {
        setEffect(GuiTools.shadow);
        return this;
    }

    /**
     * Adds "white-text" style class to the Label.
     * @return Return the label object (this).
     */
    public LabelEx white() {
        getStyleClass().add("white-text");
        return this;
    }

    /**
     * Turns on text wrapping.
     * @return Return the label object (this).
     */
    public LabelEx wrap() {
        setWrapText(true);
        return this;
    }
}
