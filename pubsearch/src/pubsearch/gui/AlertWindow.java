package pubsearch.gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;

/**
 * Hibaüzenet ablak.
 *
 * @author Zsolt
 */
public class AlertWindow extends AWindow {
 
    /**
     * Eltárolunk egy objektumot, ezt fogja beállítani és megjeleníteni a
     * statikus show(msg), így nem kell mindig felépíteni egy ablakot.
     */
    private static AlertWindow alertWindow = new AlertWindow("");
    private Label messageLabel = new Label();

    /**
     * Létrehoz egy új hibaüzenet ablakot,
     * @param message Hibaüzenet.
     */
    public AlertWindow(String message) {
        this(message, "PubSearch");
    }

    /**
     * Létrehoz egy új hibaüzenet ablakot.
     * @param message Hibaüzenet.
     * @param title Ablak címsora.
     */
    public AlertWindow(String message, String title) {
        super(title, false, true);
        messageLabel.setText(message);
        setScene(buildScene());
        setCSS();
    }

    /**
     * @return A felépített ablaktartalom.
     */
    private Scene buildScene() {
        DropShadow ds = new DropShadow();
        ds.setOffsetX(3.0f);
        ds.setOffsetY(3.0f);
        ds.setColor(Color.BLACK);

        messageLabel.getStyleClass().addAll("bold-text", "white-text");
        messageLabel.setTextAlignment(TextAlignment.JUSTIFY);
        messageLabel.setWrapText(true);
        messageLabel.setEffect(ds);

        Reflection r = new Reflection();
        r.setFraction(0.75);

        Text icon = new Text("!");
        icon.setTextAlignment(TextAlignment.CENTER);
        icon.getStyleClass().addAll("bold-text");
        icon.setFill(Color.WHITE);
        icon.setStyle("-fx-font-size:36pt;");
        icon.setEffect(r);

        Button okButton = new Button("OK");
        okButton.setCancelButton(true); // így lesz kiESCelhető a dialog
        okButton.setStyle("-fx-base: #900;");
        okButton.setPrefWidth(50);
        okButton.setPrefHeight(25);
        okButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                AlertWindow.this.hide();
            }
        });

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(12));
        layout.setLeft(icon);
        layout.setCenter(messageLabel);
        layout.setBottom(okButton);
        BorderPane.setMargin(icon, new Insets(0, 20, 0, 20));
        BorderPane.setAlignment(messageLabel, Pos.TOP_CENTER);
        BorderPane.setMargin(messageLabel, new Insets(10, 10, 10, 10));
        BorderPane.setAlignment(okButton, Pos.CENTER);

        Stop[] gradientColors = new Stop[]{
            new Stop(0, Color.web("#AA0000")),
            new Stop(1, Color.web("#330000")),};
        //LinearGradient gradient = new LinearGradient(0f, 0f, 0f, 1f, true, CycleMethod.NO_CYCLE, gradientColors);
        RadialGradient gradient = new RadialGradient(0, 0, 38, 48, 300, false, CycleMethod.NO_CYCLE, gradientColors);

        Scene scene = new Scene(layout, 400, 125);
        scene.getRoot().setStyle("-fx-background-color: transparent;"); // levesszük a CSS hátteret      
        scene.setFill(gradient);
        scene.getRoot().setCache(true);
        return scene;
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Megjelenít egy hibaüzenet ablakot.
     * @param msg A hibaüzenet.
     */
    public static void show(String msg) {
        show("PubSearch", msg);
    }

    /**
     * Megjelenít egy hibaüzenet ablakot.
     *
     * @param title Ablak címsora.
     * @param msg Hibaüzenet.
     */
    public static void show(String title, String msg) {
        alertWindow.setTitle(title);
        alertWindow.setMessage(msg);
        alertWindow.show();
    }
}