package pubsearch.gui.window;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.stage.WindowEvent;
import pubsearch.gui.tab.MainTab;

/**
 * A program főablaka.
 *
 * @author Zsolt
 */
public class MainWindow extends AWindow {

    public final ConfigWindow configWindow = new ConfigWindow(this);
    private TabPane tabs = new TabPane();

    public MainWindow() {
        super("PubSearch", true, false);

        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabs.setTabMinWidth(50);
        tabs.setTabMaxWidth(200);
        final MainTab mainTab = new MainTab(this);
        tabs.getTabs().add(mainTab);
        
        Scene scene = new Scene(tabs, 550, 400);
        setScene(scene);
        setCSS();
        setOnShown(new EventHandler<WindowEvent>(){

            public void handle(WindowEvent event) {
                mainTab.focusAuthorField();
            }
        });
    }
    
    public TabPane getTabPane() {
        return tabs;
    }
}