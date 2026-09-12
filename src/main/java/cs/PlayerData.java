package cs;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private int currency;
    private RunData activeRun;

    public PlayerData() {
        this.currency = 0;
        this.activeRun = null;
    }

    public int getCurrency() { return currency; }
    public void setCurrency(int currency) { this.currency = currency; }

    public RunData getActiveRun() { return activeRun; }
    public void setActiveRun(RunData activeRun) { this.activeRun = activeRun; }

    /**
     * Represents a saved run state that can be persisted to JSON.
     */
    public static class RunData {
        private String difficulty;
        private double difficultyMultiplier;
        private int currentAnte;
        private int maxAntes;
        private int baseTargetScore;
        private int targetPoints;
        private List<JokerData> ownedJokers;
        private List<JokerData> activeJokers;

        public RunData() {
            this.ownedJokers = new ArrayList<>();
            this.activeJokers = new ArrayList<>();
        }

        public RunData(String difficulty, double difficultyMultiplier, int currentAnte,
                       int maxAntes, int baseTargetScore, int targetPoints,
                       List<JokerData> ownedJokers, List<JokerData> activeJokers) {
            this.difficulty = difficulty;
            this.difficultyMultiplier = difficultyMultiplier;
            this.currentAnte = currentAnte;
            this.maxAntes = maxAntes;
            this.baseTargetScore = baseTargetScore;
            this.targetPoints = targetPoints;
            this.ownedJokers = ownedJokers != null ? ownedJokers : new ArrayList<>();
            this.activeJokers = activeJokers != null ? activeJokers : new ArrayList<>();
        }

        public String getDifficulty() { return difficulty; }
        public double getDifficultyMultiplier() { return difficultyMultiplier; }
        public int getCurrentAnte() { return currentAnte; }
        public int getMaxAntes() { return maxAntes; }
        public int getBaseTargetScore() { return baseTargetScore; }
        public int getTargetPoints() { return targetPoints; }
        public List<JokerData> getOwnedJokers() { return ownedJokers; }
        public List<JokerData> getActiveJokers() { return activeJokers; }
    }

    /**
     * Serializable representation of a Joker for JSON storage.
     */
    public static class JokerData {
        private String name;
        private String description;
        private String imagePath;
        private String effectType;
        private int effectValue;

        public JokerData() {}

        public JokerData(String name, String description, String imagePath, String effectType, int effectValue) {
            this.name = name;
            this.description = description;
            this.imagePath = imagePath;
            this.effectType = effectType;
            this.effectValue = effectValue;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getImagePath() { return imagePath; }
        public String getEffectType() { return effectType; }
        public int getEffectValue() { return effectValue; }
    }
}
