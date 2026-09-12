package cs;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Controller for the Play / Difficulty selection page (difficulty.fxml).
 */
public class DifficultyController {

    @FXML private Label difficultyLabel;
    @FXML private Label multiplierLabel;
    @FXML private Label runPreviewLabel;
    @FXML private Button newRunTab;
    @FXML private Button continueTab;
    @FXML private VBox newRunContent;
    @FXML private VBox continueContent;
    @FXML private Label continueRunTitle;
    @FXML private Label continueRunInfo;
    @FXML private Label continueRunTarget;
    @FXML private Button continueRunButton;

    private static final String[] DIFFICULTY_NAMES = { "EASY", "MEDIUM", "HARD" };
    private static final double[] DIFFICULTY_MULTIPLIERS = { 1.5, 2.0, 3.0 };
    private static final String[] DIFFICULTY_COLORS = {
        "#2b82f0",   // Blue for Easy
        "#9d42de",   // Purple for Medium
        "#e83847"    // Red for Hard
    };
    private int currentIndex = 0;

    @FXML
    public void initialize() {
        updateDifficultyDisplay();
        
        // Check if there's a saved run and populate Continue tab
        if (PlayerDatabase.hasActiveRun()) {
            PlayerData.RunData run = PlayerDatabase.loadRun();
            if (continueRunTitle != null) {
                continueRunTitle.setText(run.getDifficulty() + " RUN");
            }
            if (continueRunInfo != null) {
                int jokerCount = run.getOwnedJokers() != null ? run.getOwnedJokers().size() : 0;
                continueRunInfo.setText(String.format("Ante %d/%d  •  %d Joker(s) owned",
                    run.getCurrentAnte(), run.getMaxAntes(), jokerCount));
            }
            if (continueRunTarget != null) {
                continueRunTarget.setText(String.format("Current Target: %,d Pts", run.getTargetPoints()));
            }
            if (continueRunButton != null) {
                continueRunButton.setVisible(true);
            }
            // Auto-switch to Continue tab if a run exists
            handleContinueTab(null);
        }
    }

    @FXML
    private void handleNewRunTab(ActionEvent event) {
        if (newRunContent != null) newRunContent.setVisible(true);
        if (continueContent != null) continueContent.setVisible(false);
        if (newRunTab != null) newRunTab.setStyle("-fx-pref-width: 250px; -fx-pref-height: 50px; -fx-background-color: linear-gradient(to bottom, #2b82f0, #175ec2); -fx-font-size: 18px;");
        if (continueTab != null) continueTab.setStyle("-fx-pref-width: 250px; -fx-pref-height: 50px; -fx-background-color: rgba(255,255,255,0.1); -fx-font-size: 18px;");
    }

    @FXML
    private void handleContinueTab(ActionEvent event) {
        if (newRunContent != null) newRunContent.setVisible(false);
        if (continueContent != null) continueContent.setVisible(true);
        if (continueTab != null) continueTab.setStyle("-fx-pref-width: 250px; -fx-pref-height: 50px; -fx-background-color: linear-gradient(to bottom, #2b82f0, #175ec2); -fx-font-size: 18px;");
        if (newRunTab != null) newRunTab.setStyle("-fx-pref-width: 250px; -fx-pref-height: 50px; -fx-background-color: rgba(255,255,255,0.1); -fx-font-size: 18px;");
    }

    @FXML
    private void handlePrevDifficulty(ActionEvent event) {
        currentIndex = (currentIndex - 1 + DIFFICULTY_NAMES.length) % DIFFICULTY_NAMES.length;
        updateDifficultyDisplay();
    }

    @FXML
    private void handleNextDifficulty(ActionEvent event) {
        currentIndex = (currentIndex + 1) % DIFFICULTY_NAMES.length;
        updateDifficultyDisplay();
    }

    private void updateDifficultyDisplay() {
        String name = DIFFICULTY_NAMES[currentIndex];
        double mult = DIFFICULTY_MULTIPLIERS[currentIndex];
        String color = DIFFICULTY_COLORS[currentIndex];

        if (difficultyLabel != null) {
            difficultyLabel.setText(name);
            difficultyLabel.setStyle("-fx-font-size: 48px; -fx-text-fill: " + color + ";");
        }
        if (multiplierLabel != null) {
            String multStr = (mult == (int) mult) ? String.valueOf((int) mult) : String.valueOf(mult);
            multiplierLabel.setText("Score scales at " + multStr + "x per ante");
        }
        if (runPreviewLabel != null) {
            // Show the final ante target: 1000 * mult^9
            int finalTarget = (int) Math.round(1000 * Math.pow(mult, 9));
            runPreviewLabel.setText(String.format("Ante 10 Target: %,d Pts", finalTarget));
        }
    }

    @FXML
    private void handleStartRun(ActionEvent event) {
        String difficulty = DIFFICULTY_NAMES[currentIndex];
        double multiplier = DIFFICULTY_MULTIPLIERS[currentIndex];
        GameSession.getInstance().startNewGame(difficulty, multiplier);
        try {
            App.setRoot("game");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleContinueRun(ActionEvent event) {
        GameSession session = GameSession.getInstance();
        boolean loaded = session.loadFromDatabase();
        if (loaded) {
            try {
                App.setRoot("game");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
