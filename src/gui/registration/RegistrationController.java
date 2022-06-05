package gui.registration;

import db.DBUserType;
import db.entities.user.DBUserQuery;
import gui.login.DBSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import utility.SHA256Encryptor;

import java.sql.SQLException;

public class RegistrationController {
    @FXML private TextField TF_login;
    @FXML private TextField TF_name;
    @FXML private TextField TF_patronymic;
    @FXML private TextField TF_surname;
    @FXML private TextField TF_password;
    @FXML private TextField TF_passwordRepeat;
    @FXML private PasswordField PF_password;
    @FXML private PasswordField PF_passwordRepeat;
    @FXML private Button Button_logOut;
    @FXML private Label label_passwordsNotMatch;
    @FXML private Label label_loginOccupied;
    @FXML private ChoiceBox<DBUserType> ChB_userType;
    @FXML private CheckBox check_showPassword;

    @FXML
    private void initialize() {
        TF_password.textProperty()
                .bindBidirectional(PF_password.textProperty());
        TF_passwordRepeat.textProperty()
                .bindBidirectional(PF_passwordRepeat.textProperty());

    }

    @FXML
    private void registration(ActionEvent actionEvent) throws SQLException {
        boolean error = false;
        label_loginOccupied.setVisible(false);
        label_passwordsNotMatch.setVisible(false);

        String login = TF_login.getText();
        if (DBUserQuery.hasLogin(login)) {
            label_loginOccupied.setVisible(true);
            error = true;
        }

        String password = PF_passwordRepeat.getText();
        if (!PF_password.getText()
                .equals(PF_passwordRepeat.getText())
        ) {
            label_passwordsNotMatch.setVisible(true);
            error = true;
        }

        if (error) {
            return;
        }

        String name = TF_name.getText();
        String surname = TF_surname.getText();
        String patronymic = TF_patronymic.getText();
        String passwordEncrypted = SHA256Encryptor.encrypt(password);

    }

    @FXML
    private void showPassword() {
        boolean show = check_showPassword.isSelected();
        TF_password.setVisible(show);
        TF_passwordRepeat.setVisible(show);
        PF_password.setVisible(!show);
        PF_passwordRepeat.setVisible(!show);
    }

    @FXML
    private void returnBack(ActionEvent actionEvent) {
    }

    @FXML
    private void logOut(ActionEvent actionEvent) {
    }

    @FXML
    private void exit(ActionEvent actionEvent) {
    }
}
