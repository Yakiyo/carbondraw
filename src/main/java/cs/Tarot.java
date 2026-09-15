package cs;

public record Tarot(
    String name,
    String description,
    String imagePath,
    TarotEffect effectType,
    int targetCount
) {
    public enum TarotEffect {
        ENHANCE_CHIPS,
        ENHANCE_MULTI,
        CHANGE_SUIT,
        GAIN_MONEY,
        DESTROY_CARD
    }
}
