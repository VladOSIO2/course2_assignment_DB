package sample;

import db.entities.question.ReportQuery;
import gui.SceneStarter;
import javafx.application.Application;
import javafx.stage.Stage;
import pdfbuilder.PDFBuilder;

import java.sql.SQLException;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneStarter.startSceneLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
