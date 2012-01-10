package pubsearch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pubsearch.config.ConfigModel;
import pubsearch.data.Connection;
import pubsearch.gui.AlertWindow;
import pubsearch.gui.MainWindow;

/**
 * A főprogram, és a főablak osztálya.
 *
 * @author Zsolt
 */
public class Pubsearch extends Application {

    /**
     * Elindítja az alkalmazást.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Létrehozza és megjeleníti a főablakot.
     * 
     * @param primaryStage 
     */
    @Override
    public void start(Stage primaryStage) {
        //new AboutWindow().show(); if(true)return;
        ConfigModel.load();

        Stage stage = new MainWindow();
        if (Connection.tryInit()) {
            stage.show();
        } else {
            if (Connection.getLastError() == Connection.SQL_ERROR) {
                ((MainWindow) stage).configWindow.show();
                AlertWindow.show("Nem sikerült csatlakozni a MySQL adatbázishoz, kérlek add meg a megfelelő paramétereket!");
            } else if (Connection.getLastError() == Connection.JPA_ERROR) {
                AlertWindow.show("Hiba történt az adatbáziskapcsolat felépítésekor (JPA_ERROR).");
                Platform.exit();
            }
        }
    }
}
