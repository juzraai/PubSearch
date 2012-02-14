package pubsearch.gui.window;

import java.util.Iterator;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.WindowEvent;
import pubsearch.Config;
import pubsearch.GetProxyList;
import pubsearch.gui.control.LabelEx;

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
        super("proxyWindowTitle", false, true);
        setScene(buildScene());
        setCSS();
        setOnShowing(new EventHandler<WindowEvent>() {

            public void handle(WindowEvent event) {
                List<String> pl = Config.getProxyList();
                StringBuilder sb = new StringBuilder();
                for (Iterator<String> it = pl.iterator(); it.hasNext();) {
                    String pli = it.next();
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
        LabelEx plzLabel = new LabelEx(texts.getString("proxyListDescription")).shadow().white().wrap();
        plzLabel.setTextAlignment(TextAlignment.JUSTIFY);

        proxyTA.setStyle("-fx-font-family:monospace;");
        proxyTA.setPrefWidth(200);

        Button getButton = new Button(texts.getString("downloadProxies"));
        getButton.setTextAlignment(TextAlignment.CENTER);
        getButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent arg0) {
                List<String> pl = GetProxyList.getProxyList();
                String ta = proxyTA.getText().trim();
                if (0 != ta.length()) ta += "\n";
                for (String p : pl) {
                    ta += p + "\n";
                }
                proxyTA.setText(ta);
                proxyTA.end();
            }
        });

        Button saveButton = new Button(texts.getString("saveProxies"));
        saveButton.setPrefHeight(32);
        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                Config.setProxyList(proxyTA.getText().split("\n"));
                Config.saveProxyList();
                ProxyWindow.this.hide();
            }
        });

        VBox buttons = new VBox(10);
        buttons.setPadding(new Insets(10));
        getButton.setMaxWidth(Double.MAX_VALUE);
        saveButton.setMaxWidth(Double.MAX_VALUE);
        buttons.getChildren().addAll(getButton, saveButton);
        buttons.setAlignment(Pos.CENTER);

        BorderPane center = new BorderPane();
        center.setPadding(new Insets(0, 10, 10, 0));
        center.setCenter(plzLabel);
        center.setBottom(buttons);
        BorderPane.setAlignment(plzLabel, Pos.TOP_CENTER);

        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(10));
        layout.setCenter(center);
        layout.setRight(proxyTA);

        return new Scene(layout, 420, 320);
    }
}
