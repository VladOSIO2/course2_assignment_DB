package gui.logs;

import db.entities.user.LogsQuery;
import gui.SceneStarter;
import gui.login.DBSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LogsInfoController {
    @FXML private ListView<String> LV_logs;
    @FXML private TextField TF_search;

    @FXML
    private void searchByLogin() throws SQLException {
        String loginPart = TF_search.getText();
        ObservableList<String> list = FXCollections.observableArrayList();
        list.addAll(LogsQuery.getLogs(loginPart));
        LV_logs.setItems(list);
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
