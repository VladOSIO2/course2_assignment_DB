package sample;

import gui.SceneStarter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.LinkedHashMap;
import java.util.Map;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneStarter.startSceneLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
