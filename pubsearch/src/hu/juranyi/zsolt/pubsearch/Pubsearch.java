package hu.juranyi.zsolt.pubsearch;

import hu.juranyi.zsolt.pubsearch.data.Connection;
import hu.juranyi.zsolt.pubsearch.gui.window.AlertWindow;
import hu.juranyi.zsolt.pubsearch.gui.window.MainWindow;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main application.
 * This contains the initialization of the application. Loads the configurations,
 * then tries to connect to the database. Shows the main window on success.
 *
 * @author Jurányi Zsolt (JUZRAAI.ELTE)
 */
public class Pubsearch extends Application {

    /**
     * Launches the JavaFX application.
     *
     * @param args Command line parameters - has no effect.
     */
    public static void main(String[] args) {
        Application.launch(args);
    }

    /**
     * Loads configurations, creates main window, then connects to the database.
     * If connection fails, shows up the configuration window instead of the
     * main window.
     *
     * @param primaryStage Output parameter, it will contain the main window.
     */
    @Override
    public void start(Stage primaryStage) {

        Config.loadMySQLConfig();
        Config.loadProxyList();

        MainWindow stage = new MainWindow();
        primaryStage = stage;
        if (Connection.tryInit()) {
            stage.show();
        } else {
            ResourceBundle texts = ResourceBundle.getBundle("hu.juranyi.zsolt.pubsearch.gui.texts.texts");
            if (Connection.getLastError() == Connection.SQL_ERROR) {
                stage.configWindow.show();
                AlertWindow.show(texts.getString("mysqlError"));
            } else if (Connection.getLastError() == Connection.JPA_ERROR) {
                AlertWindow.show(texts.getString("jpaError"));
            }
        }
    }
}
