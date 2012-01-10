package pubsearch.gui;

import java.io.IOException;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.text.Text;

/**
 * Névjegyablak.
 *
 * @author Zsolt
 */
public class AboutWindow extends AWindow {

    public AboutWindow() {
        super("Névjegy", false, true);
        setScene(buildScene());
        setCSS();
    }

    private Scene buildScene() {
        Reflection reflection = new Reflection();
        reflection.setFraction(0.75);

        DropShadow shadow = new DropShadow();
        shadow.setOffsetX(3.0f);
        shadow.setOffsetY(3.0f);
        shadow.setColor(Color.BLACK);

        Blend reflAndShadow = new Blend();
        reflAndShadow.setMode(BlendMode.OVERLAY);
        reflAndShadow.setBottomInput(reflection);
        reflAndShadow.setTopInput(shadow);

        LinearGradient textGradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, Color.WHITE), new Stop(1, Color.web("#66AAFF")));

        Text appName = new Text("PubSearch");
        appName.getStyleClass().addAll("bold-text", "italic-text");
        appName.setFill(textGradient);
        appName.setStyle("-fx-font-size:42px;");
        appName.setEffect(reflAndShadow);

        Text myName = new Text("Jurányi Zsolt");
        myName.getStyleClass().addAll("bold-text", "italic-text");
        myName.setFill(textGradient);
        myName.setStyle("-fx-font-size:18px;");
        myName.setEffect(reflAndShadow);

        /*
         * Verzió: 1.0
         * Kiadva: 2012.05.??
         * Web: _projekt a SourceForge-on_
         * E-mail: zsolt.juranyi@gmail.com
         *
         * Ez a program a BSc szakdolgozatom 2012.-ben az ELTE-IK-n.
         * Témavez?!
         */

        EventHandler<MouseEvent> linkOnMouseOver = new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                ((Text) event.getSource()).setUnderline(true);
                ((Text) event.getSource()).setFill(Color.web("#AADDFF"));
            }
        };
        EventHandler<MouseEvent> linkOnMouseOut = new EventHandler<MouseEvent>() {

            public void handle(MouseEvent event) {
                ((Text) event.getSource()).setUnderline(false);
                ((Text) event.getSource()).setFill(Color.web("#66AAFF"));
            }
        };

        Text ver1 = new Text("Verzió");
        ver1.setFill(Color.WHITE);
        ver1.setEffect(shadow);
        Text ver2 = new Text("1.0");
        ver2.setFill(Color.WHITE);
        ver2.setEffect(shadow);
        Text rel1 = new Text("Kiadás dátuma");
        rel1.setFill(Color.WHITE);
        rel1.setEffect(shadow);
        Text rel2 = new Text("2012.05.??");
        rel2.setFill(Color.WHITE);
        rel2.setEffect(shadow);
        /*Text web1 = new Text("Web");
        web1.setFill(Color.WHITE);
        web1.setEffect(shadow);
        final Text web2 = new Text("SourceForge projekt: 'PubSearch'");
        web2.setCursor(Cursor.HAND);
        web2.setFill(Color.web("#66AAFF"));
        web2.setEffect(shadow);
        web2.setOnMouseEntered(linkOnMouseOver);
        web2.setOnMouseExited(linkOnMouseOut);*/       

        VBox left = new VBox(8);
        left.setPrefWidth(195);
        left.setAlignment(Pos.TOP_RIGHT);
        left.getChildren().addAll(ver1, rel1);

        VBox right = new VBox(8);
        right.setPrefWidth(195);
        right.setAlignment(Pos.TOP_LEFT);
        right.getChildren().addAll(ver2, rel2);

        BorderPane center = new BorderPane();
        center.setTop(myName);
        center.setLeft(left);
        center.setRight(right);

        BorderPane layout = new BorderPane();
        layout.setTop(appName);

        layout.setCenter(center);

        BorderPane.setAlignment(myName, Pos.CENTER);
        BorderPane.setMargin(myName, new Insets(30, 0, 30, 0));
        BorderPane.setAlignment(appName, Pos.CENTER);
        BorderPane.setMargin(appName, new Insets(10));

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
