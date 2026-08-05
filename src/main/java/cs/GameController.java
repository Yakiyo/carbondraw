package cs;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Controller for the main Game page view (game.fxml).
 */
public class GameController {

    @FXML
    private Label gameInfoLabel;

    @FXML
    public void initialize() {
        GameSession session = GameSession.getInstance();
        if (session.getDifficulty() != null) {
            gameInfoLabel.setText(String.format("Difficulty: %s  |  Target: %,d Pts", 
                session.getDifficulty(), session.getTargetPoints()));
        }
    }

    @FXML
    private void handleReturnHome(ActionEvent event) {
        GameSession.getInstance().endSession();
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
