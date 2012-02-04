package pubsearch.gui.tab;

import java.awt.Desktop;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
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
    private final ResourceBundle texts = ResourceBundle.getBundle("pubsearch.gui.texts.texts");

    public PubTab(MainWindow mainWindow, Publication p) {
        super(p.getTitle());
        this.mainWindow = mainWindow;
        this.p = p;

        /*
         * Details
         */
        Label authorsLabel1 = new Label(texts.getString("authorsLabel"));
        Label titleLabel1 = new Label(texts.getString("titleLabel"));
        Label yearLabel1 = new Label(texts.getString("yearLabel"));
        Label dbLabel1 = new Label(texts.getString("databaseLabel"));
        Label urlLabel1 = new Label(texts.getString("urlLabel"));

        dbLabel1.setMinWidth(75);

        Label authorsLabel2 = new Label(p.getAuthors());
        authorsLabel2.setWrapText(true);

        Label titleLabel2 = new Label(p.getTitle());
        titleLabel2.setWrapText(true);

        Integer y = p.getYear();
        String ys = (null == y || -1 == y) ? texts.getString("unknownYear") : y.toString();
        Label yearLabel2 = new Label(ys);

        Label dbLabel2 = new Label(p.getDbName());
        final Hyperlink urlLabel2 = new Hyperlink(p.getUrl());
        urlLabel2.setOnMouseClicked(new EventHandler<MouseEvent>(){

            public void handle(MouseEvent arg0) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(new URI(urlLabel2.getText())); // TODO TEST THIS METHOD ON LINUX (lovi), MAC (lizik)
                    } catch (Exception ex) {
                    }
                }
            }
        });

        GuiTools.addStyleClassToNodes("bold-text", authorsLabel1, titleLabel1, yearLabel1, dbLabel1, urlLabel1);
        GuiTools.addStyleClassToNodes("italic-text", authorsLabel2, titleLabel2, yearLabel2, dbLabel2, urlLabel2);

        GridPane details = new GridPane();
        details.setPadding(new Insets(12));
        details.setHgap(20);
        details.setVgap(20);
        details.setManaged(true);
        details.add(authorsLabel1, 0, 0);
        details.add(authorsLabel2, 1, 0);
        details.add(titleLabel1, 0, 1);
        details.add(titleLabel2, 1, 1);
        details.add(yearLabel1, 0, 2);
        details.add(yearLabel2, 1, 2);
        details.add(dbLabel1, 0, 3);
        details.add(dbLabel2, 1, 3);
        details.add(urlLabel1, 0, 4);
        details.add(urlLabel2, 1, 4);
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
        // TODO uniformize somehow...
        if (null != p.getBibtex()) {
            final TextArea bibtexTA = new TextArea(p.getBibtex());
            bibtexTA.setEditable(false);
            bibtexTA.setStyle("-fx-font-family:monospace;-fx-font-size:14px;");

            Button copyButton = new Button(texts.getString("copyToClipboard"));
            copyButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    bibtexTA.selectAll();
                    bibtexTA.copy();
                    bibtexTA.deselect();
                }
            });

            Button saveButton = new Button(texts.getString("exportToFile"));
            saveButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    FileChooser fc = new FileChooser();
                    fc.setTitle(texts.getString("saveDialogTitle"));
                    fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(texts.getString("allFiles") + " (*.*)", "*.*"));

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
        ObservableList<Publication> cites = FXCollections.observableArrayList(new ArrayList<Publication>(p.getCitedBy()));
        if (cites.size() > 0) {
            PubTable citesView = new PubTable(mainWindow);
            citesView.setItems(cites);

            BorderPane citesLayout = new BorderPane();
            citesLayout.setCenter(citesView);
            BorderPane.setMargin(citesView, new Insets(10));

            Tab citesTab = new Tab(texts.getString("citedByTab") + " (" + cites.size() + ")");
            citesTab.setContent(citesLayout);
            tabs.getTabs().add(citesTab);
        }

        /*
         * Build
         */
        BorderPane layout = new BorderPane();
        layout.setTop(details);
        layout.setCenter(tabs);
        BorderPane.setMargin(tabs, new Insets(10));
        setContent(layout);
    }
}
