package hu.juranyi.zsolt.pubsearch.gui.tab;

import hu.juranyi.zsolt.pubsearch.Config;
import hu.juranyi.zsolt.pubsearch.StringTools;
import hu.juranyi.zsolt.pubsearch.crawl.Crawler;
import hu.juranyi.zsolt.pubsearch.data.Publication;
import hu.juranyi.zsolt.pubsearch.gui.GuiTools;
import hu.juranyi.zsolt.pubsearch.gui.control.LabelEx;
import hu.juranyi.zsolt.pubsearch.gui.control.PubTable;
import hu.juranyi.zsolt.pubsearch.gui.window.AboutWindow;
import hu.juranyi.zsolt.pubsearch.gui.window.AlertWindow;
import hu.juranyi.zsolt.pubsearch.gui.window.MainWindow;
import hu.juranyi.zsolt.pubsearch.gui.window.ProxyWindow;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;

/**
 * A Tab which contains the search form, the result table and the buttons.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
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
    private final ResourceBundle texts = ResourceBundle.getBundle("pubsearch.gui.texts.texts");
    private final TextField authorField = new TextField();
    private final TextField titleField = new TextField();
    private final ChoiceBox transLevCombo = new ChoiceBox(FXCollections.observableArrayList(texts.getString("transitivityLevel0"), texts.getString("transitivityLevel1"), texts.getString("transitivityLevel2")));
    private final TextField dbThreadLimitField = new TextField();
    private final TextField ppThreadLimitField = new TextField();
    private final CheckBox onlyLocalCheckBox = new CheckBox(texts.getString("onlyLocalSearch"));
    private final Button searchButton = new Button(texts.getString("searchButton"));
    private final PubTable resultsView;
    private final Label resultCountLabel = new Label();
    // Variables
    private Crawler crawler;

    /**
     * Sets up the Tab.
     * @param mainWindow MainWindow objects which contains the tab.
     */
    public MainTab(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        setText(texts.getString("searchTab"));
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
        Label authorLabel = new LabelEx(texts.getString("searchForAuthor")).bold().shadow().white();
        Label titleLabel = new LabelEx(texts.getString("filterByTitle")).shadow().white();
        Label transLevLabel = new LabelEx(texts.getString("transitivityLevel")).shadow().white();
        Label dbThreadLimitLabel = new LabelEx("dbThreadLimit").shadow().white(); // TODO i18n
        Label ppThreadLimitLabel = new LabelEx("ppThreadLimit").shadow().white(); // TODO i18n

        authorField.setOnAction(startSearchAction);
        authorField.setOnKeyReleased(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                searchButton.setDisable(authorField.getText().length() == 0);
                titleField.setDisable(authorField.getText().length() == 0);
            }
        });
        authorField.setTooltip(new Tooltip(texts.getString("authorFieldTooltip")));

        titleField.setOnAction(startSearchAction);
        titleField.setDisable(true);
        titleField.setTooltip(new Tooltip(texts.getString("titleFieldTooltip")));

        transLevCombo.getSelectionModel().select(1);

        dbThreadLimitField.setOnAction(startSearchAction);
        dbThreadLimitField.setPrefWidth(30);
        dbThreadLimitField.setText("2");

        ppThreadLimitField.setOnAction(startSearchAction);
        ppThreadLimitField.setPrefWidth(30);
        ppThreadLimitField.setText("3");

        onlyLocalCheckBox.setStyle("-fx-text-fill: #AFA");
        onlyLocalCheckBox.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent arg0) {
                transLevCombo.setDisable(onlyLocalCheckBox.selectedProperty().get());
                dbThreadLimitField.setDisable(onlyLocalCheckBox.selectedProperty().get());
                ppThreadLimitField.setDisable(onlyLocalCheckBox.selectedProperty().get());
            }
        });

        searchButton.setDisable(true);
        searchButton.setPrefWidth(75);
        searchButton.setPrefHeight(45);
        searchButton.setStyle("-fx-base: #3AD;");
        searchButton.setOnAction(startSearchAction);
        searchButton.setEffect(GuiTools.reflection);

        Button editProxiesButton = new Button(texts.getString("proxySetup"));
        editProxiesButton.setPrefWidth(150);
        editProxiesButton.setStyle("-fx-base: #D6F;");
        editProxiesButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                proxyWindow.show();
            }
        });

        Button editDBConnButton = new Button(texts.getString("databaseSetup"));
        editDBConnButton.setPrefWidth(150);
        editDBConnButton.setStyle("-fx-base: #D33;");
        editDBConnButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MainTab.this.mainWindow.configWindow.show();
            }
        });

        Button aboutButton = new Button(texts.getString("aboutPubSearch"));
        aboutButton.setPrefWidth(150);
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
        top.add(transLevLabel, 0, 2);
        top.add(transLevCombo, 1, 2);
        top.add(dbThreadLimitLabel, 0, 3, 2, 1);
        top.add(dbThreadLimitField, 2, 3);
        top.add(ppThreadLimitLabel, 0, 4, 2, 1);
        top.add(ppThreadLimitField, 2, 4);
        top.add(onlyLocalCheckBox, 1, 5);
        top.add(searchButton, 2, 0, 1, 2);
        top.add(aboutButton, 3, 0);
        top.add(editProxiesButton, 3, 1);
        top.add(editDBConnButton, 3, 2);

        /*
         * Center
         */
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

        Button abortButton = new Button(texts.getString("abort"));
        abortButton.setStyle("-fx-base: #822;");
        abortButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent arg0) {
                kill();
            }
        });

        searchLayout = new BorderPane();
        searchLayout.setCenter(progressBar);
        searchLayout.setBottom(abortButton);
        searchLayout.setCursor(Cursor.WAIT);
        BorderPane.setAlignment(abortButton, Pos.CENTER);
        BorderPane.setMargin(abortButton, new Insets(10));

        // ---
        setContent(mainLayout);
    }

    /**
     * Starts crawler and disables search form controls by switching the GUI.
     */
    private void startSearch() {
        if (!onlyLocalCheckBox.selectedProperty().get()) {
            // crawl eset
            if (Config.getProxyList().isEmpty()) {
                // nincs proxy, hibajelzés
                proxyWindow.show();
                AlertWindow.show(texts.getString("proxyListNeeded"));
            } else {
                // van proxy, indul a crawl, külön szálon, majd ő értesít az eredmények megjelenítéséről
                switchScene(false);

                int dbThreadLimit = 2;
                int ppThreadLimit = 3;
                try {
                    dbThreadLimit = Math.max(1, Integer.parseInt(dbThreadLimitField.getText()));
                } catch (NumberFormatException ex) {
                    dbThreadLimitField.setText(Integer.toString(dbThreadLimit));
                }
                try {
                    ppThreadLimit = Math.max(1, Integer.parseInt(ppThreadLimitField.getText()));
                } catch (NumberFormatException ex) {
                    ppThreadLimitField.setText(Integer.toString(ppThreadLimit));
                }

                crawler = new Crawler(this, authorField.getText(), titleField.getText(), transLevCombo.getSelectionModel().getSelectedIndex(), dbThreadLimit, ppThreadLimit);
                crawler.start();
            }
        } else {
            // only local eset, csak lekérdezés
            showResults(0);
        }
    }

    /**
     * Switches the GUI between "search form" and "search in progress" modes.
     * @param toMain If true, "search form" mode will be restored otherwise "search in progress" will be displayed.
     */
    public void switchScene(boolean toMain) {
        setContent((toMain) ? mainLayout : searchLayout);
        setText((toMain) ? texts.getString("searchTab") : texts.getString("searchInProgress"));
    }

    /**
     * Megjeleníti az eredményeket. A keresés befejeztével hívódik meg.
     * @param bytes Ennyi bájt adatforgalmat vett igénybe a keresés, a crawler motor jegyzi és adja át.
     */
    /**
     * Shows the results in the table. Selects the longest word from author search
     * field, then queries the database.
     * @param bytes Count of bytes to be displayed as net traffic used by the search.
     */
    public void showResults(long bytes) {
        long queryStartTime = System.nanoTime();
        try {
            String aup[] = authorField.getText().split(" ");
            String au = "";
            for (String p : aup) {
                if (p.length() > au.length()) {
                    au = p;
                }
            }
            resultsView.getSelectionModel().clearSelection();
            resultsView.setItems(FXCollections.observableArrayList(Publication.searchResults(au, titleField.getText())));
        } catch (Exception e) {
            AlertWindow.show(texts.getString("errorWhileQueryingResults"));
        }
        long time = System.nanoTime() - queryStartTime;
        if (!onlyLocalCheckBox.selectedProperty().get()) {
            time += crawler.getTime();
        }
        resultCountLabel.setText(String.format(texts.getString("resultInfos"), resultsView.getItems().size(), StringTools.formatNanoTime(time, false, false), StringTools.formatDataSize(bytes)));
        switchScene(true);
    }

    /**
     * Sets the focus on the author search field.
     */
    public void focusAuthorField() {
        authorField.requestFocus();
    }

    /**
     * Sends an interrupt for the crawler. Note: crawler will not stop immediately,
     * it will finish all running downloads and process them before dying.
     */
    public void kill() { // TODO javadoc update
        if (null != crawler) {
            System.err.println("Killing crawler thread.");
            crawler.interrupt();
        }
    }
}
