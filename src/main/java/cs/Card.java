package cs;

/**
 * Represents a card in the Carbon Draw deckbuilding game.
 */
public class Card {
    private String name;
    private String category;
    private String imagePath;
    private int points;
    private Enhancement enhancement;

    public enum Enhancement {
        NONE, MULT, BONUS, LUCKY, WILD, STEEL, GLASS
    }

    public Card(String name, String category, String imagePath, int points) {
        this.name = name;
        this.category = category;
        this.imagePath = imagePath;
        this.points = points;
        this.enhancement = Enhancement.NONE;
    }

    public Card(String name, String category, String imagePath, int points, Enhancement enhancement) {
        this.name = name;
        this.category = category;
        this.imagePath = imagePath;
        this.points = points;
        this.enhancement = enhancement != null ? enhancement : Enhancement.NONE;
    }

    public String name() { return name; }
    public String category() { return category; }
    public String imagePath() { return imagePath; }
    public int points() { return points; }
    public Enhancement enhancement() { return enhancement; }

    public void setEnhancement(Enhancement enhancement) {
        this.enhancement = enhancement;
    }

    /**
     * Helper to get the stream for loading the image in JavaFX.
     * Usage: Image img = new Image(card.getImageStream());
     */
    public java.io.InputStream getImageStream() {
        return getClass().getResourceAsStream(imagePath);
    }

    // Standard getters required for hand evaluation
    public String getName() {
        return name;
    }

    public String getSuit() {
        if (enhancement == Enhancement.WILD) {
            return "WILD"; // Special handling in hand evaluator
        }
        return category;
    }

    public String getOriginalSuit() {
        return category;
    }

    public int getTier() {
        // Since we don't have an explicit tier field yet, derive it from points
        return points / 5; 
    }
}
