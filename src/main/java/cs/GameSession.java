package cs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton state holder for the active game session.
 */
public class GameSession {
    private static GameSession instance;
    private String difficulty;
    private double difficultyMultiplier;
    private int currentAnte;
    private int maxAntes;
    private int baseTargetScore;
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

    public void startNewGame(String difficulty, double multiplier) {
        this.difficulty = difficulty;
        this.difficultyMultiplier = multiplier;
        this.currentAnte = 1;
        this.maxAntes = 10;
        this.baseTargetScore = 1000;
        this.targetPoints = baseTargetScore;
        this.currentScore = 0;
        
        // Jokers will be bought from the shop
        this.activeJokers.clear();
        
        // Persist to database
        saveRunToDatabase();
    }

    /**
     * Restore a run from saved database data.
     */
    public boolean loadFromDatabase() {
        PlayerData.RunData runData = PlayerDatabase.loadRun();
        if (runData == null) return false;

        this.difficulty = runData.getDifficulty();
        this.difficultyMultiplier = runData.getDifficultyMultiplier();
        this.currentAnte = runData.getCurrentAnte();
        this.maxAntes = runData.getMaxAntes();
        this.baseTargetScore = runData.getBaseTargetScore();
        this.targetPoints = runData.getTargetPoints();
        this.currentScore = 0;

        // Restore jokers
        this.activeJokers.clear();
        if (runData.getOwnedJokers() != null) {
            for (PlayerData.JokerData jd : runData.getOwnedJokers()) {
                this.activeJokers.add(new Joker(
                    jd.getName(),
                    jd.getDescription(),
                    jd.getImagePath(),
                    Joker.JokerEffect.valueOf(jd.getEffectType()),
                    jd.getEffectValue()
                ));
            }
        }
        return true;
    }

    /**
     * Save current run state to the database.
     */
    public void saveRunToDatabase() {
        List<PlayerData.JokerData> jokerDataList = activeJokers.stream()
            .map(j -> new PlayerData.JokerData(
                j.name(), j.description(), j.imagePath(),
                j.effectType().name(), j.effectValue()
            ))
            .collect(Collectors.toList());

        PlayerData.RunData runData = new PlayerData.RunData(
            difficulty, difficultyMultiplier, currentAnte,
            maxAntes, baseTargetScore, targetPoints, jokerDataList
        );
        PlayerDatabase.saveRun(runData);
    }

    /**
     * Advance to the next ante. Returns true if there are more antes, false if the run is complete.
     */
    public boolean advanceAnte() {
        if (currentAnte >= maxAntes) {
            return false; // Run complete!
        }
        currentAnte++;
        // Target score = base * multiplier^(ante-1)
        targetPoints = (int) Math.round(baseTargetScore * Math.pow(difficultyMultiplier, currentAnte - 1));
        currentScore = 0;
        
        // Persist updated state
        saveRunToDatabase();
        return true;
    }

    public void endSession() {
        this.difficulty = null;
        this.targetPoints = 0;
        this.currentScore = 0;
        this.currentAnte = 0;
        this.activeJokers.clear();
        
        // Clear saved run from database
        PlayerDatabase.clearRun();
    }

    public String getDifficulty() { return difficulty; }
    public double getDifficultyMultiplier() { return difficultyMultiplier; }
    public int getCurrentAnte() { return currentAnte; }
    public int getMaxAntes() { return maxAntes; }
    public int getTargetPoints() { return targetPoints; }
    public int getCurrentScore() { return currentScore; }
    public void setScore(int score) { this.currentScore = score; }
    public List<Joker> getActiveJokers() { return activeJokers; }
}
