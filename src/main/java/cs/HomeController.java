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
    private Label currencyLabel;

    @FXML
    private Button playButton;

    @FXML
    private Button shopButton;

    @FXML
    private Button deckButton;

    @FXML
    private Button exitButton;

    @FXML
    public void initialize() {
        if (statusLabel != null) {
            statusLabel.setText("♠ Ready to deal... Select an option ♥");
        }
        if (currencyLabel != null) {
            currencyLabel.setText("Coins: " + GameSession.getInstance().getCoins());
        }
    }

    @FXML
    private void handlePlay(ActionEvent event) {
        try {
            App.setRoot("difficulty");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShop(ActionEvent event) {
        try {
            App.setRoot("shop");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDeck(ActionEvent event) {
        try {
            App.setRoot("deck");
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleExit(ActionEvent event) {
        // Gracefully close the JavaFX application
        Platform.exit();
        System.exit(0);
    }

    @FXML
    private void handleToggleMusic(ActionEvent event) {
        App.toggleBackgroundMusic();
    }
}
