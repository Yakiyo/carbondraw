package cs;

import java.util.ArrayList;
import java.util.List;

public class PlayerData {
    private RunData activeRun;

    public PlayerData() {
        this.activeRun = null;
    }

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
        private List<TarotData> ownedTarots;
        private List<CardData> deck;

        private int coins;

        public RunData() {
            this.ownedJokers = new ArrayList<>();
            this.activeJokers = new ArrayList<>();
            this.ownedTarots = new ArrayList<>();
            this.deck = new ArrayList<>();
            this.coins = 0;
        }

        public RunData(String difficulty, double difficultyMultiplier, int currentAnte,
                       int maxAntes, int baseTargetScore, int targetPoints,
                       List<JokerData> ownedJokers, List<JokerData> activeJokers, 
                       List<TarotData> ownedTarots, List<CardData> deck, int coins) {
            this.difficulty = difficulty;
            this.difficultyMultiplier = difficultyMultiplier;
            this.currentAnte = currentAnte;
            this.maxAntes = maxAntes;
            this.baseTargetScore = baseTargetScore;
            this.targetPoints = targetPoints;
            this.ownedJokers = ownedJokers != null ? ownedJokers : new ArrayList<>();
            this.activeJokers = activeJokers != null ? activeJokers : new ArrayList<>();
            this.ownedTarots = ownedTarots != null ? ownedTarots : new ArrayList<>();
            this.deck = deck != null ? deck : new ArrayList<>();
            this.coins = coins;
        }

        public String getDifficulty() { return difficulty; }
        public double getDifficultyMultiplier() { return difficultyMultiplier; }
        public int getCurrentAnte() { return currentAnte; }
        public int getMaxAntes() { return maxAntes; }
        public int getBaseTargetScore() { return baseTargetScore; }
        public int getTargetPoints() { return targetPoints; }
        public List<JokerData> getOwnedJokers() { return ownedJokers; }
        public List<JokerData> getActiveJokers() { return activeJokers; }
        public List<TarotData> getOwnedTarots() { return ownedTarots; }
        public List<CardData> getDeck() { return deck; }
        public int getCoins() { return coins; }
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

    /**
     * Serializable representation of a Tarot for JSON storage.
     */
    public static class TarotData {
        private String name;
        private String description;
        private String imagePath;
        private String effectType;
        private int targetCount;

        public TarotData() {}

        public TarotData(String name, String description, String imagePath, String effectType, int targetCount) {
            this.name = name;
            this.description = description;
            this.imagePath = imagePath;
            this.effectType = effectType;
            this.targetCount = targetCount;
        }

        public String getName() { return name; }
        public String getDescription() { return description; }
        public String getImagePath() { return imagePath; }
        public String getEffectType() { return effectType; }
        public int getTargetCount() { return targetCount; }
    }

    /**
     * Serializable representation of a Card for JSON storage.
     */
    public static class CardData {
        private String name;
        private String category;
        private String imagePath;
        private int points;
        private String enhancement;

        public CardData() {}

        public CardData(String name, String category, String imagePath, int points, String enhancement) {
            this.name = name;
            this.category = category;
            this.imagePath = imagePath;
            this.points = points;
            this.enhancement = enhancement;
        }

        public String getName() { return name; }
        public String getCategory() { return category; }
        public String getImagePath() { return imagePath; }
        public int getPoints() { return points; }
        public String getEnhancement() { return enhancement; }
    }
}
