package pubsearch.gui.control;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

    public PubTable(MainWindow mainWindow) {
        this.mainWindow = mainWindow;
        TableColumn authorsCol = new TableColumn("Authors");
        authorsCol.setPrefWidth(250);
        authorsCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("authors")); // unsafe op.

        TableColumn titleCol = new TableColumn("Title");
        titleCol.setPrefWidth(250);
        titleCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("title")); // unsafe op.

        getColumns().addAll(authorsCol, titleCol);
        setEditable(false);
        setPlaceholder(new Label(""));

        setOnKeyPressed(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    showPubWindow();
                }
            }
        });
        setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                if (event.getClickCount() > 1) {
                    showPubWindow();
                }
            }
        });
    }

    /**
     * Eseménykezelő. Esemény: duplakattintás/ENTER a találati listában. Tevékenység: betölti a hivatkozó publikációkat.
     */
    private void showPubWindow() {
        if (getSelectionModel().getSelectedIndex() > -1) {
            mainWindow.getTabPane().getTabs().add(new PubTab(mainWindow, getSelectionModel().getSelectedItem()));
            mainWindow.getTabPane().getSelectionModel().selectLast();
        }
    }
}
