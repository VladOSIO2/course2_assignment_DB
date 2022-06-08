package gui.quizStats;

import gui.SceneStarter;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.SQLException;

public class QuizStatsController {

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
}
