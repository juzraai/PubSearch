package hu.juranyi.zsolt.pubsearch.gui.window;

import hu.juranyi.zsolt.pubsearch.data.Exporter;
import hu.juranyi.zsolt.pubsearch.data.Publication;
import hu.juranyi.zsolt.pubsearch.gui.control.LabelEx;
import hu.juranyi.zsolt.pubsearch.gui.control.PubTable;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

/**
 * Export window.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class ExportWindow extends AWindow {

    private static String[] delimiters = {";",
        "\\t"};
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
        LabelEx scope = new LabelEx(texts.getString("exportScope")).white().shadow();
        LabelEx method = new LabelEx(texts.getString("exportMethod")).white().shadow();
        LabelEx format = new LabelEx(texts.getString("exportFormat")).white().shadow();
        LabelEx delim = new LabelEx(texts.getString("exportDelimiter")).white().shadow();

        final ChoiceBox<String> formatCB = new ChoiceBox<String>();
        formatCB.getItems().addAll(Exporter.getFormatList());
        formatCB.getSelectionModel().select(0);

        final ChoiceBox<String> delimCB = new ChoiceBox<String>();
        delimCB.getItems().addAll(delimiters);
        delimCB.getSelectionModel().select(0);

        RadioButton scopeAll = new RadioButton(texts.getString("exportAll"));
        scopeAll.getStyleClass().add("white-text");
        scopeAll.setSelected(true);

        final RadioButton scopeSel = new RadioButton(texts.getString("exportSelected"));
        scopeSel.getStyleClass().add("white-text");

        ToggleGroup scopeGroup = new ToggleGroup();
        scopeGroup.getToggles().addAll(scopeAll, scopeSel);


        final RadioButton citation = new RadioButton(texts.getString("exportCitation"));
        citation.getStyleClass().add("white-text");

        final RadioButton csv = new RadioButton(texts.getString("exportCSV"));
        csv.getStyleClass().add("white-text");

        EventHandler<ActionEvent> refresh = new EventHandler<ActionEvent>() {

            public void handle(ActionEvent t) {
                formatCB.setDisable(csv.selectedProperty().get());
                delimCB.setDisable(citation.selectedProperty().get());
            }
        };

        if (Exporter.getFormatList().isEmpty()) {
            citation.setDisable(true);
            csv.setSelected(true);
        } else {
            citation.setSelected(true);
        }
        refresh.handle(null);

        citation.setOnAction(refresh);
        csv.setOnAction(refresh);

        ToggleGroup methodGroup = new ToggleGroup();
        methodGroup.getToggles().addAll(citation, csv);

        Button export = new Button(texts.getString("doExport"));
        export.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                doExport(scopeSel.selectedProperty().get(), citation.selectedProperty().get(), formatCB.getSelectionModel().getSelectedIndex(), delimCB.getSelectionModel().getSelectedIndex());
                ExportWindow.this.hide();
            }
        });

        VBox left = new VBox(10);
        left.getChildren().addAll(scope, scopeAll, scopeSel, method, citation, csv);
        VBox right = new VBox(10);
        right.getChildren().addAll(format, formatCB, delim, delimCB);
        VBox.setMargin(scopeAll, new Insets(0, 0, 0, 10));
        VBox.setMargin(scopeSel, new Insets(0, 0, 10, 10));
        VBox.setMargin(citation, new Insets(0, 0, 0, 10));
        VBox.setMargin(csv, new Insets(0, 0, 10, 10));
        VBox.setMargin(formatCB, new Insets(0, 0, 10, 10));
        VBox.setMargin(delimCB, new Insets(0, 0, 10, 10));

        GridPane controls = new GridPane();
        controls.setPadding(new Insets(10));
        controls.add(left, 0, 0);
        controls.add(right, 1, 0);

        BorderPane layout = new BorderPane();
        layout.setCenter(controls);
        layout.setBottom(export);
        BorderPane.setAlignment(export, Pos.CENTER);
        BorderPane.setMargin(export, new Insets(20));
        BorderPane.setMargin(left, new Insets(10));

        return new Scene(layout, 350, 250);
    }

    private void doExport(boolean onlySelected, boolean citationMethod, int formatIndex, int delimIndex) {
        ObservableList<Publication> pubs;
        if (onlySelected) {
            pubs = pubTable.getSelectionModel().getSelectedItems();
        } else {
            pubs = pubTable.getItems();
        }

        StringBuilder export = new StringBuilder();
        if (citationMethod) {
            for (Publication p : pubs) {
                Exporter e = new Exporter(p);
                export.append(e.export(formatIndex));
                export.append("\n\n");
            }
        } else { // CSV mode
            String d = delimiters[delimIndex];
            if (d.equals("\\t")) d = "\t";
            for(Publication p:pubs) {
                export.append(p.getDbName());
                export.append(d);
                export.append(p.getAuthors());
                export.append(d);
                export.append(p.getTitle());
                export.append(d);
                export.append(p.getYearAsString());
                export.append(d);
                export.append(p.getCitedByCount());
                export.append("\n");
            }
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
