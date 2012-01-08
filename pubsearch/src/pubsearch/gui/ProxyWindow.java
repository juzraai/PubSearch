package pubsearch.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pubsearch.config.ConfigModel;

/**
 * A proxy lista beállításához használatos ablak.
 *
 * @author Zsolt
 */
public class ProxyWindow extends Stage {

    private TextArea proxyTA = new TextArea();

    public ProxyWindow(Stage mainWindow) {
        initModality(Modality.APPLICATION_MODAL);
        setResizable(false);
        setTitle("Proxy lista");
        setScene(buildScene());
        setOnShown(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                String[] pl = ConfigModel.getProxyList();
                StringBuilder sb = new StringBuilder();
                for (String pli : pl) {
                    sb.append(pli);
                    sb.append("\n");
                }
                proxyTA.setText(sb.toString());
                Tools.centerizeStage((Stage) (ProxyWindow.this));

            }
        });
    }

    private Scene buildScene() {
        Label plzLabel = new Label("A publikációs adatbázisok nem nézik jó szemmel a sűrű lekérdezéseket, ezért a program proxy-n keresztül küldi a kéréseket. Kérlek adj meg egy érvényes proxy listát (IP:PORT).");
        plzLabel.getStyleClass().addAll("white-text");
        plzLabel.setWrapText(true);

        Button saveButton = new Button("Mentés");
        saveButton.setPrefWidth(100);
        saveButton.setPrefHeight(32);
        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                ConfigModel.setProxyList(proxyTA.getText().split("\n"));
                ConfigModel.save();
                ProxyWindow.this.hide();
            }
        });

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(12));
        layout.setTop(plzLabel);
        layout.setCenter(proxyTA);
        layout.setBottom(saveButton);
        BorderPane.setMargin(proxyTA, new Insets(10, 0, 10, 0));
        BorderPane.setAlignment(saveButton, Pos.CENTER);

        Scene scene = new Scene(layout, 320, 340);
        scene.getStylesheets().add("pubsearch/gui/style.css");
        return scene;
    }
}
