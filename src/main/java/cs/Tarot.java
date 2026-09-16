package cs;

public record Tarot(
    String name,
    String description,
    String imagePath,
    TarotEffect effectType,
    int targetCount
) {
    public enum TarotEffect {
        GAIN_MONEY,
        ENHANCE_CHIPS,
        ENHANCE_MULTI,
        ENHANCE_LUCKY,
        ENHANCE_WILD,
        ENHANCE_STEEL,
        ENHANCE_GLASS,
        DESTROY_CARD,
        SPAWN_JOKER,
        SPAWN_TAROT
    }
}
