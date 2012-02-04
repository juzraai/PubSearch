package pubsearch.gui.window;

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
    private final MainTab mainTab = new MainTab(this);
    private TabPane tabs = new TabPane();

    public MainWindow() {
        super("PubSearch", true, false);

        tabs.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabs.setTabMinWidth(50);
        tabs.setTabMaxWidth(200);
        tabs.getTabs().add(mainTab);

        Scene scene = new Scene(tabs, 640, 450);
        setScene(scene);
        setCSS();
    }

    @Override
    protected void onShownAction(WindowEvent event) {
        super.onShownAction(event);
        mainTab.focusAuthorField();
    }

    public TabPane getTabPane() {
        return tabs;
    }
}