package hu.juranyi.zsolt.pubsearch.gui.tab;

import hu.juranyi.zsolt.pubsearch.data.Publication;
import hu.juranyi.zsolt.pubsearch.gui.control.LabelEx;
import hu.juranyi.zsolt.pubsearch.gui.control.PubTable;
import hu.juranyi.zsolt.pubsearch.gui.window.MainWindow;
import java.awt.Desktop;
import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

/**
 * Tab which shows the details of a publication.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class PubTab extends Tab {

    private MainWindow mainWindow;
    private Publication p;
    private final ResourceBundle texts = ResourceBundle.getBundle("pubsearch.gui.texts.texts");

    static {
        Velocity.init();
    }

    /**
     * Sets up the tab. BibTeX and cited by list will be shown only when not null/empty.
     * Export tab will shown only if there are any *.vm files in "formats" directory.
     * @param mainWindow MainWindow which hold the tabs.
     * @param p Publication to be displayed.
     */
    public PubTab(MainWindow mainWindow, Publication p) {
        super(p.getTitle());
        this.mainWindow = mainWindow;
        this.p = p;

        /*
         * Details
         */
        Label authorsLabel1 = new LabelEx(texts.getString("authorsLabel")).bold();
        Label titleLabel1 = new LabelEx(texts.getString("titleLabel")).bold();
        Label yearLabel1 = new LabelEx(texts.getString("yearLabel")).bold();
        Label dbLabel1 = new LabelEx(texts.getString("databaseLabel")).bold();
        Label urlLabel1 = new LabelEx(texts.getString("urlLabel")).bold();

        dbLabel1.setMinWidth(75);

        Label authorsLabel2 = new LabelEx(p.getAuthors()).italic();
        authorsLabel2.setWrapText(true);

        Label titleLabel2 = new LabelEx(p.getTitle()).italic();
        titleLabel2.setWrapText(true);

        Integer y = p.getYear();
        String ys = (null == y || -1 == y) ? texts.getString("unknownYear") : y.toString();
        Label yearLabel2 = new LabelEx(ys).italic();

        Label dbLabel2 = new LabelEx(p.getDbName()).italic();
        final Hyperlink urlLabel2 = new Hyperlink(p.getUrl());
        urlLabel2.setOnMouseClicked(new EventHandler<MouseEvent>() {

            public void handle(MouseEvent arg0) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop d = Desktop.getDesktop();
                        if (d.isSupported(Desktop.Action.BROWSE)) {
                            d.browse(new URI(urlLabel2.getText()));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

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
        if (null != p.getBibtex()) {
            final TextArea bibtexTA = new TextArea(p.getBibtex());
            bibtexTA.setEditable(false);

            Button copyButton = new Button(texts.getString("copyToClipboard"));
            copyButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    copyTextAreaContent(bibtexTA);
                }
            });

            Button saveButton = new Button(texts.getString("exportToFile"));
            saveButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    saveTextAreaContent(bibtexTA);
                }
            });

            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            buttons.setPadding(new Insets(10));
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
         * Export citation
         */
        final List<String> vmFiles = new ArrayList<String>();
        File confDir = new File("formats");
        String[] confFiles = confDir.list();
        if (null != confFiles) {
            for (String f : confFiles) {
                if (f.endsWith(".vm")) {
                    vmFiles.add("formats" + File.separator + f);
                }
            }
        }
        if (!vmFiles.isEmpty()) {

            final VelocityContext context = new VelocityContext();
            context.put("authors", p.getAuthors());
            context.put("title", p.getTitle());
            if (-1 < p.getYear()) {
                context.put("year", p.getYear());
            }
            if (null != p.getUrl()) {
                context.put("url", p.getUrl());
            }

            final TextArea exportTA = new TextArea();
            exportTA.setEditable(false);

            ChoiceBox<String> formatCB = new ChoiceBox<String>();
            for (String t : vmFiles) {
                t = t.substring("formats".length() + 1);
                t = t.substring(0, t.length() - 3);
                formatCB.getItems().add(t);
            }
            formatCB.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {

                public void changed(ObservableValue<? extends Number> ov, Number oldValue, Number newValue) {
                    exportTA.setText("");
                    StringWriter sw = new StringWriter();
                    try {
                        Template template = Velocity.getTemplate(vmFiles.get(newValue.intValue()));
                        template.merge(context, sw);
                    } finally {
                        exportTA.setText(sw.toString());
                    }
                }
            });

            Button copyButton = new Button(texts.getString("copyToClipboard"));
            copyButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    copyTextAreaContent(exportTA);
                }
            });

            Button saveButton = new Button(texts.getString("exportToFile"));
            saveButton.setOnAction(new EventHandler<ActionEvent>() {

                public void handle(ActionEvent event) {
                    saveTextAreaContent(exportTA);
                }
            });

            HBox buttons = new HBox(10);
            buttons.setAlignment(Pos.CENTER_RIGHT);
            buttons.getChildren().addAll(copyButton, saveButton);

            BorderPane selectorAndButtons = new BorderPane();
            selectorAndButtons.setLeft(formatCB);
            selectorAndButtons.setRight(buttons);
            selectorAndButtons.setPadding(new Insets(10));

            BorderPane exportTabLayout = new BorderPane();
            exportTabLayout.setTop(selectorAndButtons);
            exportTabLayout.setCenter(exportTA);
            BorderPane.setMargin(exportTA, new Insets(0, 10, 10, 10));

            Tab exportTab = new Tab(texts.getString("exportTab"));
            exportTab.setContent(exportTabLayout);
            tabs.getTabs().add(exportTab);
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

    private void copyTextAreaContent(TextArea textArea) {
        textArea.selectAll();
        textArea.copy();
        textArea.deselect();
    }

    private void saveTextAreaContent(TextArea textArea) {
        FileChooser fc = new FileChooser();
        fc.setTitle(texts.getString("saveDialogTitle"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(texts.getString("allFiles") + " (*.*)", "*.*"));

        File f = fc.showSaveDialog(PubTab.this.mainWindow);

        BufferedWriter w = null;
        try {
            w = new BufferedWriter(new FileWriter(f));
            w.write(textArea.getText());
            w.newLine();
        } catch (IOException e) {
            System.err.println("Cannot save TextArea content.");
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