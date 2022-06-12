package gui.questionManager.questionConstructor;

import db.entities.Question;
import db.entities.queries.*;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.login.DBSession;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import utility.Util;

import java.sql.SQLException;
import java.util.*;

public class QuestionConstructorController {
    @FXML private ChoiceBox<String> chB_subject;
    @FXML private ChoiceBox<String> chB_theme;

    @FXML private Button button_setAAsRight;
    @FXML private VBox vBox_answers;
    @FXML private ChoiceBox<String> chB_grade;
    @FXML private TextField TF_q;
    @FXML private ChoiceBox<String> chB_answers;
    @FXML private TextField TF_a;
    @FXML private Button button_deleteAnswer;
    @FXML private Button button_updateAnswer;
    @FXML private Label label_author;

//    initializes upon calling init() from another scene
    private int questionID;

//    scene variables
    private Map<Integer, String> subjectMap;
    private Map<Integer, String> themeMap;
    private int authorID;
    private Map<Integer, String> gradeMap;
    private List<String> answers;
    private String rightAnswer;
    private String chosenAnswer;
    private boolean suppressAlerts;


    @FXML
    private void initialize() throws SQLException {
//        subject & theme
        subjectMap = SubjectQuery.searchSubject("");
        chB_subject.setItems(FXCollections.observableArrayList(subjectMap.values()));
        chB_subject.getSelectionModel().selectedIndexProperty().addListener(
                observable -> {
                    int i = ((ReadOnlyIntegerProperty) observable).get();
                    int subjectID = new ArrayList<>(subjectMap.keySet()).get(i);
                    try {
                        themeMap = ThemeQuery.searchTheme("", subjectID);
                        chB_theme.setItems(FXCollections.observableArrayList(
                                themeMap.values()));
                        chB_theme.setDisable(false);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
        );
//        grade
        gradeMap = GradeQuery.getGrades();
        chB_grade.setItems(FXCollections.observableArrayList(gradeMap.values()));
//        answers
        answers = new ArrayList<>();
        rightAnswer = "";
        chB_answers.getSelectionModel().selectedItemProperty().addListener(
                (o, newS, oldS) -> {
                    chosenAnswer = oldS;
                    TF_a.setText(chosenAnswer);
                    button_deleteAnswer.setDisable(false);
                    button_updateAnswer.setDisable(false);
                    button_setAAsRight.setDisable(false);
                }
        );
    }

    @FXML
    private void setAAsRight() {
        rightAnswer = chosenAnswer;
        fillVBox();
        clearField();
    }

    @FXML
    private void deleteAnswer() {
        answers.remove(chosenAnswer);
        if (chosenAnswer.equals(rightAnswer)) {
            rightAnswer = "";
        }
        fillVBox();
        clearField();
    }

    @FXML
    private void updateAnswer() {
        String text = TF_a.getText();
        if (chosenAnswer.equals(rightAnswer)) {
            rightAnswer = text;
        }
        answers.remove(chosenAnswer);
        answers.add(text);
        fillVBox();
        clearField();
    }

    @FXML
    private void clearField() {
        chB_answers.setValue("");
        chosenAnswer = "";
        TF_a.setText("");
        button_deleteAnswer.setDisable(true);
        button_updateAnswer.setDisable(true);
        button_setAAsRight.setDisable(true);
        chB_answers.setItems(FXCollections.observableArrayList(answers));
    }

    @FXML
    private void insertAnswer() {
        String text = TF_a.getText();
        if (text.isBlank()) {
            GUIUtil.showErrorAlert("Введіть текст відповіді!");
            return;
        }
        answers.add(text);
        TF_a.setText("");
        fillVBox();
        clearField();
    }

    @FXML
    private void saveQuestion(ActionEvent actionEvent) {
//        theme
        int themeSelected = chB_theme.getSelectionModel().getSelectedIndex();
        if (themeSelected == -1) {
            GUIUtil.showErrorAlert("Оберіть тему!");
            return;
        }
        int themeID = new ArrayList<>(themeMap.keySet()).get(themeSelected);

//        grade
        int gradeSelected = chB_grade.getSelectionModel().getSelectedIndex();
        if (gradeSelected == -1) {
            GUIUtil.showErrorAlert("Оберіть рівень складності!");
            return;
        }
        int gradeID = new ArrayList<>(gradeMap.keySet()).get(gradeSelected);
        String text = TF_q.getText();

        if (answers.size() < 2) {
            GUIUtil.showErrorAlert("У питання має бути хоча б два варіанти відповіді!");
            return;
        }

        if (rightAnswer.isBlank() || !answers.contains(rightAnswer)) {
            GUIUtil.showErrorAlert("Питання має мати правильну відповідь!");
            return;
        }

        if (new HashSet<>(answers).size() != answers.size()) {
            GUIUtil.showErrorAlert("Відповіді на питання не мають повторюватись!!");
            return;
        }

        Question question = new Question(
                questionID, authorID, themeID, gradeID, text,
                answers, rightAnswer
        );
        System.out.println();


        if (suppressAlerts || GUIUtil.showConfirmationAlert(
                "Додати/змінити питання?",
                Util.splitStringOnLines("Додати/змінити питання: " + TF_q.getText() + "?", 35))) {
            String res = QuestionQuery.addQuestion(question);
            if (res.contains("Помилка")) {
                GUIUtil.showErrorAlert(res);
            } else {
                if (!suppressAlerts) {
                    GUIUtil.showInfoAlert("Питання успішно додано/змінено!", res);
                }
                SceneStarter.exit(actionEvent);
            }
        }
    }

    @FXML
    private void exit(ActionEvent actionEvent) throws SQLException {
        DBSession.logOut();
        SceneStarter.exit(actionEvent);
    }

    public void init(int questionID, boolean suppressAlerts) throws SQLException {
        this.questionID = questionID;
        this.suppressAlerts = suppressAlerts;
        if (questionID == -1) {
            authorID = AuthorQuery.getUserAsAuthor(DBSession.getId());
        } else {
            chB_subject.setValue(SubjectQuery.getSubjectByQuestion(questionID));
            chB_theme.setValue(ThemeQuery.getThemeByQuestion(questionID));
            TF_q.setText(QuestionQuery.getQuestionText(questionID));
            authorID = AuthorQuery.getAuthorIDByQuestion(questionID);
            chB_grade.setValue(GradeQuery.getGradeByQuestion(questionID));
            answers = AnswerQuery.getAnswers(questionID);
            rightAnswer = AnswerQuery.getRightAnswer(questionID);
//            drawing answers
            fillVBox();
            chB_answers.setItems(FXCollections.observableArrayList(answers));
        }
        label_author.setText(AuthorQuery.getAuthor(authorID));
    }

    private void fillVBox() {
        vBox_answers.getChildren().clear();
        for (String answer : answers) {
            RadioButton rb = new RadioButton(answer);
            if (answer.equals(rightAnswer)) {
                rb.setStyle(GUIUtil.RIGHT_ANSWER_STYLE);
            }
            vBox_answers.getChildren().add(rb);
        }
    }
}
