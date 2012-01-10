package pubsearch.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pubsearch.config.ConfigModel;
import pubsearch.data.Publication;

/**
 * A program főablaka.
 *
 * @author Zsolt
 */
public class MainWindow extends AWindow {

    public final ConfigWindow configWindow = new ConfigWindow((Stage) this);
    private final ProxyWindow proxyWindow = new ProxyWindow();
    private final AboutWindow aboutWindow = new AboutWindow();
    private final TextField authorField = new TextField();
    private final TextField titleField = new TextField();
    private final CheckBox onlyLocalCheckBox = new CheckBox("Keresés csak a helyi adatbázisban");
    private final Button searchButton = new Button("Keresés!");
    private final PubTable resultsView = new PubTable();
    private final Label resultCountLabel = new Label();

    /**
     * Létrehozza az ablakot.
     */
    public MainWindow() {
        super("PubSearch", true, false);
        setScene(buildScene());
        setCSS();
    }

    /**
     * @return A felépített ablaktartalom.
     */
    private Scene buildScene() {

        EventHandler<ActionEvent> startSearchAction = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                if (!searchButton.isDisabled()) {
                    startSearch();
                }
            }
        };

        /*
         * Top
         */
        Label authorLabel = new Label("Keresés szerzőre:");
        authorLabel.setLabelFor(authorField);
        authorLabel.getStyleClass().addAll("white-text", "bold-text");

        authorField.setOnAction(startSearchAction);

        authorField.setOnKeyReleased(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                searchButton.setDisable(authorField.getText().length() == 0);
                titleField.setDisable(authorField.getText().length() == 0);
            }
        });

        Label titleLabel = new Label("Szűkítés címre:");
        titleLabel.setLabelFor(titleField);
        titleLabel.getStyleClass().addAll("white-text");

        titleField.setOnAction(startSearchAction);
        titleField.setDisable(true);

        onlyLocalCheckBox.setStyle("-fx-text-fill: #AFA");

        searchButton.setDisable(true);
        searchButton.setPrefWidth(75);
        searchButton.setPrefHeight(45);
        searchButton.setStyle("-fx-base: #3AD;");
        searchButton.setOnAction(startSearchAction);

        Button editProxiesButton = new Button("Proxy...");
        editProxiesButton.setPrefWidth(100);
        editProxiesButton.setStyle("-fx-base: #D6F;");
        editProxiesButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                proxyWindow.show();
            }
        });

        Button editDBConnButton = new Button("Adatbázis...");
        editDBConnButton.setPrefWidth(100);
        editDBConnButton.setStyle("-fx-base: #D33;");
        editDBConnButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                configWindow.show();
            }
        });

        Button aboutButton = new Button("Névjegy");
        aboutButton.setPrefWidth(100);
        aboutButton.setStyle("-fx-base: #3D6");
        aboutButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                aboutWindow.show();
            }
        });

        GridPane top = new GridPane();
        top.setPadding(new Insets(12));
        top.setHgap(10);
        top.setVgap(10);
        top.add(authorLabel, 0, 0);
        top.add(authorField, 1, 0);
        top.add(titleLabel, 0, 1);
        top.add(titleField, 1, 1);
        top.add(onlyLocalCheckBox, 1, 2);
        top.add(searchButton, 2, 0, 1, 2);
        top.add(aboutButton, 3, 0);
        top.add(editProxiesButton, 3, 1);
        top.add(editDBConnButton, 3, 2);

        /*
         * Center
         */
        TableColumn authorsCol = new TableColumn("Szerzők");
        authorsCol.setPrefWidth(250);
        authorsCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("authors")); // unsafe op.
        TableColumn titleCol = new TableColumn("Cím");
        titleCol.setPrefWidth(250);
        titleCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("title")); // unsafe op.

        resultCountLabel.getStyleClass().addAll("white-text", "bold-text");
        resultCountLabel.setTextAlignment(TextAlignment.CENTER);
        resultCountLabel.setWrapText(true);
        BorderPane.setAlignment(resultCountLabel, Pos.CENTER);

        BorderPane center = new BorderPane();
        center.setPadding(new Insets(12));
        center.setCenter(resultsView);
        center.setTop(resultCountLabel);

        /*
         * Build
         */
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(0));
        layout.setTop(top);
        layout.setCenter(center);
        return new Scene(layout, 520, 300);
    }

    /**
     * Eseménykezelő. Esemény: keresés gomb akciója. Tevékenység: elindítja a kereső algoritmust, és elérhetetlenné teszi a GUI-t (beviteli mező, keresés gomb).
     */
    private void startSearch() {
        long startTime = System.nanoTime();
        if (!onlyLocalCheckBox.selectedProperty().get()) {
            if (ConfigModel.getProxyList().length == 0) {
                proxyWindow.show();
                AlertWindow.show("A kereséshez meg kell adnod egy proxy listát.\n(Vagy keress csak a helyi adatbázisban.)");
                return;
            }
            // crawl
            // TODO majd bent a szóközöket +-ra cseréli! (?)
        }

        try {
            resultsView.setItems(FXCollections.observableArrayList(Publication.searchResults(authorField.getText(), titleField.getText())));
        } catch (Throwable t) {
            AlertWindow.show("Hiba történt lekérdezés közben (JPA_ERROR).");
            return;
        }

        long time = System.nanoTime() - startTime;
        System.out.println(time);
        System.out.println(Tools.formatNanoTime(time));
        resultCountLabel.setText(String.format("%d db találat (idő: %s, adatforgalom: %d KB)", resultsView.getItems().size(), Tools.formatNanoTime(time), 0));
    }
}
