package cs;

import java.util.List;

/**
 * Stores the result of a hand evaluation.
 */
public record HandResult(String handName, int mult, List<Card> cardsUsed) {
}
