package cs;

public record Voucher(
    String name,
    String description,
    EffectType effectType,
    String imagePath
) {
    public enum EffectType {
        EXTRA_JOKER_SLOT,
        CLEARANCE_SALE,
        EXTRA_HAND,
        OVERSTOCK,
        REROLL_SURPLUS,
        EXTRA_DISCARD
    }
}
