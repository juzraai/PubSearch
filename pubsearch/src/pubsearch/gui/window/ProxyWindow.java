package pubsearch.gui.window;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.WindowEvent;
import pubsearch.Config;
import pubsearch.gui.GuiTools;
import pubsearch.gui.control.MyLabel;

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
        super("Proxy list setup", false, true);
        setScene(buildScene());
        setCSS();
        setOnShowing(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                String[] pl = Config.getProxyList();
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
        MyLabel plzLabel = new MyLabel("Publication databases don't tolerate rare querying, so PubSearch uses proxies to reach them. Please specify a valid proxy list (IP:PORT).", true, false, false, GuiTools.shadow);
        plzLabel.setWrapText(true);
        
        proxyTA.setStyle("-fx-font-family:monospace;");

        Button saveButton = new Button("Save");
        saveButton.setPrefWidth(100);
        saveButton.setPrefHeight(32);
        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                Config.setProxyList(proxyTA.getText().split("\n"));
                Config.save();
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
