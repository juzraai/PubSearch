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
    private ObservableList<Publication> results = FXCollections.observableArrayList();
    private TextField authorField = new TextField();
    private TextField titleField = new TextField();
    private CheckBox onlyLocalCheckBox = new CheckBox("Keresés csak a helyi adatbázisban");
    private final TableView<Publication> resultsView = new TableView<Publication>();
    private Label resultCountLabel = new Label();

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
                startSearch();
            }
        };

        /*
         * Top
         */
        Label authorLabel = new Label("Szerző:");
        authorLabel.setLabelFor(authorField);
        authorLabel.setStyle("-fx-text-fill: white");

        authorField.setOnAction(startSearchAction);

        Label titleLabel = new Label("Cím:");
        titleLabel.setLabelFor(titleField);
        titleLabel.setStyle("-fx-text-fill: white");

        titleField.setOnAction(startSearchAction);

        onlyLocalCheckBox.setStyle("-fx-text-fill: #AFA");

        Button searchButton = new Button("Keresés!");
        searchButton.setPrefWidth(75);
        searchButton.setPrefHeight(45);
        searchButton.setStyle("-fx-base: #3AD;");
        searchButton.setOnAction(startSearchAction);

        Button editProxiesButton = new Button("Proxy...");
        editProxiesButton.setPrefWidth(100);
        editProxiesButton.setStyle("-fx-base: #D6F;");
        editProxiesButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MainWindow.this.proxyWindow.show();
            }
        });

        Button editDBConnButton = new Button("Adatbázis...");
        editDBConnButton.setPrefWidth(100);
        editDBConnButton.setStyle("-fx-base: #D33;");
        editDBConnButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MainWindow.this.configWindow.show();
            }
        });

        Button aboutButton = new Button("Névjegy");
        aboutButton.setPrefWidth(100);
        aboutButton.setStyle("-fx-base: #3D6");

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

        resultsView.setPlaceholder(new Label("Nincs megjeleníthető találat."));
        resultsView.getColumns().addAll(authorsCol, titleCol);
        resultsView.setEditable(false);
        resultsView.setOnKeyPressed(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    showPubWindow();
                }
            }
        });
        resultsView.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    showPubWindow();
                }
            }
        });

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

        return new Scene(layout, 600, 300);
    }

    /**
     * Eseménykezelő. Esemény: duplakattintás/ENTER a találati listában. Tevékenység: betölti a hivatkozó publikációkat.
     */
    private void showPubWindow() {
        if (resultsView.getSelectionModel().getSelectedIndex() > -1) {
            Publication p = resultsView.getSelectionModel().getSelectedItem();

            new PubWindow(p).show();
        }
    }

    /**
     * Eseménykezelő. Esemény: keresés gomb akciója. Tevékenység: elindítja a kereső algoritmust, és elérhetetlenné teszi a GUI-t (beviteli mező, keresés gomb).
     */
    private void startSearch() {
        if (!onlyLocalCheckBox.selectedProperty().get()) {
            if (ConfigModel.getProxyList().length == 0) {
                AlertWindow.show("A kereséshez meg kell adnod egy érvényes proxy listát.");
            }
            // crawl
            // TODO majd bent a szóközöket +-ra cseréli! (?)
        }
        try {
            resultsView.setItems(FXCollections.observableArrayList(Publication.searchResults(authorField.getText(), titleField.getText())));
        } catch (Throwable t) {
            AlertWindow.show("Hiba történt lekérdezés közben (JPA_ERROR).");
        }
        resultCountLabel.setText(String.format("%d db találat (szerző: ' %s ', cím: ' %s '); a művelet %d KB adatforgalmat vett igénybe", resultsView.getItems().size(), authorField.getText(), titleField.getText(), 0));
    }
}
