package pubsearch.gui.control;

import javafx.scene.control.Label;
import javafx.scene.effect.Effect;
import pubsearch.gui.GuiTools;

/**
 * A Label objektum kényelmesebb paraméterezéséhez.
 *
 * @author Zsolt
 */
public class LabelEx extends Label {

    public LabelEx(String text) {
        super(text);
    }

    public LabelEx bold() {
        getStyleClass().add("bold-text");
        return this;
    }

    public LabelEx effect(Effect e) {
        setEffect(e);
        return this;
    }

    public LabelEx italic() {
        getStyleClass().add("italic-text");
        return this;
    }

    public LabelEx shadow() {
        setEffect(GuiTools.shadow);
        return this;
    }

    public LabelEx white() {
        getStyleClass().add("white-text");
        return this;
    }

    public LabelEx wrap() {
        setWrapText(true);
        return this;
    }
}
