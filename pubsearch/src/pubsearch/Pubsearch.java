package pubsearch;

import javafx.application.Application;
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
        AlertWindow.show("Teszt hibaüzi. Sok szót még ideírok, hogy több sorosan is le tudjam tesztelni!");
        if (true) return;

        ConfigModel.load();

        primaryStage = new MainWindow();
        if (Connection.tryInit()) {
            primaryStage.show();
        } else {
            if (Connection.getLastError() == Connection.SQL_ERROR) {
                ((MainWindow) primaryStage).configWindow.show();
            } else if (Connection.getLastError() == Connection.JPA_ERROR) {
                AlertWindow.show("Hiba történt az adatbáziskapcsolat felépítésekor (JPA_ERROR).");
                System.exit(1);
            }
        }
    }
}
