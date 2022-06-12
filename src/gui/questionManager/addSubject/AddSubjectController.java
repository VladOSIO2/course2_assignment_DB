package gui.questionManager.addSubject;

import db.entities.queries.SubjectQuery;
import gui.GUIUtil;
import gui.SceneStarter;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.util.Objects;

public class AddSubjectController {
    //TODO: refactoring

    public void show(Pair<Integer, String> subject, boolean isUpdate, boolean suppressAlerts) {
        Parent root;
        try {
            root = FXMLLoader.load(Objects.requireNonNull(
                    AddSubjectController.class.getResource("/gui/questionManager/addSubject/addSubject.fxml/")));
            Stage stage = new Stage();
            stage.setTitle("Предмет");
            stage.setResizable(false);

            Label label = new Label(isUpdate ? "Зміна предмету" : "Додавання предмету");
            label.setLayoutX(12);
            label.setLayoutY(36);

            TextField tf = new TextField(subject.getValue());
            tf.setLayoutX(12);
            tf.setLayoutY(58);
            tf.setPrefSize(287, 25);

            Button button = new Button(isUpdate ? "Змінити" : "Додати");
            button.setLayoutX(12);
            button.setLayoutY(90);
            button.setPrefSize(287, 25);
            button.setOnAction(isUpdate ?
                    x -> updateSubject(stage, suppressAlerts, subject, tf.getText())
                    : x -> insertSubject(stage, suppressAlerts, tf.getText()));

            Pane pane = new Pane(root);
            pane.getChildren().addAll(button, tf, label);

            stage.setScene(new Scene(pane));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void updateSubject(
            Stage stage, boolean suppressAlerts,
            Pair<Integer, String> subject, String subjectNew) {
        if (suppressAlerts || GUIUtil.showConfirmationAlert(
                "Змінити назву предмету?",
                "Змінити назву предмету\n\tз\n%s\n\tна\n%s?"
                        .formatted(subject.getValue(), subjectNew))) {
            String res = SubjectQuery.updateSubject(subject.getKey(), subjectNew);
            if (res.contains("Помилка")) {
                GUIUtil.showErrorAlert(res);
            } else {
                if (!suppressAlerts) {
                    GUIUtil.showInfoAlert("Предмет успішно змінено!", res);
                }
                stage.close();
            }
        }
    }

    private static void insertSubject(Stage stage, boolean suppressAlerts, String subject) {
        if (suppressAlerts || GUIUtil.showConfirmationAlert(
                "Додати предмет?",
                "Додати предмет з назвою:\n" + subject + "?")) {
            String res = SubjectQuery.insertSubject(subject);
            if (res.contains("Помилка")) {
                GUIUtil.showErrorAlert(res);
            } else {
                if (!suppressAlerts) {
                    GUIUtil.showInfoAlert("Предмет успішно додано!", res);
                }
                stage.close();
            }
        }
    }

    public void cancel(ActionEvent actionEvent) {
        SceneStarter.exit(actionEvent);
    }
}
