package gui.generalStats;

import db.entities.question.AuthorQuery;
import db.entities.question.ReportQuery;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pdfbuilder.PDFBuilder;

import java.sql.SQLException;
import java.util.List;

public class GeneralStatsController {

    //TODO: control buttons

    @FXML
    private void showMostValuableAuthor(ActionEvent actionEvent) throws SQLException {
        Integer count = AuthorQuery.getMaxQuestionAuthorCount();
        List<String> names = AuthorQuery.getMaxQuestionAuthorNames();
        String info = "Максимальна кількість питань в автора: " +
                count + "\n" +
                "Автор(-и) з такою кількістю питань\n" +
                String.join("\n", names);
        GUIUtil.showInfoAlert("Автор(-и) з найбільшою кількістю питань", info);
    }

    @FXML
    private void showAndUpdateTest(ActionEvent actionEvent) {

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

    /**
     * creates a pdf report:
     * for each subject show theme with least amount of questions
     */
    @FXML
    private void printReport1() throws SQLException {
        String result = PDFBuilder.build(
                "Звіт1",
                "Звіт: тема з найменшою кількістю питань в кожному предметі",
                List.of("Назва предмету", "Назва теми", "Клькість питань"),
                ReportQuery.leastQuestionsThemeInEachSubject(),
                new int[]{25, 37, 8}
        );
        GUIUtil.showInfoAlert("Створення файлу:", result);
    }

    /**
     * creates a pdf report:
     * min and max mark for each test
     */
    @FXML
    private void printReport2() throws SQLException {
        String result = PDFBuilder.build(
                "Звіт1",
                "Звіт: тема з найменшою кількістю питань в кожному предметі",
                List.of("Назва предмету", "Назва теми", "Клькість питань"),
                ReportQuery.leastQuestionsThemeInEachSubject(),
                new int[]{30, 30, 30}
        );
        GUIUtil.showInfoAlert("Створення файлу:", result);
    }
}
