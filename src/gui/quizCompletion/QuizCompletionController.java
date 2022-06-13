package gui.quizCompletion;

import db.entities.Question;
import db.entities.queries.*;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.chooseQuiz.ChooseQuizController;
import gui.chooseQuiz.SelectedQuiz;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

public class QuizCompletionController {
    @FXML private Label label_questionText;
    @FXML private VBox VBox_answers;
    @FXML private Button button_chooseQuiz;
    @FXML private Label label_quizName;
    @FXML private Label label_timeToDo;
    @FXML private Label label_maxMark;
    @FXML private Label label_timestampStart;
    @FXML private Label label_timestampEnd;
    @FXML private Button button_startQuiz;
    @FXML private Button button_checkAnswer;
    @FXML private Button button_nextQuestion;
    @FXML private Label label_questionGrade;
    @FXML private Label label_currMark;
    @FXML private Label label_questionN;

//    scene variables
    private Pair<Integer, String> selectedQuiz;
    private List<Integer> questionIDs;
    private int currMark = 0;
    private int currQuestion = 0;
    private int currPoints = 0;
    private LocalDateTime startDT;
    private LocalDateTime endDT;
    private String currRightAnswer = null;


    @FXML
    private void chooseQuiz() throws SQLException {
        ChooseQuizController cqc = new ChooseQuizController();
        cqc.showStage();
        selectedQuiz = SelectedQuiz.get();
        boolean isSelected = selectedQuiz.getKey() != -1;
        if (!isSelected) {
            return;
        }
        questionIDs = QuizQuery.getQuestionIDs(selectedQuiz.getKey());
        Collections.shuffle(questionIDs);
        label_quizName.setText(selectedQuiz.getValue());
        label_timeToDo.setText(QuizQuery.getTimeToDo(selectedQuiz.getKey()) + " хвилин");
        label_maxMark.setText(QuizQuery.getMaxQuizMark(selectedQuiz.getKey()) + " бали");
        button_startQuiz.setDisable(false);
        clearLastQuestion();
        button_nextQuestion.setText("Наступне питання");
    }

    @FXML
    private void startQuiz() throws SQLException {
        startDT = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        label_timestampStart.setText(startDT.toString());
        label_currMark.setText("0");
        button_checkAnswer.setDisable(false);
        button_chooseQuiz.setDisable(true);
        button_startQuiz.setDisable(true);
        loadQuestion();
    }

    @FXML
    private void checkAnswer() {
        button_checkAnswer.setDisable(true);
        button_nextQuestion.setDisable(false);
        for (Node node : VBox_answers.getChildren()) {
            if (node instanceof RadioButton rb) {
                rb.setDisable(true);
                if (rb.isSelected()) {
                    if (currRightAnswer.equals(rb.getText())) {
                        currMark += currPoints;
                        label_currMark.setText(Integer.toString(currMark));
                    } else {
                        rb.setStyle(GUIUtil.WRONG_ANSWER_STYLE);
                    }
                }
                if (currRightAnswer.equals(rb.getText())) {
                    rb.setStyle(GUIUtil.RIGHT_ANSWER_STYLE);
                }
            }
        }
    }

    @FXML
    private void nextQuestion() throws SQLException {
        if ("Завершити спробу".equals(button_nextQuestion.getText())) {
            endDT = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
            label_timestampEnd.setText(endDT.toString());
            saveQuizCompletion();
            button_checkAnswer.setDisable(true);
            return;
        }
        if (!loadQuestion()) {
            button_nextQuestion.setText("Завершити спробу");
            button_checkAnswer.setDisable(true);
            button_nextQuestion.setDisable(false);
        } else {
            button_checkAnswer.setDisable(false);
            button_nextQuestion.setDisable(true);
        }
    }

    private boolean loadQuestion() throws SQLException {
        if (currQuestion >= questionIDs.size()) {
            return false;
        }
        int currQID = questionIDs.get(currQuestion);
        List<String> answers = AnswerQuery.getAnswers(currQID);
        currRightAnswer = AnswerQuery.getRightAnswer(currQID);
        currPoints = GradeQuery.getGradePoints(currQID);
        label_questionGrade.setText(Integer.toString(currPoints));
        label_questionText.setText(QuestionQuery.getQuestionText(currQID));
        label_questionN.setText(++currQuestion + "/" + questionIDs.size());

        ToggleGroup tg = new ToggleGroup();
        double height = VBox_answers.getHeight() / answers.size();
        VBox_answers.getChildren().clear();
        for (String answer : answers) {
            RadioButton rb = new RadioButton();
            rb.setText(answer);
            rb.setToggleGroup(tg);
            rb.setPrefHeight(height);
            VBox_answers.getChildren().add(rb);
        }
        return true;
    }

    private void saveQuizCompletion() throws SQLException {
        int responderID = ResponderQuery.getUserAsResponder(
                DBSession.getId());
        String res = QuizQuery.insertQuizCompletion(
                selectedQuiz.getKey(), responderID, currMark,
                startDT.toString(), endDT.toString());
        if (res.contains("Помилка")) {
            GUIUtil.showErrorAlert(res);
        } else {
            GUIUtil.showInfoAlert("Проходження тесту збережено!", res);
            button_nextQuestion.setDisable(true);
            button_chooseQuiz.setDisable(false);
        }
    }

    private void clearLastQuestion() {
        VBox_answers.getChildren().clear();
        label_questionText.setText("<Питання>");
        label_questionN.setText("<№>");
        label_questionGrade.setText("<Бали>");
        label_timestampStart.setText("<Тест не розпочато>");
        label_timestampEnd.setText("<Тест не завершено>");
        label_currMark.setText("<Бали>");
        currMark = 0;
        currQuestion = 0;
    }

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
}
