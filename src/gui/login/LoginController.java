package gui.login;

import gui.SceneStarter;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import gui.GUIUtil;
import utility.SHA256Encryptor;

import javafx.event.ActionEvent;
import java.sql.SQLException;

public class LoginController {
    @FXML private TextField TF_login;
    @FXML private  PasswordField PF_password;
    @FXML private TextField TF_password;
    @FXML private CheckBox check_showPassword;

    @FXML
    private void initialize() {
        PF_password.textProperty().bindBidirectional(TF_password.textProperty());
    }

    @FXML
    private void login(ActionEvent actionEvent) throws SQLException {
        String login = TF_login.getText();
        String password = SHA256Encryptor.encrypt(PF_password.getText());
        if (DBSession.init(login, password)) {
            SceneStarter.startSceneMenu(actionEvent);
        } else {
            GUIUtil.showErrorAlert("Невірне ім'я користувача або пароль!");
        }
    }

    @FXML
    private void getRegistrationScene(ActionEvent actionEvent) {
        SceneStarter.startSceneRegistration(actionEvent);
    }

    @FXML
    private void showPassword() {
        boolean show = check_showPassword.isSelected();
        TF_password.setVisible(show);
        PF_password.setVisible(!show);
    }
}
