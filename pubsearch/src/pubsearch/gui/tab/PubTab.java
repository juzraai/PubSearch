package pubsearch.gui.tab;//TODO i18n

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import pubsearch.data.Publication;
import pubsearch.gui.GuiTools;
import pubsearch.gui.control.PubTable;
import pubsearch.gui.window.MainWindow;

/**
 * Egy publikáció adatait megjelenítő tab.
 *
 * @author Zsolt
 */
public class PubTab extends Tab {

    private MainWindow mainWindow;
    private Publication p;

    public PubTab(MainWindow mainWindow, Publication p) {
        super(p.getTitle());
        this.mainWindow = mainWindow;
        this.p = p;

        /*
         * Details
         */
        Label authorsLabel1 = new Label("Authors");
        Label titleLabel1 = new Label("Title");
        Label yearLabel1 = new Label("Year");
        Label dbLabel1 = new Label("Database");
        Label urlLabel1 = new Label("URL");

        Label authorsLabel2 = new Label(p.getAuthors());
        authorsLabel2.setWrapText(true);

        Label titleLabel2 = new Label(p.getTitle());
        titleLabel2.setWrapText(true);

        Integer y = p.getYear();
        String ys = (null == y) ? "(ismeretlen)" : y.toString();
        Label yearLabel2 = new Label(ys);

        Label dbLabel2 = new Label(p.getDbName());
        Label urlLabel2 = new Label(p.getUrl());

        GuiTools.addStyleClassToNodes("bold-text", authorsLabel1, titleLabel1, yearLabel1, dbLabel1, urlLabel1);
        GuiTools.addStyleClassToNodes("italic-text", authorsLabel2, titleLabel2, yearLabel2, dbLabel2, urlLabel2);

        GridPane detailsGrid = new GridPane();
        detailsGrid.setPadding(new Insets(12));
        detailsGrid.setHgap(20);
        detailsGrid.setVgap(20);
        detailsGrid.setManaged(true);
        detailsGrid.add(authorsLabel1, 0, 0);
        detailsGrid.add(authorsLabel2, 1, 0);
        detailsGrid.add(titleLabel1, 0, 1);
        detailsGrid.add(titleLabel2, 1, 1);
        detailsGrid.add(yearLabel1, 0, 2);
        detailsGrid.add(yearLabel2, 1, 2);
        detailsGrid.add(dbLabel1, 0, 3);
        detailsGrid.add(dbLabel2, 1, 3);
        detailsGrid.add(urlLabel1, 0, 4);
        detailsGrid.add(urlLabel2, 1, 4);
        GridPane.setValignment(authorsLabel1, VPos.TOP);
        GridPane.setValignment(titleLabel1, VPos.TOP);

        /*
         * Tabs
         */
        TabPane tabs = new TabPane();
        tabs.getStyleClass().add("pubtabs");
        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.setTabMinWidth(75);
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3);
        tabs.setEffect(shadow);

        /*
         * BibTeX tab
         */
        if (null != p.getBibtex()) {
            final TextArea bibtexTA = new TextArea(p.getBibtex());
            bibtexTA.setEditable(false);
            bibtexTA.setStyle("-fx-font-family:monospace;-fx-font-size:14px;");

            Button copyButton = new Button("Másolás");
            copyButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    bibtexTA.selectAll();
                    bibtexTA.copy();
                    bibtexTA.deselect();
                }
            });

            Button saveButton = new Button("Fájlba mentés...");
            saveButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    FileChooser fc = new FileChooser();
                    fc.setTitle("BibTeX exportálása...");
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Minden fájl (*.*)", "*.*"));

                    File f = fc.showSaveDialog(PubTab.this.mainWindow);

                    BufferedWriter w = null;
                    try {
                        w = new BufferedWriter(new FileWriter(f));
                        w.write(bibtexTA.getText());
                        w.newLine();
                    } catch (IOException e) {
                    } finally {
                        System.out.println("BIBTEX EXPORTED.");
                        if (w != null) {
                            try {
                                w.close();
                            } catch (IOException e) {
                            }
                        }
                    }
                }
            });

            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            buttons.setPadding(new Insets(12));
            buttons.getChildren().addAll(copyButton, saveButton);

            BorderPane bibtexTabLayout = new BorderPane();
            bibtexTabLayout.setTop(buttons);
            bibtexTabLayout.setCenter(bibtexTA);
            BorderPane.setMargin(bibtexTA, new Insets(0, 10, 10, 10));

            Tab bibtexTab = new Tab("BibTeX");
            bibtexTab.setContent(bibtexTabLayout);
            tabs.getTabs().add(bibtexTab);
        }

        /*
         * Cites tab
         */
        ObservableList<Publication> cites = FXCollections.observableArrayList(p.getCitedBy());
        if (cites.size() > 0) {
            PubTable citesView = new PubTable(mainWindow);
            citesView.setItems(cites);

            BorderPane citesLayout = new BorderPane();
            citesLayout.setCenter(citesView);
            BorderPane.setMargin(citesView, new Insets(10));

            Tab citesTab = new Tab("Cited by (" + cites.size() + ")");
            citesTab.setContent(citesLayout);
            tabs.getTabs().add(citesTab);
        }

        /*
         * Build
         */
        BorderPane layout = new BorderPane();
        layout.setTop(detailsGrid);
        layout.setCenter(tabs);
        BorderPane.setMargin(tabs, new Insets(10));
        setContent(layout);
    }
}
