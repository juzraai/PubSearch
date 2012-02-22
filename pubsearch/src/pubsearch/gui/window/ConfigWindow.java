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
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import pubsearch.Config;
import pubsearch.data.Connection;
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
    private Label msgLabel = new LabelEx("").bold().white();
    private boolean configIsOK;

    public ConfigWindow(MainWindow mainWindow) {
        super("configWindowTitle", false, true);
        this.mainWindow = mainWindow;
        setScene(buildScene());
        setCSS();
        setOnShowing(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                configIsOK = (Connection.getEntityManager() != null);
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
        LabelEx plzLabel = new LabelEx(texts.getString("databaseDescription")).shadow().white();
        plzLabel.setTextAlignment(TextAlignment.JUSTIFY);
        plzLabel.setWrapText(true);
        LabelEx urlLabel1 = new LabelEx(texts.getString("dbServer")).bold().shadow().white();
        LabelEx urlLabel2 = new LabelEx("\tmysql://").italic().shadow().white();
        LabelEx urlLabel3 = new LabelEx("/pubsearch").italic().shadow().white();
        LabelEx urlLabel4 = new LabelEx("(" + texts.getString("default") + " 'localhost:3306')").shadow().white();
        LabelEx userLabel1 = new LabelEx(texts.getString("dbUsername")).bold().shadow().white();
        LabelEx userLabel2 = new LabelEx("(" + texts.getString("default") + " 'root')").shadow().white();
        LabelEx passwordLabel1 = new LabelEx(texts.getString("dbPassword")).bold().shadow().white();
        LabelEx passwordLabel2 = new LabelEx("(" + texts.getString("default") + " " + texts.getString("empty") + ")").shadow().white();

        EventHandler<ActionEvent> reInitAction = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                reInit();
            }
        };

        Button okButton = new Button(texts.getString("connectAndSave"));
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
                Config.saveMySQLConfig();
            } else {
                msgLabel.setText(texts.getString("cantConnect"));
            }
        }

        if (configIsOK) {
            ConfigWindow.this.hide();
        }
    }
}
