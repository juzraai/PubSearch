/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.stage.Stage;
import javax.print.PrintService;
import pubsearch.config.ConfigModel;
import pubsearch.data.Connection;
import pubsearch.data.PubDb;
import pubsearch.gui.ConfigWindow;
import pubsearch.gui.MainWindow;
import pubsearch.gui.Tools;

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
        System.out.println("hello");
    }

    /**
     * Létrehozza és megjeleníti a főablakot.
     * 
     * @param primaryStage 
     */
    @Override
    public void start(Stage primaryStage) {

        ConfigModel.load();

        primaryStage = new MainWindow();
        if (Connection.tryInit()) {
            primaryStage.show();
        } else {
            if (Connection.getLastError() == Connection.SQL_ERROR) {
                ((MainWindow) primaryStage).configWindow.show();
            } else if (Connection.getLastError() == Connection.JPA_ERROR) {
                // TODO ALERT ABLAK (adatbázis és programverzió nem konzisztens, módsult a séma, ilyesmi)
                System.err.println("JPA ERROR");
                System.exit(1);
            }
        }
    }
}
