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
}
