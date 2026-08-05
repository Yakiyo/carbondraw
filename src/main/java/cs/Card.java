package cs;

/**
 * Represents a card in the Carbon Draw deckbuilding game.
 */
public record Card(
    String name,
    String category,
    String imagePath,
    int points
) {
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
        return category;
    }

    public int getTier() {
        // Since we don't have an explicit tier field yet, derive it from points
        return points / 5; 
    }
}
