package gui.quizManager;

import db.entities.queries.QuestionQuery;
import db.entities.queries.QuizQuery;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.chooseQuiz.ChooseQuizController;
import gui.chooseQuiz.SelectedQuiz;
import gui.login.DBSession;
import gui.questionManager.QuestionManagerController;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class QuizManagerController {
    @FXML private DatePicker date_start;
    @FXML private TextField TF_hoursStart;
    @FXML private TextField TF_minutesStart;

    @FXML private DatePicker date_end;
    @FXML private TextField TF_hoursEnd;
    @FXML private TextField TF_minutesEnd;

    @FXML private TextField TF_timeToDo;
    @FXML private Label label_quizName;
    @FXML private ListView<String> LV_questions;
    @FXML private TextField TF_quizName;
    @FXML private Label label_alertNoQuestionChange;
    @FXML private Button button_saveQuiz;
    @FXML private Button button_deleteQuiz;
    @FXML private Button button_addQuestion;
    @FXML private Button button_removeQuestion;

//    controller variables
    private Pair<Integer, String> selectedQuiz;
    private Map<Integer, String> questionMap;
    private boolean isCreating = false;
    private QuestionManagerController qmc;
    private Stage stage_qmc;

    @FXML
    private void selectQuiz() throws SQLException {
        if (isCreating) {
            if (!GUIUtil.showConfirmationAlert(
                    "Обрати інше тестування?",
                    "Обрати інше тестування? Не збережені дані буде зтерто!")) {
                return;
            }
        }
        //TODO move to SceneStarter methods
        ChooseQuizController cqc = new ChooseQuizController();
        cqc.showStage();
        selectedQuiz = SelectedQuiz.get();
        boolean isSelected = selectedQuiz.getKey() != -1;
        if (!isSelected) {
            return;
        }
        isCreating = true;
        label_quizName.setText(selectedQuiz.getValue());
        toggleInputs(false);
        fillDateTime();
        questionMap = QuestionQuery.getQuestions(selectedQuiz.getKey());
        LV_questions.setItems(FXCollections.observableArrayList(
                questionMap.values()));
        button_deleteQuiz.setDisable(false);

        boolean hasResponder = QuizQuery.hasResponders(selectedQuiz.getKey());
        label_alertNoQuestionChange.setVisible(hasResponder);
        button_addQuestion.setDisable(hasResponder);
        button_removeQuestion.setDisable(hasResponder);

        TF_quizName.setText(selectedQuiz.getValue());
    }

    @FXML
    private void newQuiz() {
        if (isCreating) {
            if (!GUIUtil.showConfirmationAlert(
                    "Обрати інше питання?",
                    "Обрати інше питання? Не збережені дані буде зтерто!")) {
                return;
            }
        }
        selectedQuiz = new Pair<>(-1, "");
        isCreating = true;
        label_quizName.setText("<Нове тестування>");
        toggleInputs(false);
        clearDateTime();
        questionMap = new LinkedHashMap<>();
        LV_questions.setItems(FXCollections.observableArrayList());

        label_alertNoQuestionChange.setVisible(false);
        button_addQuestion.setDisable(false);
        button_removeQuestion.setDisable(false);
        button_deleteQuiz.setDisable(true);

        TF_quizName.setText("");
    }

    @FXML
    private void addQuestion() throws IOException {
        if (stage_qmc == null || !stage_qmc.isShowing()) {
            getController();
        }
        Pair<Integer, String> q = qmc.getSelectedQuestion();
        questionMap.put(q.getKey(), q.getValue());
        fillQuestions();
    }

    private void getController() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
                "/gui/questionManager/questionManagerPane.fxml"));
        Parent root = fxmlLoader.load();
        qmc = fxmlLoader.getController();
        stage_qmc = new Stage();
        stage_qmc.setTitle("Менеджер питань");
        stage_qmc.setScene(new Scene(root));
        stage_qmc.showAndWait();
    }

    @FXML
    private void removeQuestion() {
        int i = LV_questions.getSelectionModel().getSelectedIndex();
        if (i == -1) {
            GUIUtil.showErrorAlert("Для видалення питання виберіть\n" +
                    "питання яке треба видалити!");
            return;
        }
        int questionID = new ArrayList<>(questionMap.keySet()).get(i);
        questionMap.remove(questionID);
        fillQuestions();
    }

    @FXML
    private void saveQuiz() throws SQLException {
        ArrayList<Integer> questions = null;
        if (GUIUtil.showConfirmationAlert(
                "Додати тестування?",
                "Додати тестування " + TF_quizName.getText() + "?")) {
            if (!QuizQuery.hasResponders(selectedQuiz.getKey())) {
                questions = new ArrayList<>(questionMap.keySet());
            }
            QuizQuery.addQuiz(selectedQuiz.getKey(), getDateStart(), getDateEnd(),
                    Integer.parseInt(TF_timeToDo.getText()), TF_quizName.getText(), questions);
        }
    }

    @FXML
    private void deleteQuiz() throws SQLException {
        if (GUIUtil.showConfirmationAlert(
                "Видалити тестування?",
                "Видалити тестування? Повернути цю дію буде неможливо!")) {
            if (QuizQuery.hasResponders(selectedQuiz.getKey())) {
                GUIUtil.showErrorAlert("Не можна видалити тест, що має проходження");
            } else {
                QuizQuery.deleteQuiz(selectedQuiz.getKey());
                resetScene();
            }
        }
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

    private void toggleInputs(boolean isDisabled) {
        TF_quizName.setDisable(isDisabled);
        date_start.setDisable(isDisabled);
        TF_hoursStart.setDisable(isDisabled);
        TF_hoursEnd.setDisable(isDisabled);
        date_end.setDisable(isDisabled);
        TF_minutesStart.setDisable(isDisabled);
        TF_minutesEnd.setDisable(isDisabled);
        TF_timeToDo.setDisable(isDisabled);
        LV_questions.setDisable(isDisabled);
        button_saveQuiz.setDisable(isDisabled);
    }

    private void fillDateTime() throws SQLException {
        LocalDateTime dtStart = QuizQuery.getDTStart(selectedQuiz.getKey());
        LocalDateTime dtEnd = QuizQuery.getDTEnd(selectedQuiz.getKey());
        date_start.setValue(dtStart.toLocalDate());
        date_end.setValue(dtEnd.toLocalDate());
        TF_hoursStart.setText(Integer.toString(dtStart.getHour()));
        TF_minutesStart.setText(Integer.toString(dtStart.getMinute()));
        TF_hoursEnd.setText(Integer.toString(dtEnd.getHour()));
        TF_minutesEnd.setText(Integer.toString(dtEnd.getMinute()));
        TF_timeToDo.setText(Integer.toString(QuizQuery.getTimeToDo(selectedQuiz.getKey())));
    }

    private void clearDateTime() {
        date_start.setValue(null);
        date_end.setValue(null);
        TF_hoursStart.setText("");
        TF_minutesStart.setText("");
        TF_hoursEnd.setText("");
        TF_minutesEnd.setText("");
        TF_timeToDo.setText("");
    }

    private void fillQuestions() {
        LV_questions.setItems(FXCollections.observableArrayList(questionMap.values()));
    }

    private void resetScene() {
        toggleInputs(true);
        clearDateTime();
        selectedQuiz = new Pair<>(-1, "");
        questionMap = new LinkedHashMap<>();
        fillQuestions();
        label_quizName.setText("");
        button_deleteQuiz.setDisable(true);
        button_addQuestion.setDisable(true);
        button_removeQuestion.setDisable(true);
        TF_quizName.setText("");
    }

    private String getDateStart() {
        int hour = Integer.parseInt(TF_hoursStart.getText());
        int minute = Integer.parseInt(TF_minutesStart.getText());
        LocalDateTime ldt = LocalDateTime.of(
                date_start.getValue(),
                LocalTime.of(hour, minute));
        return ldt.toString();
    }

    private String getDateEnd() {
        int hour = Integer.parseInt(TF_hoursEnd.getText());
        int minute = Integer.parseInt(TF_minutesEnd.getText());
        LocalDateTime ldt = LocalDateTime.of(
                date_end.getValue(),
                LocalTime.of(hour, minute));
        return ldt.toString();
    }
}
