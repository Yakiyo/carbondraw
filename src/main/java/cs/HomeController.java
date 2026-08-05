package cs;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

/**
 * Controller for the Balatro-themed Home page view (home.fxml).
 */
public class HomeController {

    @FXML
    private Label statusLabel;

    @FXML
    private Button playButton;

    @FXML
    private Button settingsButton;

    @FXML
    private Button exitButton;

    @FXML
    public void initialize() {
        if (statusLabel != null) {
            statusLabel.setText("♠ Ready to deal... Select an option ♥");
        }
    }

    @FXML
    private void handlePlay(ActionEvent event) {
        if (statusLabel != null) {
            statusLabel.setText("♥ Starting new run... Ante 1: Small Blind ($300) ♦");
        }
    }

    @FXML
    private void handleSettings(ActionEvent event) {
        if (statusLabel != null) {
            statusLabel.setText("♣ Settings: Game Speed (4x) • CRT Scanlines • Audio ♠");
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        // Gracefully close the JavaFX application
        Platform.exit();
        System.exit(0);
    }
}
