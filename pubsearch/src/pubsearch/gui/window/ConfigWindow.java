package pubsearch.gui.window;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.WindowEvent;
import pubsearch.Config;
import pubsearch.data.Connection;
import pubsearch.gui.GuiTools;
import pubsearch.gui.control.LabelEx;

/**
 * Az adatbáziskapcsolat beállítására szolgáló ablak.
 *
 * @author Zsolt
 */
public class ConfigWindow extends AWindow {

    private final MainWindow mainWindow;
    private TextField urlField = new TextField();
    private TextField userField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Label msgLabel = new LabelEx("", true, true, false);
    private boolean configIsOK;

    public ConfigWindow(MainWindow mainWindow) {
        super("Setup local database connection", false, true);
        this.mainWindow = mainWindow;
        setScene(buildScene());
        setCSS();
        setOnShowing(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                configIsOK = (Connection.getEm() != null);
                urlField.setText(Config.getJdbcUrl());
                userField.setText(Config.getJdbcUser());
                passwordField.setText(Config.getJdbcPass());
            }
        });
        setOnHidden(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                if (configIsOK) {
                    ConfigWindow.this.mainWindow.show();
                } else {
                    System.exit(1);
                }
            }
        });
    }

    /**
     * @return A felépített ablaktartalom.
     */
    private Scene buildScene() {
        LabelEx plzLabel = new LabelEx("PubSearch needs a MySQL database to store the gathered informations. Please specify the parameters.", true, false, false, GuiTools.shadow);
        plzLabel.setWrapText(true);
        LabelEx urlLabel1 = new LabelEx("Database (server) URL:", true, true, false, GuiTools.shadow);
        LabelEx urlLabel2 = new LabelEx("\tmysql://", true, false, true, GuiTools.shadow);
        LabelEx urlLabel3 = new LabelEx("/pubsearch", true, false, true, GuiTools.shadow);
        LabelEx urlLabel4 = new LabelEx("(default: 'localhost:3306')", true, false, false, GuiTools.shadow);
        LabelEx userLabel1 = new LabelEx("Username:", true, true, false, GuiTools.shadow);
        LabelEx userLabel2 = new LabelEx("(default: 'root')", true, false, false, GuiTools.shadow);
        LabelEx passwordLabel1 = new LabelEx("Password:", true, true, false, GuiTools.shadow);
        LabelEx passwordLabel2 = new LabelEx("(default: empty)", true, false, false, GuiTools.shadow);

        EventHandler<ActionEvent> reInitAction = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                reInit();
            }
        };

        Button okButton = new Button("Connect & save");
        okButton.setPrefWidth(150);
        okButton.setPrefHeight(32);
        okButton.setOnAction(reInitAction);
        urlField.setOnAction(reInitAction);
        userField.setOnAction(reInitAction);
        passwordField.setOnAction(reInitAction);

        GridPane grid = new GridPane();
        grid.setHgap(5);
        grid.setVgap(10);
        grid.add(urlLabel1, 0, 1, 3, 1);
        grid.add(urlLabel2, 0, 2);
        grid.add(urlField, 1, 2);
        grid.add(urlLabel3, 2, 2);
        grid.add(urlLabel4, 1, 3);
        grid.add(userLabel1, 0, 4, 3, 1);
        grid.add(userField, 1, 5);
        grid.add(userLabel2, 1, 6);
        grid.add(passwordLabel1, 0, 7, 3, 1);
        grid.add(passwordField, 1, 8);
        grid.add(passwordLabel2, 1, 9);
        grid.add(okButton, 0, 10, 3, 1);
        grid.add(msgLabel, 0, 11, 3, 1);
        GridPane.setHalignment(okButton, HPos.CENTER);
        GridPane.setHalignment(msgLabel, HPos.CENTER);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setTop(plzLabel);
        layout.setCenter(grid);
        BorderPane.setAlignment(grid, Pos.CENTER);

        return new Scene(layout, 310, 380);
    }

    /**
     * Megpróbálja újrainicializálni az adatbáziskapcsolatot. Ha sikeres, akkor
     * bezárja az ablakot és elmenti a beállításokat.
     */
    private void reInit() {
        if (!Config.getJdbcUrl().equals(urlField.getText())
                || !Config.getJdbcUser().equals(userField.getText())
                || !Config.getJdbcPass().equals(passwordField.getText())
                || !configIsOK) {
            Config.setJdbcUrl(urlField.getText());
            Config.setJdbcUser(userField.getText());
            Config.setJdbcPass(passwordField.getText());
            msgLabel.setText("");

            configIsOK = Connection.tryInit();
            if (configIsOK) {
                Config.save();
            } else {
                msgLabel.setText("Can't connect to database.");
            }
        }

        if (configIsOK) {
            ConfigWindow.this.hide();
        }
    }
}
