package gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.concurrent.Callable;

public class GUIUtil {
    public static void showErrorAlert(String contextText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText("Помилка");
        alert.setContentText(contextText);
        alert.showAndWait();
    }

    public static void showInfoAlert(String headerText , String contextText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація");
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);
        alert.showAndWait();
    }

    public static boolean showConfirmationAlert(String headerText , String contextText) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Інформація");
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);
        alert.showAndWait();
        return alert.getResult() == ButtonType.OK;
    }
}
