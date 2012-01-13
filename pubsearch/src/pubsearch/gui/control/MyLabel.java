package pubsearch.gui.control;

import javafx.scene.control.Label;
import javafx.scene.effect.Effect;

/**
 * Egy kényelmesebb konstruktorok a Label objektumhoz, hogy csökkentsem a programsorokat.
 *
 * @author Zsolt
 */
public class MyLabel extends Label {

    public MyLabel(String text, Effect effect) {
        this(text, false, false, false, effect);
    }

    public MyLabel(String text, boolean bold, boolean italic) {
        this(text, false, bold, italic, null);
    }

    public MyLabel(String text, boolean white, boolean bold, boolean italic) {
        this(text, white, bold, italic, null);
    }

    public MyLabel(String text, boolean bold, boolean italic, Effect effect) {
        this(text, false, bold, italic, effect);
    }

    public MyLabel(String text, boolean white, boolean bold, boolean italic, Effect effect) {
        super(text);
        if (white) {
            getStyleClass().add("white-text");
        }
        if (bold) {
            getStyleClass().add("bold-text");
        }
        if (italic) {
            getStyleClass().add("italic-text");
        }
        setEffect(effect);
    }
}
