/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pubsearch.data.Connection;

/**
 *
 * @author Zsolt
 */
public class ConfigWindow extends Stage {

    private Stage mainWindow;
    private TextField urlField = new TextField(Connection.dburl);
    private TextField userField = new TextField(Connection.username);
    private PasswordField passwordField = new PasswordField();
    private Label msgLabel = new Label();
    private boolean configIsOK = true;

    public ConfigWindow(Stage mainWindow) {
        this.mainWindow = mainWindow;
        setResizable(false);
        setTitle("Adatbáziskapcsolat");
        setScene(buildScene());
        setOnShown(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                Tools.centerizeStage((Stage) (ConfigWindow.this));
            }
        });
        setOnShowing(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                ConfigWindow.this.mainWindow.hide();
            }
        });
        setOnHiding(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                if (configIsOK) {
                    ConfigWindow.this.mainWindow.show();
                }
            }
        });
    }

    private Scene buildScene() {
        Label urlLabel1 = new Label("Adatbázis URL:");
        urlLabel1.getStyleClass().addAll("bold-text", "white-text");

        Label urlLabel2 = new Label("\tmysql://");
        urlLabel2.getStyleClass().addAll("italic-text", "white-text");

        Label urlLabel3 = new Label("/pubsearch");
        urlLabel3.getStyleClass().addAll("italic-text", "white-text");

        Label urlLabel4 = new Label("(alapért.: 'localhost:3306')");
        urlLabel4.getStyleClass().addAll("white-text");

        Label userLabel1 = new Label("Felhasználó:");
        userLabel1.getStyleClass().addAll("bold-text", "white-text");

        Label userLabel2 = new Label("(alapért.: 'root')");
        userLabel2.getStyleClass().addAll("white-text");

        Label passwordLabel1 = new Label("Jelszó:");
        passwordLabel1.getStyleClass().addAll("bold-text", "white-text");

        Label passwordLabel2 = new Label("(alapért.: üres)");
        passwordLabel2.getStyleClass().addAll("white-text");

        urlField.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                reInit();
            }
        });
        userField.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                reInit();
            }
        });
        passwordField.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                reInit();
            }
        });
        passwordField.setText(Connection.password);

        msgLabel.getStyleClass().addAll("bold-text", "white-text");

        Button okButton = new Button("OK");
        okButton.setPrefWidth(75);
        okButton.setPrefHeight(32);
        okButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                reInit();
            }
        });

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(12));
        grid.setHgap(5);
        grid.setVgap(10);
        grid.add(urlLabel1, 0, 0, 3, 1);
        grid.add(urlLabel2, 0, 1);
        grid.add(urlField, 1, 1);
        grid.add(urlLabel3, 2, 1);
        grid.add(urlLabel4, 1, 2);
        grid.add(userLabel1, 0, 3, 3, 1);
        grid.add(userField, 1, 4);
        grid.add(userLabel2, 1, 5);
        grid.add(passwordLabel1, 0, 6, 3, 1);
        grid.add(passwordField, 1, 7);
        grid.add(passwordLabel2, 1, 8);
        grid.add(okButton, 0, 9, 3, 1);
        grid.add(msgLabel, 0, 10, 3, 1);
        GridPane.setHalignment(okButton, HPos.CENTER);
        GridPane.setHalignment(msgLabel, HPos.CENTER);

        Scene scene = new Scene(grid, 310, 340);
        scene.getStylesheets().add("pubsearch/gui/style.css");
        return scene;
    }

    private void reInit() {
        if (!Connection.dburl.equals(urlField.getText())
                || !Connection.username.equals(userField.getText())
                || !Connection.password.equals(passwordField.getText())) {
            Connection.dburl = urlField.getText();
            Connection.username = userField.getText();
            Connection.password = passwordField.getText();
            msgLabel.setText("");
            try {
                Connection.init();
                configIsOK = true;
            } catch (Throwable t) {
                configIsOK = false;
                msgLabel.setText("Nem sikerült felépíteni a kapcsolatot.");
                return;
            }
        }

        if (configIsOK) {
            ConfigWindow.this.hide();
        }
    }
}
