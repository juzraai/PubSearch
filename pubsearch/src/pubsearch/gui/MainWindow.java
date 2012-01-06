/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch.gui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import pubsearch.data.Publication;
/**
 *
 * @author Zsolt
 */
public class MainWindow extends Stage {
    
    public final ConfigWindow configWindow = new ConfigWindow((Stage)this);
    private ObservableList<Publication> results = FXCollections.observableArrayList();
    private TextField authorField = new TextField();
    private TextField titleField = new TextField();
    private CheckBox onlyLocalCheckBox = new CheckBox("Keresés csak a helyi adatbázisban");
    private final TableView<Publication> resultsView = new TableView<Publication>();
    private Label resultCountLabel = new Label("0 db találat a 'Szerző: blablabla és Cím: blablabla' keresésre, a művelet 0 KB adatforgalmat vett igénybe.");
    
    public MainWindow() {
        setTitle("Publikáció kereső");
        setScene(buildScene());
        setOnShown(new EventHandler<WindowEvent>(){

            public void handle(WindowEvent event) {
                Tools.centerizeStage((Stage)(MainWindow.this));
            }
        });        
    }

    /**
     * Felépíti az ablakot.
     * @return 
     */
    private Scene buildScene() {

        /*
         * Top
         */
        Label authorLabel = new Label("Szerző:");
        authorLabel.setLabelFor(authorField);
        authorLabel.setStyle("-fx-text-fill: white");

        authorField.setOnAction(new EventHandler<ActionEvent>() {
            
            public void handle(ActionEvent event) {
                startSearch();
            }
        });
        
        Label titleLabel = new Label("Cím:");
        titleLabel.setLabelFor(titleField);
        titleLabel.setStyle("-fx-text-fill: white");
        
        titleField.setOnAction(new EventHandler<ActionEvent>() {
            
            public void handle(ActionEvent event) {
                startSearch();
            }
        });
        
        onlyLocalCheckBox.setStyle("-fx-text-fill: #AFA");
        
        Button searchButton = new Button("Keresés!");
        searchButton.setPrefWidth(75);
        searchButton.setPrefHeight(45);
        searchButton.setStyle("-fx-base: #3AD;");
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            
            public void handle(ActionEvent event) {
                startSearch();
            }
        });
        
        Button editProxiesButton = new Button("Proxy...");
        editProxiesButton.setPrefWidth(100);
        editProxiesButton.setStyle("-fx-base: #D6F;");
        
        Button editDBConnButton = new Button("Adatbázis...");
        editDBConnButton.setPrefWidth(100);
        editDBConnButton.setStyle("-fx-base: #D33;");
        editDBConnButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                MainWindow.this.configWindow.show();
            }
        });
        
        Button aboutButton = new Button("Névjegy");
        aboutButton.setPrefWidth(100);
        aboutButton.setStyle("-fx-base: #3D6");
        
        GridPane top = new GridPane();
        top.setPadding(new Insets(12));
        top.setHgap(10);
        top.setVgap(10);
        top.add(authorLabel, 0, 0);
        top.add(authorField, 1, 0);
        top.add(titleLabel, 0, 1);
        top.add(titleField, 1, 1);
        top.add(onlyLocalCheckBox, 1, 2);
        top.add(searchButton, 2, 0, 1, 2);
        top.add(aboutButton, 3, 0);
        top.add(editProxiesButton, 3, 1);
        top.add(editDBConnButton, 3, 2);

        /*
         * Center
         */
        TableColumn authorsCol = new TableColumn("Szerzők");
        authorsCol.setPrefWidth(250);
        authorsCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("authors")); // unsafe op.
        TableColumn titleCol = new TableColumn("Cím");
        titleCol.setPrefWidth(250);
        titleCol.setCellValueFactory(new PropertyValueFactory<Publication, String>("title")); // unsafe op.

        resultsView.setPlaceholder(new Label("Nincs megjeleníthető találat."));
        resultsView.getColumns().addAll(authorsCol, titleCol);
        resultsView.setEditable(false);
        resultsView.setOnKeyPressed(new EventHandler<KeyEvent>() {
            
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    showPubWindow();
                }
            }
        });
        resultsView.setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                    
                    public void handle(MouseEvent event) {
                        if (event.getClickCount() > 1) {
                            showPubWindow();
                        }
                    }
                });
        
        resultCountLabel.getStyleClass().addAll("white-text", "bold-text");
        resultCountLabel.setTextAlignment(TextAlignment.CENTER);
        resultCountLabel.setWrapText(true);
        BorderPane.setAlignment(resultCountLabel, Pos.CENTER);
        
        BorderPane center = new BorderPane();
        center.setPadding(new Insets(12));        
        center.setCenter(resultsView);
        center.setTop(resultCountLabel);

        /*
         * Build
         */
        BorderPane layout = new BorderPane();
        layout.setPadding(new Insets(0));
        layout.setTop(top);
        layout.setCenter(center);
        
        Scene scene = new Scene(layout, 600, 300);
        scene.getStylesheets().add("pubsearch/gui/style.css");
        return scene;
    }

    /**
     * Eseménykezelő. Esemény: duplakattintás/ENTER a találati listában. Tevékenység: betölti a hivatkozó publikációkat.
     */
    private void showPubWindow() {
        if (resultsView.getSelectionModel().getSelectedIndex() > -1) {
            Publication p = resultsView.getSelectionModel().getSelectedItem();
            
            new PubWindow(p).show();
        }
    }

    /**
     * Eseménykezelő. Esemény: keresés gomb akciója. Tevékenység: elindítja a kereső algoritmust, és elérhetetlenné teszi a GUI-t (beviteli mező, keresés gomb).
     */
    private void startSearch() {
        if (!onlyLocalCheckBox.selectedProperty().get()) {
            // crawl
        }
        resultsView.setItems(FXCollections.observableArrayList(Publication.searchResults(authorField.getText(), titleField.getText())));        
    }
}
