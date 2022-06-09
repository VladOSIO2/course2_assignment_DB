package gui.menu;

import db.entities.user.UserType;
import gui.SceneStarter;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.sql.SQLException;

public class MenuController {

    @FXML private Button button_logInfo;
    @FXML private Button button_generalStats;
    @FXML private Button button_quizStats;
    @FXML private Button button_questionManager;

    @FXML
    private void initialize() {
        if (DBSession.getType() ==  null) {
            throw new NullPointerException("User in menu not logged in!");
        }
        if (!DBSession.getType().equals(UserType.ADMIN)) {
            //not admin
            button_logInfo.setVisible(false);
            button_generalStats.setVisible(false);
            button_quizStats.setVisible(false);
            if (!DBSession.getType().equals(UserType.AUTHOR)) {
                //not author & not admin
                button_questionManager.setVisible(false);
            }
        }
    }

    @FXML
    private void getRegistrationScene(ActionEvent actionEvent) {
        SceneStarter.startSceneRegistration(actionEvent);
    }

    @FXML
    private void getQuestionManagerScene(ActionEvent actionEvent) {
        SceneStarter.startSceneQuestionManager(actionEvent);
    }

    @FXML
    private void getQuizStats(ActionEvent actionEvent) {
        SceneStarter.startSceneQuizStats(actionEvent);
    }

    @FXML
    private void getLogInfo(ActionEvent actionEvent) {
        SceneStarter.startSceneLogs(actionEvent);
    }

    @FXML
    private void getGeneralStats(ActionEvent actionEvent) {
        SceneStarter.startSceneGeneralStats(actionEvent);
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
