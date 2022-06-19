package gui.generalStats;

import db.entities.queries.AuthorQuery;
import db.entities.queries.QuizQuery;
import db.entities.queries.ReportQuery;
import gui.GUIUtil;
import gui.SceneStarter;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import pdfbuilder.PDFBuilder;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class GeneralStatsController {

    @FXML
    private void showMostValuableAuthor() throws SQLException {
        Integer count = AuthorQuery.getMaxQuestionAuthorCount();
        List<String> names = AuthorQuery.getMaxQuestionAuthorNames();
        String info = "Максимальна кількість питань в автора: " +
                count + "\n" +
                "Автор(-и) з такою кількістю питань\n" +
                String.join("\n", names);
        GUIUtil.showInfoAlert("Автор(-и) з найбільшою кількістю питань", info);
    }

    @FXML
    private void showAndUpdateTest() throws SQLException {
        Map<Integer, String> mapQuiz = QuizQuery.updateAndGetMostPopularQuizzes();
        StringBuilder sb = new StringBuilder("Оновлено інформацію про найбільш популярні тестування\n");
        if (mapQuiz.size() == 1) {
            sb.append("Найбільш популярне тестування на даний момент:\n");
        } else {
            sb.append("Найбільш популярні тестування на даний момент:\n");
        }
        mapQuiz.forEach((k, v) -> sb.append(k).append(" : ").append(v));
        GUIUtil.showInfoAlert("Найбільш популярні тестування", sb.toString());
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
     * average level of success for each test
     */
    @FXML
    private void printReport2() throws SQLException {
        String result = PDFBuilder.build(
                "Звіт2",
                "Звіт: Середня успішність для кожного тестування",
                List.of("Номер тестування", "Назва теми", "Успішність, %"),
                ReportQuery.averageSuccess(),
                new int[]{13, 50, 17}
        );
        GUIUtil.showInfoAlert("Створення файлу:", result);
    }
}
