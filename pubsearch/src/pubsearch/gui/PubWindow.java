/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.gui;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import pubsearch.data.Link;
import pubsearch.data.Publication;

/**
 * Egy publikáció adatait megjelenítő ablak.
 * @author Zsolt
 */
public class PubWindow extends Stage {

    private Publication p;

    public PubWindow(Publication p) {
        this.p = p;
        setResizable(false);
        setTitle(p.getAuthors() + " - " + p.getTitle());
        setScene(buildScene());
        setOnShown(new EventHandler<WindowEvent>(){

            public void handle(WindowEvent event) {
                Tools.centerizeStage((Stage)(PubWindow.this));
            }
        }); 
    }

    /**
     * Felépíti az ablakot.
     * @return 
     */
    protected Scene buildScene() {
        /*
         * Tab list
         */
        Tab detailsTab = new Tab("Adatok");
        Tab linksTab = new Tab("Linkek (#)");
        Tab refPubsTab = new Tab("Hivatkozó publikációk (#)");
        Tab exportTab = new Tab("Exportálás");

        /*
         * Details tab
         */
        Label authorsLabel1 = new Label("Szerzők:");
        authorsLabel1.getStyleClass().add("bold-text");

        Label authorsLabel2 = new Label(p.getAuthors());
        authorsLabel2.getStyleClass().add("italic-text");
        authorsLabel2.setAlignment(Pos.CENTER_RIGHT);
        authorsLabel2.setTextAlignment(TextAlignment.RIGHT);
        authorsLabel2.setWrapText(true);

        Label titleLabel1 = new Label("Cím:");
        titleLabel1.getStyleClass().add("bold-text");

        Label titleLabel2 = new Label(p.getTitle());
        titleLabel2.getStyleClass().add("italic-text");
        titleLabel2.setAlignment(Pos.CENTER_RIGHT);
        titleLabel2.setTextAlignment(TextAlignment.RIGHT);
        titleLabel2.setWrapText(true);
        
        Label yearLabel1 = new Label("Év:");
        yearLabel1.getStyleClass().add("bold-text");
        
        Label yearLabel2 = new Label(Integer.toString(p.getYear()));
        yearLabel2.getStyleClass().add("italic-text");
        

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

        detailsTab.setContent(detailsGrid);

        /*
         * Links tab
         */
        TableView<Link> linksView = new TableView<Link>();
        linksView.setPlaceholder(new Label("Nincs link ehhez a publikációhoz."));
        
        TableColumn dbNameCol = new TableColumn("Adatbázis");
        dbNameCol.setPrefWidth(125);
        dbNameCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("dbID")); // unsafe op.
        
        TableColumn linkCol = new TableColumn("URL");
        linkCol.setPrefWidth(250);
        linkCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("url"));
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
        // TODO valahogy megoldani, hogy a dbID helyett majd dbName-et kapjunk vissza, persze tesztadatok is kellenek
        //Query q = Connection.em.createQuery("select d.dbName, l.url FROM dbName d, Link l WHERE l.pubID="+p.getId()+" AND l.dbID=d.id");
        //linksView.setItems(FXCollections.observableArrayList(q.getResultList()));

        //BorderPane linksPane = new BorderPane();
        //linksPane.setPadding(new Insets(10));
        //linksPane.getChildren().add(linksView);
        linksTab.setContent(linksView);

        /*
         * Build
         */
        TabPane tabs = new TabPane();
        tabs.tabClosingPolicyProperty().set(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabs.getTabs().addAll(detailsTab, linksTab, refPubsTab, exportTab);

        Scene scene = new Scene(tabs, 400, 250);
        //scene.getStylesheets().add("pubsearch/gui/style.css");
        return scene;
    }
}
