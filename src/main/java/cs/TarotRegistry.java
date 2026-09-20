package cs;

import java.util.List;

/**
 * Registry for all available Breakthroughs (Tarots) in the game.
 */
public class TarotRegistry {
    public static final List<Tarot> TAROTS = List.of(
        new Tarot(
            "The Hermit",
            "Doubles your current coins (Max 20 Coins).",
            "/cs/images/tarot/green_investment.png",
            Tarot.TarotEffect.GAIN_MONEY,
            0
        ),
        new Tarot(
            "The Hierophant",
            "Enhances 1 selected card to permanently give +30 points.",
            "/cs/images/tarot/subsidies.png",
            Tarot.TarotEffect.ENHANCE_CHIPS,
            1
        ),
        new Tarot(
            "The Empress",
            "Enhances 1 selected card to permanently give +4 Mult.",
            "/cs/images/tarot/carbon_tax.png",
            Tarot.TarotEffect.ENHANCE_MULTI,
            1
        ),
        new Tarot(
            "The Magician",
            "Enhances 1 selected card to be a Lucky card.",
            "/cs/images/tarot/geoengineering.png",
            Tarot.TarotEffect.ENHANCE_LUCKY,
            1
        ),
        new Tarot(
            "The Lovers",
            "Enhances 1 selected card to be a Wild card.",
            "/cs/images/tarot/grid_integration.png",
            Tarot.TarotEffect.ENHANCE_WILD,
            1
        ),
        new Tarot(
            "The Chariot",
            "Enhances 1 selected card to be a Steel card.",
            "/cs/images/tarot/carbon_capture.png",
            Tarot.TarotEffect.ENHANCE_STEEL,
            1
        ),
        new Tarot(
            "Justice",
            "Enhances 1 selected card to be a Glass card.",
            "/cs/images/tarot/regulatory_mandate.png",
            Tarot.TarotEffect.ENHANCE_GLASS,
            1
        ),
        new Tarot(
            "The Hanged Man",
            "Destroys up to 2 selected cards permanently.",
            "/cs/images/tarot/research_grant.png",
            Tarot.TarotEffect.DESTROY_CARD,
            2
        ),
        new Tarot(
            "The Emperor",
            "Spawns 2 random Jokers (Must have room).",
            "/cs/images/tarot/global_summit.png",
            Tarot.TarotEffect.SPAWN_JOKER,
            0
        ),
        new Tarot(
            "The High Priestess",
            "Spawns 2 random Breakthroughs (Must have room).",
            "/cs/images/tarot/policy_overhaul.png",
            Tarot.TarotEffect.SPAWN_TAROT,
            0
        )
    );
}
