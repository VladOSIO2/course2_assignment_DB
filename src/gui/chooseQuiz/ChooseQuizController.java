package gui.chooseQuiz;

import db.entities.question.QuizQuery;
import db.entities.question.SubjectQuery;
import db.entities.question.ThemeQuery;
import gui.GUIUtil;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Pair;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ChooseQuizController {
    @FXML private ChoiceBox<String> chB_subject;
    @FXML private ChoiceBox<String> chB_theme;
    @FXML private TextField TF_searchQuiz;
    @FXML private CheckBox check_includeNotNowQuiz;
    @FXML private ListView<String> LV_quizzes;

    private Map<Integer, String> subjectMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedSubject = new Pair<>(-1, "");

    private Map<Integer, String> themeMap = new LinkedHashMap<>();
    private Pair<Integer, String> selectedTheme = new Pair<>(-1, "");

    private Map<Integer, String> quizMap = new LinkedHashMap<>();

    @FXML
    private void initialize() throws SQLException {
//        subjects
        fillSubjects();
        chB_subject.getSelectionModel().selectedIndexProperty().addListener(
                observable -> {
                    int id = ((ReadOnlyIntegerProperty) observable).get();
                    if (id == 0 || id == -1) {
                        selectedSubject = new Pair<>(-1, "");
                    } else {
                        int chosen = new ArrayList<>(subjectMap.keySet()).get(id - 1);
                        selectedSubject = new Pair<>(chosen, subjectMap.get(chosen));
                    }
                    fillThemes();
                    fillQuizzes();
                }
        );
//        themes
        fillThemes();
        chB_theme.getSelectionModel().selectedIndexProperty().addListener(
                observable -> {
                    int id = ((ReadOnlyIntegerProperty) observable).get();
                    if (id == 0 || id == -1) {
                        selectedTheme = new Pair<>(-1, "");
                    } else {
                        int chosen = new ArrayList<>(themeMap.keySet()).get(id - 1);
                        selectedTheme = new Pair<>(chosen, themeMap.get(chosen));
                    }
                    fillQuizzes();
                }
        );
//        quizzes
        fillQuizzes();
        TF_searchQuiz.textProperty().addListener(
                observable -> fillQuizzes()
        );
    }

    @FXML
    private void checkIncludeNotNowQuiz() {
        fillQuizzes();
    }

    @FXML
    private void cancel(ActionEvent actionEvent) {

    }

    @FXML
    private void selectQuiz() {
        int selected = LV_quizzes.getSelectionModel().getSelectedIndex();
        if (selected == -1) {
            GUIUtil.showErrorAlert("Оберіть тестування!");
            return;
        }
        int key = new ArrayList<>(quizMap.keySet()).get(selected);
        Pair<Integer, String> selectedQuiz = new Pair<>(key, quizMap.get(key));
    }

    private void fillSubjects() throws SQLException {
        subjectMap = SubjectQuery.searchSubject("");
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("<Предмет>");
        list.addAll(subjectMap.values());
        chB_subject.setItems(list);
    }

    private void fillThemes() {
        try {
            if (selectedSubject.getKey() == -1) {
                themeMap = ThemeQuery.searchTheme("");
            } else {
                themeMap = ThemeQuery.searchTheme("", selectedSubject.getKey());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ObservableList<String> list = FXCollections.observableArrayList();
        list.add("<Тема>");
        list.addAll(themeMap.values());
        chB_theme.setItems(list);
    }

    private void fillQuizzes() {
        try {
            quizMap = QuizQuery.getQuizzes(
                    TF_searchQuiz.getText(),
                    selectedSubject.getKey(),
                    selectedTheme.getKey(),
                    check_includeNotNowQuiz.isSelected());
            ObservableList<String> list = FXCollections.observableArrayList();
            list.addAll(quizMap.values());
            LV_quizzes.setItems(list);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
