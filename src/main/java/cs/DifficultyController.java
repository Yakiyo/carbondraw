package cs;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Controller for the Difficulty selection page (difficulty.fxml).
 */
public class DifficultyController {

    @FXML
    private void handleEasy(ActionEvent event) {
        startGame("Easy", 1000);
    }

    @FXML
    private void handleNormal(ActionEvent event) {
        startGame("Normal", 2500);
    }

    @FXML
    private void handleHard(ActionEvent event) {
        startGame("Hard", 5000);
    }

    private void startGame(String difficulty, int targetPoints) {
        GameSession.getInstance().startNewGame(difficulty, targetPoints);
        try {
            App.setRoot("game");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
