package pubsearch.gui.control;

import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import pubsearch.data.Publication;
import pubsearch.gui.tab.PubTab;
import pubsearch.gui.window.MainWindow;

/**
 * Egy publikációkat megjelenítő TableView, ami egy publikáció kiválasztásakor
 * (ENTER vagy dupla klikk hatására) megjeleníti a hozzá tartozó ablakot.
 *
 * @author Zsolt
 */
public class PubTable extends TableView<Publication> {

    private final MainWindow mainWindow;
    private final ResourceBundle texts = ResourceBundle.getBundle("pubsearch.gui.texts.texts");

    public PubTable(MainWindow mainWindow) {
        this.mainWindow = mainWindow;

        TableColumn dbCol = new TableColumn(texts.getString("databaseColumn"));
        dbCol.setPrefWidth(100);
        dbCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("dbName")); // unsafe op.

        TableColumn authorsCol = new TableColumn(texts.getString("authorsColumn"));
        authorsCol.setPrefWidth(200);
        authorsCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("authors")); // unsafe op.

        TableColumn titleCol = new TableColumn(texts.getString("titleColumn"));
        titleCol.setPrefWidth(250);
        titleCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("title")); // unsafe op.

        TableColumn yearCol = new TableColumn(texts.getString("yearColumn"));
        yearCol.setPrefWidth(50);
        yearCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("yearAsString")); // unsafe op.
        final Callback<TableColumn<Publication, String>, TableCell<Publication, String>> yearCellFactory = yearCol.getCellFactory();
        yearCol.setCellFactory(new Callback<TableColumn<Publication, String>, TableCell<Publication, String>>() {

            public TableCell<Publication, String> call(TableColumn<Publication, String> param) {
                final TableCell<Publication, String> cell = yearCellFactory.call(param);
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        TableColumn cbCol = new TableColumn(texts.getString("citedByColumn"));
        cbCol.setPrefWidth(75);
        cbCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("citedByCount")); // unsafe op.
        final Callback<TableColumn<Publication, String>, TableCell<Publication, String>> cbCellFactory = cbCol.getCellFactory();
        cbCol.setCellFactory(new Callback<TableColumn<Publication, String>, TableCell<Publication, String>>() {

            public TableCell<Publication, String> call(TableColumn<Publication, String> param) {
                final TableCell<Publication, String> cell = cbCellFactory.call(param);
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });

        getColumns().addAll(dbCol, authorsCol, titleCol, yearCol, cbCol);
        setEditable(false);
        setPlaceholder(new Label(""));

        setOnKeyPressed(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    showDetails();
                }
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    showDetails();
                }
            }
        });
    }

    /**
     * Eseménykezelő. Esemény: duplakattintás/ENTER a találati listában.
     * Tevékenység: megjeleníti a kiválaszott publikáció adatait.
     */
    private void showDetails() {
        if (getSelectionModel().getSelectedIndex() > -1) {
            mainWindow.getTabPane().getTabs().add(new PubTab(mainWindow, getSelectionModel().getSelectedItem()));
            mainWindow.getTabPane().getSelectionModel().selectLast();
        }
    }
}
