package pubsearch.gui;

import pubsearch.Tools;
import com.sun.glass.ui.Application;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import pubsearch.config.ConfigModel;
import pubsearch.crawl.Crawler;
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
    private BorderPane mainLayout, searchLayout;
    private Paint mainFill, searchFill;
    private final int WIDTH = 520;
    private long startTime;

    /**
     * Létrehozza az ablakot.
     */
    public MainWindow() {
        super("PubSearch", true, false);
        build();
        setScene(new Scene(mainLayout, WIDTH, 300));
        setCSS();
        switchScene(true);
    }

    /**
     * Vált a főablak kétféle állapota között: keresőform; keresés folyamatban.
     * @param toMain Keresőform állapot? Ha nem, akkor keresés folyamatban.
     */
    private void switchScene(boolean toMain) {
        if (toMain) {
            getScene().setRoot(mainLayout);
            getScene().setFill(mainFill);
        } else {
            getScene().setRoot(searchLayout);
            getScene().setFill(searchFill);
        }
    }

    /**
     * Felépíti az ablak tartalmát, mindkét állapothoz (keresőform; keresés folyamatban),
     * és eltárolja az adattagokban.
     */
    private void build() {
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
        MyLabel authorLabel = new MyLabel("Keresés szerzőre:", true, true, false, shadow);
        MyLabel titleLabel = new MyLabel("Szűkítés címre:", true, false, false, shadow);

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
        searchButton.setEffect(reflection);

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

        mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(0));
        mainLayout.setTop(top);
        mainLayout.setCenter(center);
        mainLayout.setStyle("-fx-background-color: transparent;");

        Stop[] mainFillColors = new Stop[]{
            new Stop(0, Color.web("#484860")),
            new Stop(1, Color.web("#333344")),};
        mainFill = new RadialGradient(0, 0, WIDTH / 2, 0, WIDTH, false, CycleMethod.NO_CYCLE, mainFillColors);

        /*
         * Searching
         */
        ProgressBar progressBar = new ProgressBar(-1f);
        progressBar.setPrefSize(250, 25);
        progressBar.setEffect(reflection);

        searchLayout = new BorderPane();
        searchLayout.setCenter(progressBar);
        searchLayout.setStyle("-fx-background-color: transparent;");

        Stop[] searchFillColors = new Stop[]{
            new Stop(0, Color.web("#A5DDFE")),
            new Stop(1, Color.web("#029DFB")),};
        searchFill = new RadialGradient(0, 0, WIDTH / 2, 0, WIDTH, false, CycleMethod.NO_CYCLE, searchFillColors);

    }

    /**
     * Eseménykezelő. Esemény: keresés gomb akciója. Tevékenység: elindítja a kereső algoritmust, és elérhetetlenné teszi a GUI-t (beviteli mező, keresés gomb).
     */
    private void startSearch() {
        startTime = System.nanoTime();
        if (!onlyLocalCheckBox.selectedProperty().get()) {
            // crawl eset
            if (ConfigModel.getProxyList().length == 0) {
                // nincs proxy, hibajelzés
                proxyWindow.show();
                AlertWindow.show("A kereséshez meg kell adnod egy proxy listát.\n(Vagy keress csak a helyi adatbázisban.)");
            } else {
                // van proxy, indul a crawl, külön szálon, majd ő értesít az eredmények megjelenítéséről
                switchScene(false);
                Crawler crawler = new Crawler(this, authorField.getText(), titleField.getText());
                crawler.start();
            }
        } else {
            // only local eset, csak lekérdezés
            showResults(0);
        }
    }

    /**
     * Megjeleníti az eredményeket. A keresés befejeztével hívódik meg.
     * @param bytes Ennyi bájt adatforgalmat vett igénybe a keresés, a crawler motor jegyzi és adja át.
     */
    public void showResults(long bytes) {
        try {
            resultsView.setItems(FXCollections.observableArrayList(Publication.searchResults(authorField.getText(), titleField.getText())));
        } catch (Throwable t) {
            AlertWindow.show("Hiba történt lekérdezés közben (JPA_ERROR).");
        }
        long time = System.nanoTime() - startTime;
        resultCountLabel.setText(String.format("%d db találat (idő: %s, adatforgalom: %s)", resultsView.getItems().size(), Tools.formatNanoTime(time, false, false), Tools.formatDataSize(bytes)));
        switchScene(true);
    }
}
