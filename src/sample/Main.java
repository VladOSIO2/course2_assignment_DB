package sample;

import db.entities.queries.QuizQuery;
import gui.SceneStarter;
import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneStarter.startSceneLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
