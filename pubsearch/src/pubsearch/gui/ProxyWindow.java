package pubsearch.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import pubsearch.config.ConfigModel;

/**
 * A proxy lista beállításához használatos ablak.
 *
 * @author Zsolt
 */
public class ProxyWindow extends AWindow {

    private TextArea proxyTA = new TextArea();

    /**
     * Létrehozza az ablakot.
     */
    public ProxyWindow() {
        super("Proxy lista beállítása", false, true);
        setScene(buildScene());
        setCSS();
        setOnShowing(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                String[] pl = ConfigModel.getProxyList();
                StringBuilder sb = new StringBuilder();
                for (String pli : pl) {
                    sb.append(pli);
                    sb.append("\n");
                }
                proxyTA.setText(sb.toString());
            }
        });
    }

    /**
     * @return A felépített ablaktartalom.
     */
    private Scene buildScene() {
        MyLabel plzLabel = new MyLabel("A publikációs adatbázisok nem nézik jó szemmel a sűrű lekérdezéseket, ezért a program proxy-n keresztül küldi a kéréseket. Kérlek adj meg egy érvényes proxy listát (IP:PORT).", true, false, false, shadow);
        plzLabel.setWrapText(true);
        
        proxyTA.setStyle("-fx-font-family:monospace;");

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

        return new Scene(layout, 320, 340);
    }
}
