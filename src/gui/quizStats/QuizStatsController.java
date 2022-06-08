package gui.quizStats;

import db.entities.question.QuizQuery;
import db.entities.question.ReportQuery;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.chooseQuiz.ChooseQuizController;
import gui.chooseQuiz.SelectedQuiz;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import pdfbuilder.PDFBuilder;

import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.List;

public class QuizStatsController {
    @FXML Label label_quizNotChosen;
    @FXML private Label label_responderAmount;
    @FXML private Label label_id;
    @FXML private Label label_name;
    @FXML private Label label_mark;
    @FXML private Button button_getResponderAmount;
    @FXML private CheckBox check_onlyForMarks;
    @FXML private TextField TF_from;
    @FXML private TextField TF_to;
    @FXML private Button button_printReport3;

    private Pair<Integer, String> selectedQuiz = new Pair<>(-1, "");
    private int maxMark = 0;

    @FXML
    private void chooseQuiz(ActionEvent actionEvent) throws SQLException {
        //TODO move to SceneStarter methods
        ChooseQuizController cqc = new ChooseQuizController();
        cqc.showStage();
        selectedQuiz = SelectedQuiz.get();
        if (selectedQuiz.getKey() == -1) {
            clearPane();
        } else {
            configurePane();
        }
    }

    private void clearPane() {
        label_quizNotChosen.setVisible(true);
        button_getResponderAmount.setDisable(true);
        button_printReport3.setDisable(true);
        check_onlyForMarks.setDisable(true);
        check_onlyForMarks.setSelected(false);
        TF_to.setDisable(true);
        TF_to.setText("");
        TF_from.setDisable(true);
        TF_from.setText("");
        label_id.setText("<Номер>");
        label_mark.setText("<Бали>");
        label_name.setText("<Назва>");
        label_responderAmount.setText("<Кількість>");
    }

    private void configurePane() throws SQLException {
        label_quizNotChosen.setVisible(false);
        button_getResponderAmount.setDisable(false);
        button_printReport3.setDisable(false);
        check_onlyForMarks.setDisable(false);
        TF_to.setText("");
        TF_from.setText("");
        label_id.setText(Integer.toString(selectedQuiz.getKey()));
        maxMark = QuizQuery.getMaxQuizMark(selectedQuiz.getKey());
        label_mark.setText(Integer.toString(maxMark));
        label_name.setText(selectedQuiz.getValue());
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

    @FXML
    private void getResponderAmount() throws SQLException {
        int key = selectedQuiz.getKey();
        int amount = 0;
        if (!check_onlyForMarks.isSelected()) {
            amount = QuizQuery.getResponderCount(key);
        } else {
            try {
                int from, to;
                from = Integer.parseInt(TF_from.getText());
                to = Integer.parseInt(TF_to.getText());
                if (from < 0 || to < 0) {
                    GUIUtil.showErrorAlert("Діапазон від та до має бути задано числами >= 0!");
                    return;
                } else if (from > to) {
                    GUIUtil.showErrorAlert("Значення 'від' має бути не більшим за значення 'до'");
                    return;
                } else if (from > maxMark || to > maxMark) {
                    GUIUtil.showErrorAlert("Значення діапазону мають не перевищувати\nмаксимальну оцінку!");
                    return;
                } else {
                    amount = QuizQuery.getResponderCount(selectedQuiz.getKey(), from, to);
                }
            } catch (NumberFormatException e) {
                GUIUtil.showErrorAlert("Діапазон від та до має бути задано числами >= 0!");
                return;
            }

        }

        label_responderAmount.setText(Integer.toString(amount));
    }

    /**
     * creates a pdf report:
     * full information about certain test completion
     */
    @FXML
    private void printReport3() throws SQLException {
        String result = PDFBuilder.build(
                "Звіт3",
                "Звіт: Загальна результативність тесту №%d \"%s\""
                        .formatted(selectedQuiz.getKey(), selectedQuiz.getValue()),
                List.of("ПІБ", "Оцінка", "Використання часу", "Здача після закінчення"),
                ReportQuery.getFullResults(selectedQuiz.getKey()),
                new int[]{37, 8, 17, 16}
        );
        GUIUtil.showInfoAlert("Створення файлу:", result);
    }

    @FXML
    private void toggleOnlyForMarks() {
        boolean isSelected = check_onlyForMarks.isSelected();
        if (!isSelected) {
            TF_from.setText("");
            TF_to.setText("");
        }
        TF_from.setDisable(!isSelected);
        TF_to.setDisable(!isSelected);
    }
}
