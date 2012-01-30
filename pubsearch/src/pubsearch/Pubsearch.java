package pubsearch;

import java.nio.charset.Charset;
import java.util.ResourceBundle;
import javafx.application.Application;
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
            ResourceBundle texts = ResourceBundle.getBundle("pubsearch.gui.texts.texts");
            if (Connection.getLastError() == Connection.SQL_ERROR) {
                ((MainWindow) stage).configWindow.show();
                AlertWindow.show(texts.getString("mysqlError"));
            } else if (Connection.getLastError() == Connection.JPA_ERROR) {
                AlertWindow.show(texts.getString("jpaError"));
            }
        }
    }
}
