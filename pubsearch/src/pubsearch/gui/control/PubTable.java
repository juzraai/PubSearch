package pubsearch.gui.control;

import java.util.ResourceBundle;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;
import pubsearch.data.Publication;
import pubsearch.gui.tab.PubTab;
import pubsearch.gui.window.MainWindow;

/**
 * TableView that lists publications, and shows the tab for a publication when its
 * selected (with double click or pressing ENTER).
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class PubTable extends TableView<Publication> {

    private final MainWindow mainWindow;
    private final ResourceBundle texts = ResourceBundle.getBundle("pubsearch.gui.texts.texts");

    /**
     * Sets up the TableView.
     * @param mainWindow The MainWindow object which holds the tabs.
     */
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
     * Shows the tab for the selected publication.
     */
    private void showDetails() {
        if (getSelectionModel().getSelectedIndex() > -1) {
            mainWindow.getTabPane().getTabs().add(new PubTab(mainWindow, getSelectionModel().getSelectedItem()));
            mainWindow.getTabPane().getSelectionModel().selectLast();
        }
    }
}
