package pubsearch.gui;

import javafx.scene.control.Label;
import javafx.scene.effect.Effect;

/**
 * Egy kényelmesebb konstruktor a Label objektumhoz, hogy csökkentsem a programsorokat.
 *
 * @author Zsolt
 */
public class MyLabel extends Label {

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
