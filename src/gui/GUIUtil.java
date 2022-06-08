package gui;

import javafx.scene.control.Alert;

public class GUIUtil {
    public static void showErrorAlert(String contextText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Помилка");
        alert.setHeaderText("Помилка");
        alert.setContentText(contextText);
        alert.show();
    }

    public static void showInfoAlert(String headerText , String contextText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Інформація");
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);
        alert.show();
    }
}
