package cs;

/**
 * Singleton state holder for the active game session.
 */
public class GameSession {
    private static GameSession instance;
    private String difficulty;
    private int targetPoints;
    private int currentScore;

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
    }

    public void endSession() {
        this.difficulty = null;
        this.targetPoints = 0;
        this.currentScore = 0;
    }

    public String getDifficulty() { return difficulty; }
    public int getTargetPoints() { return targetPoints; }
    public int getCurrentScore() { return currentScore; }
    public void setScore(int score) { this.currentScore = score; }
}
