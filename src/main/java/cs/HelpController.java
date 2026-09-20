package cs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class HelpController {

    @FXML
    private VBox mechanicsView;

    @FXML
    private VBox addonsView;

    @FXML
    private Button mechanicsBtn;

    @FXML
    private Button addonsBtn;

    @FXML
    public void initialize() {
        showMechanics(null);
    }

    @FXML
    private void showMechanics(ActionEvent event) {
        mechanicsView.setVisible(true);
        addonsView.setVisible(false);
        mechanicsBtn.setStyle("-fx-pref-width: 250px; -fx-pref-height: 45px; -fx-font-size: 16px; -fx-background-color: #fca311; -fx-text-fill: white;");
        addonsBtn.setStyle("-fx-pref-width: 250px; -fx-pref-height: 45px; -fx-font-size: 16px; -fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: #fca311; -fx-border-width: 2px; -fx-border-radius: 5px;");
    }

    @FXML
    private void showAddons(ActionEvent event) {
        mechanicsView.setVisible(false);
        addonsView.setVisible(true);
        addonsBtn.setStyle("-fx-pref-width: 250px; -fx-pref-height: 45px; -fx-font-size: 16px; -fx-background-color: #fca311; -fx-text-fill: white;");
        mechanicsBtn.setStyle("-fx-pref-width: 250px; -fx-pref-height: 45px; -fx-font-size: 16px; -fx-background-color: transparent; -fx-text-fill: white; -fx-border-color: #fca311; -fx-border-width: 2px; -fx-border-radius: 5px;");
    }

    @FXML
    private void handleReturnHome(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
