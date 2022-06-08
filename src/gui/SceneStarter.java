package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class SceneStarter {
    private static void startScene(ActionEvent actionEvent, String filePath, String sceneTitle) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(SceneStarter.class.getResource(filePath)));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            stage.setTitle(sceneTitle);
            stage.setResizable(false);
            if (actionEvent != null) {
                ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void startSceneMenu (ActionEvent actionEvent) {
        startScene(actionEvent, "/gui/menu/menuPane.fxml", "Меню");
    }

    public static void startSceneRegistration (ActionEvent actionEvent) {
        startScene(actionEvent, "/gui/registration/registrationPane.fxml", "Реєстрація");
    }

    public static void startSceneLogin (ActionEvent actionEvent) {
        startScene(actionEvent, "/gui/login/loginPane.fxml", "Авторизація");
    }

    public static void startSceneLogin () {
        startScene(null, "/gui/login/loginPane.fxml", "Авторизація");
    }

    public static void startSceneQuestionManager(ActionEvent actionEvent) {
        startScene(actionEvent, "/gui/questionManager/questionManagerPane.fxml", "Менеджер питань");
    }

    public static void startSceneQuizStats(ActionEvent actionEvent) {
        startScene(actionEvent, "/gui/quizStats/quizStatisticsPane.fxml", "Статистика питань");
    }

    public static void startSceneLogs(ActionEvent actionEvent) {
        startScene(actionEvent, "/gui/logs/logsInfoPane.fxml", "Логи");
    }

    public static void startSceneGeneralStats(ActionEvent actionEvent) {
        startScene(actionEvent, "/gui/generalStats/generalStatsPane.fxml", "Загальна статистика");
    }

    public static void exit(ActionEvent actionEvent) {
        if (actionEvent != null) {
            ((Node)(actionEvent.getSource())).getScene().getWindow().hide();
        }
    }
}
