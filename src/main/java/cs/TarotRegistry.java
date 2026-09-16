package cs;

import java.util.List;

/**
 * Registry for all available Breakthroughs (Tarots) in the game.
 */
public class TarotRegistry {
    public static final List<Tarot> TAROTS = List.of(
        new Tarot(
            "Green Investment",
            "Doubles your current coins (Max 20 Coins).",
            "/cs/images/tarot/green_investment.png",
            Tarot.TarotEffect.GAIN_MONEY,
            0
        ),
        new Tarot(
            "Subsidies",
            "Enhances 1 selected card to permanently give +30 points.",
            "/cs/images/tarot/subsidies.png",
            Tarot.TarotEffect.ENHANCE_CHIPS,
            1
        ),
        new Tarot(
            "Carbon Tax",
            "Enhances 1 selected card to permanently give +4 Mult.",
            "/cs/images/tarot/carbon_tax.png",
            Tarot.TarotEffect.ENHANCE_MULTI,
            1
        ),
        new Tarot(
            "Geoengineering",
            "Enhances 1 selected card to be a Lucky card.",
            "/cs/images/tarot/geoengineering.png",
            Tarot.TarotEffect.ENHANCE_LUCKY,
            1
        ),
        new Tarot(
            "Grid Integration",
            "Enhances 1 selected card to be a Wild card.",
            "/cs/images/tarot/grid_integration.png",
            Tarot.TarotEffect.ENHANCE_WILD,
            1
        ),
        new Tarot(
            "Carbon Capture",
            "Enhances 1 selected card to be a Steel card.",
            "/cs/images/tarot/carbon_capture.png",
            Tarot.TarotEffect.ENHANCE_STEEL,
            1
        ),
        new Tarot(
            "Regulatory Mandate",
            "Enhances 1 selected card to be a Glass card.",
            "/cs/images/tarot/regulatory_mandate.png",
            Tarot.TarotEffect.ENHANCE_GLASS,
            1
        ),
        new Tarot(
            "Research Grant",
            "Destroys up to 2 selected cards permanently.",
            "/cs/images/tarot/research_grant.png",
            Tarot.TarotEffect.DESTROY_CARD,
            2
        ),
        new Tarot(
            "Global Summit",
            "Spawns 2 random Jokers (Must have room).",
            "/cs/images/tarot/global_summit.png",
            Tarot.TarotEffect.SPAWN_JOKER,
            0
        ),
        new Tarot(
            "Policy Overhaul",
            "Spawns 2 random Breakthroughs (Must have room).",
            "/cs/images/tarot/policy_overhaul.png",
            Tarot.TarotEffect.SPAWN_TAROT,
            0
        )
    );
}
