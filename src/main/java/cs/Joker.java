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
        MULT_MULTI
    }

    public java.io.InputStream getImageStream() {
        return getClass().getResourceAsStream(imagePath);
    }
}
