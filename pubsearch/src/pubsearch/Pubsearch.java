package pubsearch;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import pubsearch.data.Connection;
import pubsearch.gui.window.AlertWindow;
import pubsearch.gui.window.MainWindow;

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

        Config.loadMySQLConfig();
        Config.loadProxyList();

        MainWindow stage = new MainWindow();
        if (Connection.tryInit()) {
            stage.show();
        } else {
            if (Connection.getLastError() == Connection.SQL_ERROR) {
                ((MainWindow) stage).configWindow.show();
                AlertWindow.show("Nem sikerült csatlakozni a MySQL adatbázishoz, kérlek add meg a megfelelő paramétereket!");
            } else if (Connection.getLastError() == Connection.JPA_ERROR) {
                AlertWindow.show("Hiba történt az adatbáziskapcsolat felépítésekor (JPA_ERROR).");
            }
        }
    }
}
