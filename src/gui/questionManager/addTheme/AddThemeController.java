package gui.questionManager.addTheme;

import db.entities.question.SubjectQuery;
import db.entities.question.ThemeQuery;
import gui.GUIUtil;
import gui.SceneStarter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class AddThemeController {
    @FXML private ChoiceBox<String> chB_subject;
    @FXML private TextField TF_theme;
    @FXML private Button button_theme;

//    initializes upon init() controller instance call
    private Pair<Integer, String> theme;
    private boolean suppressAlerts = false;
    private boolean isUpdate = false;

//    controller private variables
    private Map<Integer, String> subjectMap;

    public void init(
            Pair<Integer, String> theme, boolean isUpdate,
            boolean suppressAlerts) throws SQLException {
        this.theme = theme;
        this.isUpdate = isUpdate;
        this.suppressAlerts = suppressAlerts;

        chB_subject.setValue(SubjectQuery.getSubjectByTheme(theme.getKey()));
        button_theme.setText(isUpdate ? "Змінити тему" : "Додати тему");
        TF_theme.setText(theme.getValue());
    }

    @FXML
    private void initialize() throws SQLException {
        subjectMap = SubjectQuery.searchSubject("");
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(subjectMap.values());
        chB_subject.setItems(list);
    }

    @FXML
    private void insertOrUpdateTheme(ActionEvent actionEvent) {
        if (isUpdate) {
            updateTheme(actionEvent);
        } else {
            insertTheme(actionEvent);
        }
    }

    private void updateTheme(ActionEvent actionEvent) {
        if (validateValues() && (suppressAlerts || GUIUtil.showConfirmationAlert(
                "Змінити тему?",
                "Змінити тему: " + TF_theme.getText() + "?"))) {
            String res = ThemeQuery.updateTheme(theme.getKey(), getSubjectID(), TF_theme.getText());
            if (res.contains("Помилка")) {
                GUIUtil.showErrorAlert(res);
            } else {
                if (!suppressAlerts) {
                    GUIUtil.showInfoAlert("Тему успішно змінено!", res);
                }
                SceneStarter.exit(actionEvent);
            }
        }
    }

    private void insertTheme(ActionEvent actionEvent) {
        if (validateValues() && (suppressAlerts || GUIUtil.showConfirmationAlert(
                "Додати тему?",
                "Додати тему: " + TF_theme.getText() + "?"))) {
            String res = ThemeQuery.insertTheme(TF_theme.getText(), getSubjectID());
            if (res.contains("Помилка")) {
                GUIUtil.showErrorAlert(res);
            } else {
                if (!suppressAlerts) {
                    GUIUtil.showInfoAlert("Тему успішно додано!", res);
                }
                SceneStarter.exit(actionEvent);
            }
        }

    }

    private int getSubjectID() {
        int chosen = chB_subject.getSelectionModel().getSelectedIndex();
        if (chosen == -1) {
            return -1;
        }
        return new ArrayList<>(subjectMap.keySet()).get(chosen);
    }

    private boolean validateValues() {
        int subjectIndex = chB_subject.getSelectionModel().getSelectedIndex();
        if (subjectIndex == -1) {
            GUIUtil.showErrorAlert("Оберіть предмет!");
            return false;
        }
        if (TF_theme.getText().equals("")) {
            GUIUtil.showErrorAlert("Введіть назву теми!");
            return false;
        }
        return true;
    }

    @FXML
    private void cancel(ActionEvent actionEvent) {
        SceneStarter.exit(actionEvent);
    }
}

