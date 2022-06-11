package gui.questionManager;

import db.entities.question.*;
import db.entities.user.UserType;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.login.DBSession;
import gui.questionManager.addSubject.AddSubjectController;
import gui.questionManager.addTheme.AddThemeController;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;
import utility.Util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuestionManagerController {
    @FXML private Button button_deleteQuestion;
    @FXML private Button button_updateQuestion;
    @FXML private Button button_deleteSubject;
    @FXML private Button button_addSubject;
    @FXML private Button button_deleteTheme;
    @FXML private Button button_addTheme;
    @FXML private Button button_updateSubject;
    @FXML private Button button_updateTheme;

    @FXML private TextField TF_searchSubject;
    @FXML private ListView<String> LV_subjects;
    @FXML private Label label_selectedSubject;

    @FXML private TextField TF_searchTheme;
    @FXML private ListView<String> LV_themes;
    @FXML private Label label_selectedTheme;
    @FXML private CheckBox check_searchThemeWithSubject;

    @FXML private ChoiceBox<String> chB_grade;

    @FXML private TextField TF_searchQuestion;
    @FXML private ListView<String> LV_questions;
    @FXML private Label label_selectedQuestion;
    @FXML private Label label_questionAuthor;
    @FXML private CheckBox check_questionsNotInQuizzes;

    private Map<Integer, String> subjectMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedSubject = new Pair<>(-1, "");

    private Map<Integer, String> themeMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedTheme = new Pair<>(-1, "");

    private Map<Integer, String> gradeMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedGrade = new Pair<>(-1, "");

    private Map<Integer, String> questionMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedQuestion = new Pair<>(-1, "");


    @FXML CheckBox check_suppressAlerts;
    private boolean suppressAlerts = false;

    @FXML
    private void initialize() throws SQLException {
        if (!DBSession.getType().equals(UserType.ADMIN)) {
            button_addSubject.setVisible(false);
            button_addTheme.setVisible(false);
            button_deleteSubject.setVisible(false);
            button_deleteTheme.setVisible(false);
        }

//        Subjects
        TF_searchSubject.textProperty().addListener(
                (observable -> fillSubjects()));
        fillSubjects();
        LV_subjects.getSelectionModel().selectedIndexProperty().addListener(
                ((observableValue, oldVal, newVal) -> {
                    String subject = LV_subjects.getSelectionModel().selectedItemProperty().get();
                    boolean isSelected = newVal.intValue() != -1;
                    if (isSelected) {
                        selectedSubject = new Pair<>(
                                new ArrayList<>(subjectMap.keySet()).get(newVal.intValue()),
                                subject
                        );
                        label_selectedSubject.setText(subject);
                    } else {
                        selectedSubject = new Pair<>(-1, "");
                        label_selectedSubject.setText("<Предмет>");
                    }
                    button_deleteSubject.setDisable(!isSelected);
                    button_updateSubject.setDisable(!isSelected);
                    fillThemes();
                    fillQuestions();
                })
        );

//        Themes
        TF_searchTheme.textProperty().addListener(
                (observable -> fillThemes()));
        fillThemes();
        LV_themes.getSelectionModel().selectedIndexProperty().addListener(
                ((observableValue, oldVal, newVal) -> {
                    String theme = LV_themes.getSelectionModel().selectedItemProperty().get();
                    boolean isSelected = newVal.intValue() != -1;
                    if (isSelected) {
                        selectedTheme = new Pair<>(
                                new ArrayList<>(themeMap.keySet()).get(newVal.intValue()),
                                theme
                        );
                        label_selectedTheme.setText(theme);
                    } else {
                        selectedTheme = new Pair<>(-1, "");
                        label_selectedTheme.setText("<Тема>");
                    }
                    fillQuestions();
                    button_deleteTheme.setDisable(!isSelected);
                    button_updateTheme.setDisable(!isSelected);
                })
        );

//        Grades
        gradeMap = GradeQuery.getGrades();
        ObservableList<String> gradeList = FXCollections.observableArrayList();
        gradeList.add("Будь-який");
        gradeList.addAll(gradeMap.values());
        chB_grade.setItems(gradeList);
        chB_grade.setValue("Будь-який");
        chB_grade.getSelectionModel().selectedIndexProperty().addListener(
                observable -> {
                    int i = ((ReadOnlyIntegerProperty) observable).getValue();
                    if (i == -1 || i == 0) {
                        selectedGrade = new Pair<>(-1, "");
                    } else {
                        int chosen = new ArrayList<>(gradeMap.keySet()).get(i - 1);
                        selectedGrade = new Pair<>(chosen, gradeMap.get(chosen));
                    }
                    fillQuestions();
                }
        );

//        Questions
        TF_searchQuestion.textProperty().addListener(
                (observable -> fillQuestions()));
        LV_questions.getSelectionModel().selectedIndexProperty().addListener(
                observable -> {
                    int i = ((ReadOnlyIntegerProperty) observable).getValue();
                    if (i == -1) {
                        selectedQuestion = new Pair<>(-1, "");
                        label_selectedQuestion.setText("<Питання>");
                        label_questionAuthor.setText("<Автор>");
                    } else {
                        int chosen = new ArrayList<>(questionMap.keySet()).get(i);
                        selectedQuestion = new Pair<>(chosen, questionMap.get(chosen));
                        label_selectedQuestion.setText(Util.splitStringOnLines(selectedQuestion.getValue(), 34));
                        try {
                            label_questionAuthor.setText(AuthorQuery.getAuthorByQuestion(chosen));
                        } catch (SQLException e) {
                            GUIUtil.showErrorAlert(e.getMessage());
                        }
                    }
                    button_deleteQuestion.setDisable(i == -1);
                    button_updateQuestion.setDisable(i == -1);
                }
        );
    }

    @FXML
    private void insertSubject() {
        new AddSubjectController().show(new Pair<>(-1, ""), false, suppressAlerts);
        fillSubjects();
    }

    @FXML
    private void updateSubject() {
        new AddSubjectController().show(
                new Pair<>(
                        selectedSubject.getKey(),
                        selectedSubject.getValue()),
                true,
                suppressAlerts);
        fillSubjects();
    }

    @FXML
    private void deleteSubject() {
        if (suppressAlerts || GUIUtil.showConfirmationAlert(
                "Видалити предмет?",
                "Видалити предмет: " + selectedSubject.getValue() + "?")) {
            String res = SubjectQuery.deleteSubject(selectedSubject.getKey());
            if (res.contains("Помилка")) {
                GUIUtil.showErrorAlert(res);
            } else {
                if (!suppressAlerts) {
                    GUIUtil.showInfoAlert("Предмет успішно видалено!", res);
                }
            }
        }
        fillSubjects();
    }

    @FXML
    private void insertTheme() throws IOException, SQLException {
        startAddTheme(false);
        fillThemes();
    }

    @FXML
    private void updateTheme() throws IOException, SQLException {
        startAddTheme(true);
        fillThemes();
    }

    private void startAddTheme(boolean isUpdate) throws IOException, SQLException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/gui/questionManager/addTheme/addTheme.fxml"));
        Parent root = fxmlLoader.load();
        AddThemeController controller = fxmlLoader.getController();
        controller.init(
                new Pair<>(selectedTheme.getKey(),selectedTheme.getValue()),
                isUpdate, suppressAlerts);
        Stage stage = new Stage();
        stage.setTitle("Додавання теми");
        stage.setScene(new Scene(root));
        stage.showAndWait();
    }

    @FXML
    private void deleteTheme() {
        if (suppressAlerts || GUIUtil.showConfirmationAlert(
                "Видалити тему?",
                "Видалити тему: " + selectedTheme.getValue() + "?")) {
            String res = ThemeQuery.deleteTheme(selectedTheme.getKey());
            if (res.contains("Помилка")) {
                GUIUtil.showErrorAlert(res);
            } else {
                if (!suppressAlerts) {
                    GUIUtil.showInfoAlert("Тему успішно видалено!", res);
                }
            }
        }
        fillThemes();
    }

    @FXML
    private void checkThemeSearch() {
        fillThemes();
    }

    @FXML
    private void chooseGrade(ActionEvent actionEvent) {
        fillQuestions();
    }

    @FXML
    private void addQuestion(ActionEvent actionEvent) {

    }

    @FXML
    private void deleteQuestion(ActionEvent actionEvent) {

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

    @FXML
    private void showAnswerOptions() {

    }

    @FXML
    private void toggleQuestionsNotInQuizzes() {
        fillQuestions();
    }

    private void fillSubjects() {
        String subjectPart = TF_searchSubject.getText();
        try {
            subjectMap = SubjectQuery.searchSubject(subjectPart);
        } catch (SQLException e) {
            GUIUtil.showErrorAlert(e.getMessage());
        }
        ObservableList<String> list = FXCollections.observableArrayList(subjectMap.values());
        LV_subjects.setItems(list);
    }

    private void fillThemes() {
        String themePart = TF_searchTheme.getText();
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
            GUIUtil.showErrorAlert(Util.splitStringOnLines(e.getMessage(), 60));
        }
        ObservableList<String> list = FXCollections.observableArrayList(themeMap.values());
        LV_themes.setItems(list);
    }

    private void fillQuestions() {
        String questionPart = TF_searchQuestion.getText();
        try {
            questionMap = QuestionQuery.getQuestions(
                    selectedSubject.getKey(),
                    selectedTheme.getKey(), selectedGrade.getKey(),
                    questionPart, check_questionsNotInQuizzes.isSelected());
            ObservableList<String> list = FXCollections.observableArrayList(questionMap.values());
            LV_questions.setItems(list);
        } catch (SQLException e) {
            GUIUtil.showErrorAlert(e.getMessage());
        }
    }

    @FXML
    private void toggleSuppressAlerts() {
        suppressAlerts = check_suppressAlerts.isSelected();
    }
}
