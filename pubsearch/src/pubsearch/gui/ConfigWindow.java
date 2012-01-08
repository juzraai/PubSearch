package pubsearch.gui;

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
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pubsearch.config.ConfigModel;
import pubsearch.data.Connection;

/**
 * Az adatbáziskapcsolat beállítására szolgáló ablak.
 *
 * @author Zsolt
 */
public class ConfigWindow extends AWindow {

    private final Stage mainWindow;
    private TextField urlField = new TextField();
    private TextField userField = new TextField();
    private PasswordField passwordField = new PasswordField();
    private Label msgLabel = new Label();
    private boolean configIsOK;

    public ConfigWindow(Stage mainWindow) {
        super("Adatbáziskapcsolat beállítása", false, true);
        this.mainWindow = mainWindow;
        setScene(buildScene());
        setCSS();
        setOnShowing(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                configIsOK = (Connection.getEm() != null);
                urlField.setText(ConfigModel.getJdbcUrl());
                userField.setText(ConfigModel.getJdbcUser());
                passwordField.setText(ConfigModel.getJdbcPass());
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
        Label plzLabel = new Label("A programnak szüksége van egy MySQL adatbázisra a találatok tárolásához, kérlek add meg a paramétereket.");
        plzLabel.getStyleClass().addAll("white-text");
        plzLabel.setWrapText(true);

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
        if (!ConfigModel.getJdbcUrl().equals(urlField.getText())
                || !ConfigModel.getJdbcUser().equals(userField.getText())
                || !ConfigModel.getJdbcPass().equals(passwordField.getText())
                || !configIsOK) {
            ConfigModel.setJdbcUrl(urlField.getText());
            ConfigModel.setJdbcUser(userField.getText());
            ConfigModel.setJdbcPass(passwordField.getText());
            msgLabel.setText("");

            configIsOK = Connection.tryInit();
            if (configIsOK) {
                ConfigModel.save();
            } else {
                msgLabel.setText("Nem sikerült felépíteni a kapcsolatot.");
                // TODO lehetne itt is hiba típus alapján eljárni
            }
        }

        if (configIsOK) {
            ConfigWindow.this.hide();
        }
    }
}
