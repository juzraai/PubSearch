/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.gui;

import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import pubsearch.data.Publication;

/**
 *
 * @author Zsolt
 */
public class PubTable extends TableView<Publication> {

    public PubTable() {
        TableColumn authorsCol = new TableColumn("Szerzők");
        authorsCol.setPrefWidth(250);
        authorsCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("authors")); // unsafe op.

        TableColumn titleCol = new TableColumn("Cím");
        titleCol.setPrefWidth(250);
        titleCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("title")); // unsafe op.


        getColumns().addAll(authorsCol, titleCol);
        setEditable(false);
        setPlaceholder(new Label("Nincs megjeleníthető találat."));

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
            new PubWindow(getSelectionModel().getSelectedItem()).show();
        }
    }
}
