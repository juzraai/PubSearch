package pubsearch.gui.window;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.*;
import javafx.scene.text.Text;
import pubsearch.gui.GuiTools;

/**
 * Névjegyablak.
 *
 * @author Zsolt
 */
public class AboutWindow extends AWindow {

    public AboutWindow() {
        super("aboutWindowTitle", false, true);
        setScene(buildScene());
        setCSS();
    }

    private Scene buildScene() {
        LinearGradient textGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.web("#66AAFF")));

        Text appName = new Text("PubSearch");
        appName.getStyleClass().addAll("bold-text", "italic-text");
        appName.setFill(textGradient);
        appName.setStyle("-fx-font-size:42px;");
        appName.setEffect(GuiTools.reflAndShadow);

        Text myName = new Text("Jurányi Zsolt");
        myName.getStyleClass().addAll("bold-text", "italic-text");
        myName.setFill(textGradient);
        myName.setStyle("-fx-font-size:18px;");
        myName.setEffect(GuiTools.reflAndShadow);

        Text ver1 = new Text(texts.getString("version"));
        ver1.setFill(Color.WHITE);
        ver1.setEffect(GuiTools.shadow);
        Text ver2 = new Text("1.0");
        ver2.setFill(Color.WHITE);
        ver2.setEffect(GuiTools.shadow);
        Text rel1 = new Text(texts.getString("releaseDate"));
        rel1.setFill(Color.WHITE);
        rel1.setEffect(GuiTools.shadow);
        Text rel2 = new Text("15/05/2012");
        rel2.setFill(Color.WHITE);
        rel2.setEffect(GuiTools.shadow);
        Text mail1 = new Text("E-mail");
        mail1.setFill(Color.WHITE);
        mail1.setEffect(GuiTools.shadow);
        Text mail2 = new Text("zsolt.juranyi@gmail.com");
        mail2.setFill(Color.WHITE);
        mail2.setEffect(GuiTools.shadow);

        VBox left = new VBox(8);
        left.setPrefWidth(195);
        left.setAlignment(Pos.TOP_RIGHT);
        left.getChildren().addAll(ver1, rel1, mail1);

        VBox right = new VBox(8);
        right.setPrefWidth(195);
        right.setAlignment(Pos.TOP_LEFT);
        right.getChildren().addAll(ver2, rel2, mail2);

        /*Text thesis = new Text("Ez a program a Programtervező Informatikus BSc. szakdolgozatom.");
        thesis.setFill(Color.WHITE);
        thesis.setEffect(GuiTools.shadow);*/

        BorderPane center = new BorderPane();
        center.setTop(myName);
        center.setLeft(left);
        center.setRight(right);
        //center.setBottom(thesis);

        Button okButton = new Button("OK");
        okButton.setStyle("-fx-base:#000033;");
        okButton.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                AboutWindow.this.hide();
            }
        });

        BorderPane layout = new BorderPane();
        layout.setTop(appName);
        layout.setCenter(center);
        layout.setBottom(okButton);

        BorderPane.setAlignment(myName, Pos.CENTER);
        BorderPane.setMargin(myName, new Insets(30, 0, 30, 0));
        BorderPane.setAlignment(appName, Pos.CENTER);
        BorderPane.setMargin(appName, new Insets(10));
        /*BorderPane.setAlignment(thesis, Pos.CENTER);
        BorderPane.setMargin(thesis, new Insets(10, 0, 10, 0));*/
        BorderPane.setAlignment(okButton, Pos.CENTER);
        BorderPane.setMargin(okButton, new Insets(5, 0, 15, 0));

        final int WIDTH = 400;
        Stop[] gradientColors = new Stop[]{
            new Stop(0, Color.web("#0066FF")),
            new Stop(1, Color.web("#000033")),};
        RadialGradient gradient = new RadialGradient(0, 0, WIDTH / 2, 0, WIDTH, false, CycleMethod.NO_CYCLE, gradientColors);

        Scene scene = new Scene(layout, WIDTH, 300);
        scene.getRoot().setStyle("-fx-background-color: transparent;");
        scene.setFill(gradient);
        return scene;
    }
}
