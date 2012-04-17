package hu.juranyi.zsolt.pubsearch.gui.window;

import hu.juranyi.zsolt.pubsearch.data.Exporter;
import hu.juranyi.zsolt.pubsearch.data.Publication;
import hu.juranyi.zsolt.pubsearch.gui.control.PubTable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Export window.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class ExportWindow extends AWindow {

    private PubTable pubTable;

    public ExportWindow() {
        super("exportWindowTitle", false, true);
        setScene(buildScene());
        setCSS();
    }

    public void setPubTable(PubTable pubTable) {
        this.pubTable = pubTable;
    }

    private Scene buildScene() {
        RadioButton scopeAll = new RadioButton(texts.getString("exportAll"));
        scopeAll.getStyleClass().add("white-text");
        scopeAll.setSelected(true);
        final RadioButton scopeSel = new RadioButton(texts.getString("exportSelected"));
        scopeSel.getStyleClass().add("white-text");
        ToggleGroup scope = new ToggleGroup();
        scope.getToggles().addAll(scopeAll, scopeSel);

        final ChoiceBox<String> format = new ChoiceBox<String>();
        format.getItems().addAll(Exporter.getFormatList());
        format.getSelectionModel().select(0);

        //TODO radio buttons for delimiters: \n, \n\n, custom+textfield

        Button export = new Button(texts.getString("doExport"));
        export.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                doExport(scopeSel.selectedProperty().get(), format.getSelectionModel().getSelectedIndex());
                ExportWindow.this.hide();
            }
        });

        VBox controls = new VBox(10);
        controls.setPadding(new Insets(10));
        controls.getChildren().addAll(scopeAll, scopeSel, format);

        BorderPane layout = new BorderPane();
        layout.setCenter(controls);
        layout.setBottom(export);
        BorderPane.setAlignment(export, Pos.CENTER);
        BorderPane.setMargin(export, new Insets(20));

        return new Scene(layout, 200, 160);
    }

    private void doExport(boolean onlySelected, int formatIndex) {
        ObservableList<Publication> pubs;
        if (onlySelected) {
            pubs = pubTable.getSelectionModel().getSelectedItems();
        } else {
            pubs = pubTable.getItems();
        }

        StringBuilder export = new StringBuilder();
        for (Publication p : pubs) {
            Exporter e = new Exporter(p);
            export.append(e.export(formatIndex));
            export.append("\n\n");
        }

        FileChooser fc = new FileChooser();
        fc.setTitle(texts.getString("exportDialogTitle"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(texts.getString("allFiles") + " (*.*)", "*.*"));

        File f = fc.showSaveDialog(this);
        if (null != f) {
            BufferedWriter w = null;
            try {
                w = new BufferedWriter(new FileWriter(f));
                w.write(export.toString().trim());
                w.newLine();
            } catch (IOException e) {
                System.err.println("Cannot export publications.");
            } finally {
                if (w != null) {
                    try {
                        w.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
