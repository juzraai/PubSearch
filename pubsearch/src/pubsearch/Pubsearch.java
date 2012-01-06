/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pubsearch;

import java.sql.SQLException;
import javafx.application.Application;
import javafx.stage.Stage;
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
        
        // TODO load config
       
        primaryStage = new MainWindow();        
        try {
            Connection.init(); // azért inicializálunk itt, hogy ne futás közben fagyjon ki a program    
            //new PubDb("alma");
            primaryStage.show();
        } catch (SQLException ex) {
            new ConfigWindow(primaryStage).show();
        } catch (Throwable t)
        {
            // TODO más kivételt a JPA dobhat, arra valami AlertBox szerű cucc kéne
        }
    }
}
