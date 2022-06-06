package gui.menu;

import gui.SceneStarter;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.sql.SQLException;

public class MenuController {

    //TODO: control buttons

    public void getRegistrationScene(ActionEvent actionEvent) {
        SceneStarter.startSceneRegistration(actionEvent);
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
