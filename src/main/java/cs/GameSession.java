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
    private List<Joker> ownedJokers = new ArrayList<>();
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
        
        this.ownedJokers.clear();
        this.activeJokers.clear();
        
        // Clear any previous run from DB; we don't save the new run until first win
        PlayerDatabase.clearRun();
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

        // Restore owned jokers
        this.ownedJokers.clear();
        if (runData.getOwnedJokers() != null) {
            for (PlayerData.JokerData jd : runData.getOwnedJokers()) {
                this.ownedJokers.add(jokerFromData(jd));
            }
        }

        // Restore active jokers
        this.activeJokers.clear();
        if (runData.getActiveJokers() != null) {
            for (PlayerData.JokerData jd : runData.getActiveJokers()) {
                this.activeJokers.add(jokerFromData(jd));
            }
        }
        return true;
    }

    private Joker jokerFromData(PlayerData.JokerData jd) {
        return new Joker(
            jd.getName(), jd.getDescription(), jd.getImagePath(),
            Joker.JokerEffect.valueOf(jd.getEffectType()), jd.getEffectValue()
        );
    }

    private PlayerData.JokerData dataFromJoker(Joker j) {
        return new PlayerData.JokerData(
            j.name(), j.description(), j.imagePath(),
            j.effectType().name(), j.effectValue()
        );
    }

    /**
     * Save current run state to the database.
     */
    public void saveRunToDatabase() {
        List<PlayerData.JokerData> ownedData = ownedJokers.stream()
            .map(this::dataFromJoker).collect(Collectors.toList());
        List<PlayerData.JokerData> activeData = activeJokers.stream()
            .map(this::dataFromJoker).collect(Collectors.toList());

        PlayerData.RunData runData = new PlayerData.RunData(
            difficulty, difficultyMultiplier, currentAnte,
            maxAntes, baseTargetScore, targetPoints, ownedData, activeData
        );
        PlayerDatabase.saveRun(runData);
    }

    /**
     * Advance to the next ante. Returns true if there are more antes, false if the run is complete.
     */
    public boolean advanceAnte() {
        if (currentAnte >= maxAntes) {
            return false;
        }
        currentAnte++;
        targetPoints = (int) Math.round(baseTargetScore * Math.pow(difficultyMultiplier, currentAnte - 1));
        currentScore = 0;
        
        saveRunToDatabase();
        return true;
    }

    public void endSession() {
        this.difficulty = null;
        this.targetPoints = 0;
        this.currentScore = 0;
        this.currentAnte = 0;
        this.ownedJokers.clear();
        this.activeJokers.clear();
        
        PlayerDatabase.clearRun();
    }

    public String getDifficulty() { return difficulty; }
    public double getDifficultyMultiplier() { return difficultyMultiplier; }
    public int getCurrentAnte() { return currentAnte; }
    public int getMaxAntes() { return maxAntes; }
    public int getTargetPoints() { return targetPoints; }
    public int getCurrentScore() { return currentScore; }
    public void setScore(int score) { this.currentScore = score; }
    public List<Joker> getOwnedJokers() { return ownedJokers; }
    public List<Joker> getActiveJokers() { return activeJokers; }
}
