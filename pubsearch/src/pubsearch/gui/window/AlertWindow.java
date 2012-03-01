package pubsearch.gui.window;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import pubsearch.gui.GuiTools;
import pubsearch.gui.control.LabelEx;

/**
 * Error message dialog.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class AlertWindow extends AWindow {

    private static AlertWindow alertWindow = new AlertWindow("");
    private Label messageLabel = new LabelEx("").bold().shadow().white();

    /**
     * Creates an error dialog.
     * @param message Error message.
     */
    public AlertWindow(String message) {
        this(message, "PubSearch");
    }

    /**
     * Creates an error dialog.
     * @param message Error message.
     * @param title Window title.
     */
    public AlertWindow(String message, String title) {
        super(title, false, true);
        messageLabel.setText(message);
        setScene(buildScene());
        setCSS();
    }

    private Scene buildScene() {
        messageLabel.setTextAlignment(TextAlignment.JUSTIFY);
        messageLabel.setWrapText(true);

        Text icon = new Text("!");
        icon.setTextAlignment(TextAlignment.CENTER);
        icon.getStyleClass().addAll("bold-text");
        icon.setFill(Color.WHITE);
        icon.setStyle("-fx-font-size:36pt;");
        icon.setEffect(GuiTools.reflection);

        Button okButton = new Button("OK");
        okButton.setCancelButton(true); // dialog now can be ESCaped out :-)
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
        scene.getRoot().setStyle("-fx-background-color: transparent;");
        scene.setFill(gradient);
        scene.getRoot().setCache(true);
        return scene;
    }

    public void setMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Shows an error dialog.
     * @param msg Error message.
     */
    public static void show(String msg) {
        show(msg, "PubSearch");
    }

    /**
     * Shows an error dialog
     * @param msg Error message.
     * @param title Window title.
     */
    public static void show(String msg, String title) {
        alertWindow.setTitle(title);
        alertWindow.setMessage(msg);
        alertWindow.show();
    }
}
