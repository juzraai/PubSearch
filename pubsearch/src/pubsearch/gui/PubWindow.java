package pubsearch.gui;

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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import pubsearch.data.Link;
import pubsearch.data.Publication;

/**
 * Egy publikáció adatait megjelenítő ablak.
 *
 * @author Zsolt
 */
public class PubWindow extends AWindow {

    private Publication p;

    /**
     * Létrehozza a publikációhoz tartozó ablakot.
     * @param p A publikáció, melynek adatai az ablakban megjelennek.
     */
    public PubWindow(Publication p) {
        super(p.getAuthors() + " - " + p.getTitle(), true, false);
        this.p = p;
        setScene(buildScene());
        setCSS();
    }

    /**
     * @return A felépített ablaktartalom.
     */
    private Scene buildScene() {
        TabPane tabs = new TabPane();
        tabs.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.UNAVAILABLE);

        /*
         * Details tab
         */
        MyLabel authorsLabel1 = new MyLabel("Szerzők:", false, true, false, null);

        MyLabel authorsLabel2 = new MyLabel(p.getAuthors(), false, false, true, null);
        authorsLabel2.setAlignment(Pos.CENTER_RIGHT);
        authorsLabel2.setTextAlignment(TextAlignment.RIGHT);
        authorsLabel2.setWrapText(true);

        MyLabel titleLabel1 = new MyLabel("Cím:", false, true, false, null);

        MyLabel titleLabel2 = new MyLabel(p.getTitle(), false, false, true, null);
        titleLabel2.setAlignment(Pos.CENTER_RIGHT);
        titleLabel2.setTextAlignment(TextAlignment.RIGHT);
        titleLabel2.setWrapText(true);

        MyLabel yearLabel1 = new MyLabel("Év:", false, true, false, null);

        Integer y = p.getYear();
        String ys = (y == null) ? "(ismeretlen)" : y.toString();
        MyLabel yearLabel2 = new MyLabel(ys, false, false, true, null);

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

        Tab detailsTab = new Tab("Adatok");
        detailsTab.setContent(detailsGrid);
        tabs.getTabs().add(detailsTab);

        /*
         * BibTeX tab
         */
        final TextArea bibtexTA = new TextArea(p.getBibtex());
        bibtexTA.setEditable(false);
        bibtexTA.setStyle("-fx-font-family:monospace;");        

        Button copyButton = new Button("Másolás");
        copyButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                bibtexTA.selectAll();
                bibtexTA.copy();
            }
        });

        Button saveButton = new Button("Fájlba mentés...");
        saveButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("BibTeX exportálása...");
                fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Minden fájl (*.*)", "*.*"));

                File f = fc.showSaveDialog(PubWindow.this.getOwner());

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

        /*
         * Links tab
         */
        ObservableList<Link> links = FXCollections.observableArrayList(p.getLinks());
        if (links.size() > 0) {
            TableView<Link> linksView = new TableView<Link>();
            linksView.setPlaceholder(new Label("Nincs link ehhez a publikációhoz."));

            TableColumn dbNameCol = new TableColumn("Adatbázis");
            dbNameCol.setPrefWidth(125);
            dbNameCol.setCellValueFactory(new PropertyValueFactory<Link, String>("dbName")); // unsafe op.

            TableColumn linkCol = new TableColumn("URL");
            linkCol.setPrefWidth(250);
            linkCol.setCellValueFactory(new PropertyValueFactory<Link, String>("url"));
            final Callback<TableColumn<Link, String>, TableCell<Link, String>> cf = linkCol.getCellFactory();
            linkCol.setCellFactory(new Callback<TableColumn<Link, String>, TableCell<Link, String>>() {

                public TableCell<Link, String> call(TableColumn<Link, String> param) {
                    final TableCell<Link, String> cell = cf.call(param);
                    cell.setStyle("-fx-text-fill: blue; -fx-underline: true");
                    cell.setOnMouseClicked(new EventHandler<MouseEvent>() {

                        public void handle(MouseEvent event) {
                            //TODO open link (cell.getText()) in new browser window
                            System.out.println("LINK: " + cell.getText());
                        }
                    });
                    return cell;
                }
            });
            linksView.getColumns().addAll(dbNameCol, linkCol);
            linksView.setItems(links);

            Tab linksTab = new Tab("Linkek (" + links.size() + ")");
            linksTab.setContent(linksView);
            tabs.getTabs().add(linksTab);
        }

        /*
         * Cites tab
         */
        ObservableList<Publication> cites = FXCollections.observableArrayList(p.getCites());
        if (cites.size() > 0) {
            Tab citesTab = new Tab("Hivatkozik erre (" + cites.size() + ")");
            PubTable citesView = new PubTable();
            citesView.setItems(cites);
            citesTab.setContent(citesView);
            tabs.getTabs().add(citesTab);
        }

        return new Scene(tabs, 400, 250);
    }
}
