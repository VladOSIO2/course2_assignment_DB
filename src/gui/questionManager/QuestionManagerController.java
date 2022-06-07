package gui.questionManager;

import db.entities.question.SubjectQuery;
import db.entities.question.ThemeQuery;
import db.entities.user.UserType;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.login.DBSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionManagerController {
    @FXML private Button button_deleteSubject;
    @FXML private Button button_addSubject;
    @FXML private Button button_deleteTheme;
    @FXML private Button button_addTheme;

    @FXML private TextField TF_searchSubject;
    @FXML private ListView<String> LV_subjects;
    @FXML private Label label_selectedSubject;

    @FXML private TextField TF_searchTheme;
    @FXML private ListView<String> LV_themes;
    @FXML private Label label_selectedTheme;
    @FXML private CheckBox check_searchThemeWithSubject;

    @FXML private ChoiceBox<String> chB_grade;

    @FXML private TextField TF_searchQuestion;

    private Map<Integer, String> subjectMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedSubject = new Pair<>(-1, "");

    private Map<Integer, String> themeMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedTheme = new Pair<>(-1, "");

    @FXML
    private void initialize() {
        if (!DBSession.getType().equals(UserType.ADMIN)) {
            button_addSubject.setVisible(false);
            button_addTheme.setVisible(false);
            button_deleteSubject.setVisible(false);
            button_deleteTheme.setVisible(false);
        }

        TF_searchSubject.textProperty().addListener(
                ((observableValue, oldVal, newVal) -> fillSubjects(newVal))
        );
        fillSubjects("");
        LV_subjects.getSelectionModel().selectedIndexProperty().addListener(
                ((observableValue, oldVal, newVal) -> {
                    String subject = LV_subjects.getSelectionModel().selectedItemProperty().get();
                    if (newVal.intValue() != -1) {
                        selectedSubject = new Pair<>(
                                new ArrayList<>(subjectMap.keySet()).get(newVal.intValue()),
                                subject
                        );
                        label_selectedSubject.setText(subject);
                    } else {
                        selectedSubject = new Pair<>(-1, "");
                        label_selectedSubject.setText("<Предмет>");
                    }
                    fillThemes(TF_searchTheme.getText());
                })
        );

        TF_searchTheme.textProperty().addListener(
                ((observableValue, oldVal, newVal) -> fillThemes(newVal))
        );
        fillThemes("");
        LV_themes.getSelectionModel().selectedIndexProperty().addListener(
                ((observableValue, oldVal, newVal) -> {
                    String theme = LV_themes.getSelectionModel().selectedItemProperty().get();
                    if (newVal.intValue() != -1) {
                        selectedTheme = new Pair<>(
                                new ArrayList<>(themeMap.keySet()).get(newVal.intValue()),
                                theme
                        );
                        label_selectedTheme.setText(theme);
                    } else {
                        selectedTheme = new Pair<>(-1, "");
                        label_selectedTheme.setText("<Тема>");
                    }

                })
        );
    }

    @FXML
    private void checkThemeSearch() {
        fillThemes(TF_searchTheme.getText());
    }

    @FXML
    private void addSubject(ActionEvent actionEvent) {

    }

    @FXML
    private void deleteSubject(ActionEvent actionEvent) {

    }

    //TODO: control buttons

    @FXML
    private void returnBack(ActionEvent actionEvent) {
        SceneStarter.startSceneMenu(actionEvent);
    }

    @FXML
    private void logOut(ActionEvent actionEvent) throws SQLException {
        DBSession.logOut();
        SceneStarter.startSceneLogin(actionEvent);
    }

    @FXML
    private void exit(ActionEvent actionEvent) throws SQLException {
        DBSession.logOut();
        SceneStarter.exit(actionEvent);
    }

    public void showAnswerOptions() {
    }



    public void deleteTheme(ActionEvent actionEvent) {
    }

    public void addTheme(ActionEvent actionEvent) {
    }



    public void deleteQuestion(ActionEvent actionEvent) {
    }

    public void addQuestion(ActionEvent actionEvent) {
    }

    private void fillSubjects(String subjectPart) {
        try {
            subjectMap = SubjectQuery.searchSubject(subjectPart);
        } catch (SQLException e) {
            GUIUtil.showErrorAlert(e.getMessage());
        }
        ObservableList<String> list = FXCollections.observableArrayList(subjectMap.values());
        LV_subjects.setItems(list);
    }

    private void fillThemes(String themePart) {
        try {
            if (check_searchThemeWithSubject.isSelected()) {
                if (selectedSubject.getKey() == -1) {
                    GUIUtil.showErrorAlert("Для пошуку з предметом оберіть предмет!");
                    check_searchThemeWithSubject.setSelected(false);
                    return;
                } else {
                    themeMap = ThemeQuery.searchTheme(themePart, selectedSubject.getKey());
                }
            } else {
                themeMap = ThemeQuery.searchTheme(themePart);
            }
        } catch (SQLException e) {
            GUIUtil.showErrorAlert(e.getMessage());
        }
        ObservableList<String> list = FXCollections.observableArrayList(themeMap.values());
        LV_themes.setItems(list);
    }

}
