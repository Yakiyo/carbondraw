package cs;

import java.util.List;

public class JokerRegistry {
    public static final List<Joker> JOKERS = List.of(
        new Joker("Basic Joker", "+4 Mult", "/cs/images/joker/joker.png", Joker.JokerEffect.ADD_MULTI, 4),
        new Joker("Greedy Joker", "+4 Mult for every renewable card played", "/cs/images/joker/greedy_joker.png", Joker.JokerEffect.ADD_MULT_PER_RENEWABLE, 4),
        new Joker("Lusty Joker", "+4 Mult for every biosphere card played", "/cs/images/joker/lusty_joker.png", Joker.JokerEffect.ADD_MULT_PER_BIOSPHERE, 4),
        new Joker("Wrathful Joker", "+4 Mult for every green tech card played", "/cs/images/joker/wrathful_joker.png", Joker.JokerEffect.ADD_MULT_PER_GREENTECH, 4),
        new Joker("Gluttonous Joker", "+4 Mult for every policy card played", "/cs/images/joker/gluttonous_joker.png", Joker.JokerEffect.ADD_MULT_PER_POLICY, 4),
        new Joker("Jolly Joker", "+8 Mult if played hand contains Grassroots Action", "/cs/images/joker/jolly_joker.png", Joker.JokerEffect.ADD_MULT_IF_PAIR, 8),
        new Joker("Zany Joker", "+12 Mult if played hand contains Sector Focus", "/cs/images/joker/zany_joker.png", Joker.JokerEffect.ADD_MULT_IF_THREE_KIND, 12),
        new Joker("Mad Joker", "+20 Mult if played hand contains Industry Overhaul", "/cs/images/joker/mad_joker.png", Joker.JokerEffect.ADD_MULT_IF_FOUR_KIND, 20),
        new Joker("Crazy Joker", "+12 Mult if played hand contains Holistic Strategy", "/cs/images/joker/crazy_joker.png", Joker.JokerEffect.ADD_MULT_IF_STRAIGHT, 12),
        new Joker("Droll Joker", "+10 Mult if played hand contains Monoculture", "/cs/images/joker/droll_joker.png", Joker.JokerEffect.ADD_MULT_IF_FLUSH, 10),
        new Joker("Sly Joker", "+50 Chips if played hand contains Grassroots Action", "/cs/images/joker/sly_joker.png", Joker.JokerEffect.ADD_CHIPS_IF_PAIR, 50),
        new Joker("Wily Joker", "+100 Chips if played hand contains Sector Focus", "/cs/images/joker/wily_joker.png", Joker.JokerEffect.ADD_CHIPS_IF_THREE_KIND, 100),
        new Joker("Clever Joker", "+150 Chips if played hand contains Bilateral Agreement", "/cs/images/joker/clever_joker.png", Joker.JokerEffect.ADD_CHIPS_IF_TWO_PAIR, 150),
        new Joker("Devious Joker", "+100 Chips if played hand contains Holistic Strategy", "/cs/images/joker/devious_joker.png", Joker.JokerEffect.ADD_CHIPS_IF_STRAIGHT, 100),
        new Joker("Crafty Joker", "+80 Chips if played hand contains Monoculture", "/cs/images/joker/crafty_joker.png", Joker.JokerEffect.ADD_CHIPS_IF_FLUSH, 80),
        new Joker("Half Joker", "+20 Mult if played hand contains 3 or fewer cards", "/cs/images/joker/half_joker.png", Joker.JokerEffect.ADD_MULT_IF_SMALL_HAND, 20),
        new Joker("Banner", "+40 Chips for each remaining discard", "/cs/images/joker/banner.png", Joker.JokerEffect.ADD_CHIPS_PER_DISCARD, 40),
        new Joker("Mystic Summit", "+15 Mult when 0 discards remaining", "/cs/images/joker/mystic_summit.png", Joker.JokerEffect.ADD_MULT_IF_NO_DISCARDS, 15)
    );
}
