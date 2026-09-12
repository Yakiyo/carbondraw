package cs;

import java.util.ArrayList;
import java.util.List;

/**
 * Singleton state holder for the active game session.
 */
public class GameSession {
    private static GameSession instance;
    private String difficulty;
    private int targetPoints;
    private int currentScore;
    private List<Joker> activeJokers = new ArrayList<>();

    private GameSession() {}

    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    public void startNewGame(String difficulty, int targetPoints) {
        this.difficulty = difficulty;
        this.targetPoints = targetPoints;
        this.currentScore = 0;
        
        // Initialize default Jokers
        this.activeJokers.clear();
        this.activeJokers.add(new Joker("Basic Joker", "Adds +20 Chips", "/cs/images/joker/joker_1.jpg", Joker.JokerEffect.ADD_CHIPS, 20));
        this.activeJokers.add(new Joker("Multi Joker", "Adds +4 Mult", "/cs/images/joker/joker_2.jpg", Joker.JokerEffect.ADD_MULTI, 4));
        this.activeJokers.add(new Joker("Foil Joker", "Multiplies Mult by 2", "/cs/images/joker/joker_3.jpg", Joker.JokerEffect.MULT_MULTI, 2));
    }

    public void endSession() {
        this.difficulty = null;
        this.targetPoints = 0;
        this.currentScore = 0;
        this.activeJokers.clear();
    }

    public String getDifficulty() { return difficulty; }
    public int getTargetPoints() { return targetPoints; }
    public int getCurrentScore() { return currentScore; }
    public void setScore(int score) { this.currentScore = score; }
    public List<Joker> getActiveJokers() { return activeJokers; }
}
