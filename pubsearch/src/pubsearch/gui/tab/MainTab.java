package pubsearch.gui.tab;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import pubsearch.Config;
import pubsearch.StringTools;
import pubsearch.crawl.Crawler;
import pubsearch.data.Publication;
import pubsearch.gui.GuiTools;
import pubsearch.gui.control.MyLabel;
import pubsearch.gui.control.PubTable;
import pubsearch.gui.window.AboutWindow;
import pubsearch.gui.window.AlertWindow;
import pubsearch.gui.window.MainWindow;
import pubsearch.gui.window.ProxyWindow;

/**
 *
 * @author Zsolt
 */
public class MainTab extends Tab {

    // Connection
    private final MainWindow mainWindow;
    // Windows
    private final ProxyWindow proxyWindow = new ProxyWindow();
    private final AboutWindow aboutWindow = new AboutWindow();
    // MainTab states
    private BorderPane mainLayout;
    private BorderPane searchLayout;
    // Controls
    private final TextField authorField = new TextField();
    private final TextField titleField = new TextField();
    private final CheckBox onlyLocalCheckBox = new CheckBox("Search only in the local database");
    private final Button searchButton = new Button("Search!");
    private final PubTable resultsView;
    private final Label resultCountLabel = new Label();
    // Variables
    private long startTime;

    public MainTab(MainWindow mainWindow) {
        super("Search");
        this.mainWindow = mainWindow;
        setClosable(false);

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
        MyLabel authorLabel = new MyLabel("Search for author:", true, false, GuiTools.shadow);
        MyLabel titleLabel = new MyLabel("Filter by title:", false, false, GuiTools.shadow);

        authorField.setOnAction(startSearchAction);
        authorField.setOnKeyReleased(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                searchButton.setDisable(authorField.getText().length() == 0);
                titleField.setDisable(authorField.getText().length() == 0);
            }
        });

        titleField.setOnAction(startSearchAction);
        titleField.setDisable(true);

        onlyLocalCheckBox.setStyle("-fx-text-fill: #AFA");

        searchButton.setDisable(true);
        searchButton.setPrefWidth(75);
        searchButton.setPrefHeight(45);
        searchButton.setStyle("-fx-base: #3AD;");
        searchButton.setOnAction(startSearchAction);
        searchButton.setEffect(GuiTools.reflection);

        Button editProxiesButton = new Button("Proxy setup");
        editProxiesButton.setPrefWidth(120);
        editProxiesButton.setStyle("-fx-base: #D6F;");
        editProxiesButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                proxyWindow.show();
            }
        });

        Button editDBConnButton = new Button("Database setup");
        editDBConnButton.setPrefWidth(120);
        editDBConnButton.setStyle("-fx-base: #D33;");
        editDBConnButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MainTab.this.mainWindow.configWindow.show();
            }
        });

        Button aboutButton = new Button("About PubSearch");
        aboutButton.setPrefWidth(120);
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
        TableColumn authorsCol = new TableColumn("Authors");
        authorsCol.setPrefWidth(250);
        authorsCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("authors")); // unsafe op.
        TableColumn titleCol = new TableColumn("Title");
        titleCol.setPrefWidth(250);
        titleCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("title")); // unsafe op.

        resultsView = new PubTable(mainWindow);
        resultCountLabel.getStyleClass().addAll("white-text", "bold-text");
        resultCountLabel.setTextAlignment(TextAlignment.CENTER);
        resultCountLabel.setWrapText(true);
        BorderPane.setAlignment(resultCountLabel, Pos.CENTER);

        BorderPane center = new BorderPane();
        center.setPadding(new Insets(12));
        center.setCenter(resultsView);
        center.setTop(resultCountLabel);

        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(0));
        mainLayout.setTop(top);
        mainLayout.setCenter(center);

        /*
         * Searching
         */
        ProgressBar progressBar = new ProgressBar(-1f);
        progressBar.setPrefSize(250, 25);
        progressBar.setEffect(GuiTools.reflection);

        searchLayout = new BorderPane();
        searchLayout.setCenter(progressBar);

        // ---
        setContent(mainLayout);
    }

    /**
     * Eseménykezelő. Esemény: keresés gomb akciója. Tevékenység: elindítja a
     * kereső algoritmust, és elérhetetlenné teszi a GUI-t (beviteli mező, keresés gomb).
     */
    private void startSearch() {
        startTime = System.nanoTime();
        if (!onlyLocalCheckBox.selectedProperty().get()) {
            // crawl eset
            if (Config.getProxyList().length == 0) {
                // nincs proxy, hibajelzés
                proxyWindow.show();
                AlertWindow.show("Please define a proxy list first.\nOr you can search only in the local database.");
            } else {
                // van proxy, indul a crawl, külön szálon, majd ő értesít az eredmények megjelenítéséről
                switchScene(false);
                final Crawler crawler = new Crawler(this, authorField.getText(), titleField.getText());
                crawler.start();
            }
        } else {
            // only local eset, csak lekérdezés
            showResults(0);
        }
    }

    /**
     * Vált a keresés kétféle állapota között: keresőform; keresés folyamatban.
     * @param toMain Keresőform állapot? Ha nem, akkor keresés folyamatban.
     */
    public void switchScene(boolean toMain) {
        setContent((toMain) ? mainLayout : searchLayout);
        setText((toMain) ? "Search" : "( . . . )");
    }

    /**
     * Megjeleníti az eredményeket. A keresés befejeztével hívódik meg.
     * @param bytes Ennyi bájt adatforgalmat vett igénybe a keresés, a crawler motor jegyzi és adja át.
     */
    public void showResults(long bytes) {
        try {
            resultsView.setItems(FXCollections.observableArrayList(Publication.searchResults(authorField.getText(), titleField.getText())));
        } catch (Throwable t) {
            AlertWindow.show("Error while querying results. (JPA_ERROR).");
        }
        long time = System.nanoTime() - startTime;
        resultCountLabel.setText(String.format("%d results (time: %s, net traffic: %s)", resultsView.getItems().size(), StringTools.formatNanoTime(time, false, false), StringTools.formatDataSize(bytes)));
        switchScene(true);
    }

    public void focusAuthorField() {        
        authorField.requestFocus();
    }
}
