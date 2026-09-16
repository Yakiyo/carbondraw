package cs;

public record Joker(
    String name,
    String description,
    String imagePath,
    JokerEffect effectType,
    int effectValue
) {
    public enum JokerEffect {
        ADD_CHIPS,
        ADD_MULTI,
        MULT_MULTI,
        ADD_MULT_PER_RENEWABLE,
        ADD_MULT_PER_BIOSPHERE,
        ADD_MULT_PER_GREENTECH,
        ADD_MULT_PER_POLICY,
        ADD_MULT_IF_PAIR,
        ADD_MULT_IF_THREE_KIND,
        ADD_MULT_IF_FOUR_KIND,
        ADD_MULT_IF_STRAIGHT,
        ADD_MULT_IF_FLUSH,
        ADD_CHIPS_IF_PAIR,
        ADD_CHIPS_IF_TWO_PAIR,
        ADD_CHIPS_IF_THREE_KIND,
        ADD_CHIPS_IF_STRAIGHT,
        ADD_CHIPS_IF_FLUSH,
        ADD_MULT_IF_SMALL_HAND,
        ADD_CHIPS_PER_DISCARD,
        ADD_MULT_IF_NO_DISCARDS
    }

    public java.io.InputStream getImageStream() {
        return getClass().getResourceAsStream(imagePath);
    }
}
