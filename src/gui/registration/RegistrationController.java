package gui.registration;

import db.entities.user.UserType;
import db.entities.user.UserQuery;
import gui.SceneStarter;
import gui.login.DBSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    @FXML private Button button_logOut;
    @FXML private Label label_passwordsNotMatch;
    @FXML private Label label_loginOccupied;
    @FXML private ChoiceBox<String> chB_userType;
    @FXML private CheckBox check_showPassword;

    @FXML
    private void initialize() {
        //show/hide password fields binding
        TF_password.textProperty()
                .bindBidirectional(PF_password.textProperty());
        TF_passwordRepeat.textProperty()
                .bindBidirectional(PF_passwordRepeat.textProperty());
        //non logged in registration
        if(DBSession.getType() == null) {
            button_logOut.setVisible(false);
        }

        //user type choiceBox configuration
        ObservableList<String> userTypes = FXCollections.observableArrayList();
        userTypes.add(UserType.RESPONDER.name());
        chB_userType.setValue(UserType.RESPONDER.name());
        if (DBSession.getType() != null
                && DBSession.getType().equals(UserType.ADMIN)) {
            userTypes.add(UserType.AUTHOR.name());
        } else {
            chB_userType.setDisable(true);
        }
        chB_userType.setItems(userTypes);
    }

    @FXML
    private void registration(ActionEvent actionEvent) throws SQLException {
        boolean error = false;
        label_loginOccupied.setVisible(false);
        label_passwordsNotMatch.setVisible(false);

        //TODO: input data confirmation
        //login check
        String login = TF_login.getText();
        if (UserQuery.hasLogin(login)) {
            label_loginOccupied.setVisible(true);
            error = true;
        }

        //password confirmation
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

        //TODO: input data confirmation
        String name = TF_name.getText();
        String surname = TF_surname.getText();
        String patronymic = TF_patronymic.getText();
        String passwordEncrypted = SHA256Encryptor.encrypt(password);
        UserQuery.insertUser(name, surname, patronymic,
                login, passwordEncrypted, DBSession.getType());
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
        if (DBSession.getType() == null) {
            SceneStarter.startSceneLogin(actionEvent);
        } else {
            SceneStarter.startSceneMenu(actionEvent);
        }
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
