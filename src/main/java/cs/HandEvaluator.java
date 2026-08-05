package cs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class to evaluate card hands using a left-to-right greedy extraction.
 */
public class HandEvaluator {

    /**
     * Extracts multiple valid hands from left to right.
     */
    public static List<HandResult> evaluateAllHands(List<Card> playedCards) {
        List<HandResult> results = new ArrayList<>();
        if (playedCards == null || playedCards.isEmpty()) {
            return results;
        }

        List<Card> remaining = new ArrayList<>(playedCards);

        while (!remaining.isEmpty()) {
            HandResult bestHand = findBestHandContainingFirst(remaining);
            results.add(bestHand);
            // Remove the cards used in this hand from the remaining pool
            for (Card c : bestHand.cardsUsed()) {
                remaining.remove(c);
            }
        }

        return results;
    }

    private static HandResult findBestHandContainingFirst(List<Card> remaining) {
        Card anchor = remaining.get(0);
        List<Card> pool = new ArrayList<>(remaining);
        pool.remove(0); // Pool of cards to the right

        HandResult bestResult = new HandResult("High Card", 1, List.of(anchor));
        int bestPriority = getPriority("High Card");

        // Generate all subsets of pool up to size 4 to form max 5-card hands
        for (int size = 4; size >= 1; size--) {
            List<List<Card>> combinations = generateCombinations(pool, size);
            for (List<Card> combo : combinations) {
                List<Card> candidate = new ArrayList<>();
                candidate.add(anchor);
                candidate.addAll(combo);

                HandResult result = evaluateSingleCombination(candidate);
                if (result != null) {
                    int priority = getPriority(result.handName());
                    if (priority > bestPriority) {
                        bestPriority = priority;
                        bestResult = result;
                    }
                }
            }
        }

        return bestResult;
    }

    private static HandResult evaluateSingleCombination(List<Card> cards) {
        int size = cards.size();

        if (size == 5) {
            long smartGridCount = cards.stream().filter(c -> "Smart Grid AI".equalsIgnoreCase(c.getName())).count();
            long renewableCount = cards.stream().filter(c -> "Renewable Energy".equalsIgnoreCase(c.getName())).count();
            if (smartGridCount == 1 && renewableCount == 4) return new HandResult("The Smart Grid", 50, cards);

            long biosphereCount = cards.stream().filter(c -> isSuit(c, "biosphere")).count();
            long distinctNames = cards.stream().map(Card::getName).distinct().count();
            if (biosphereCount == 5 && distinctNames == 5) return new HandResult("Rewilding", 40, cards);

            long policyCount = cards.stream().filter(c -> isSuit(c, "policy")).count();
            long distinctTiers = cards.stream().map(Card::getTier).distinct().count();
            if (policyCount == 5 && distinctTiers == 1) return new HandResult("Bureaucratic Gridlock", 0, cards);
        }

        Map<String, Long> suitCounts = cards.stream().collect(Collectors.groupingBy(c -> c.getSuit() == null ? "" : c.getSuit(), Collectors.counting()));
        List<Long> sortedCounts = suitCounts.values().stream().sorted(Collections.reverseOrder()).collect(Collectors.toList());
        long max = sortedCounts.isEmpty() ? 0 : sortedCounts.get(0);
        long second = sortedCounts.size() > 1 ? sortedCounts.get(1) : 0;

        boolean isSequential = false;
        if (size == 5) {
            List<Integer> tiers = cards.stream().map(Card::getTier).sorted().collect(Collectors.toList());
            isSequential = true;
            for (int i = 1; i < tiers.size(); i++) {
                if (tiers.get(i) - tiers.get(i - 1) != 1) {
                    isSequential = false; break;
                }
            }
        }

        if (size == 5 && max == 5 && isSequential) return new HandResult("Net Zero Earthshot", 20, cards);
        if (size == 4 && max == 4) return new HandResult("Industry Overhaul", 15, cards);
        if (size == 5 && max == 3 && second == 2) return new HandResult("Symbiotic Loop", 12, cards);
        if (size == 5 && max == 5) return new HandResult("Monoculture", 10, cards);
        if (size == 5 && isSequential) return new HandResult("Holistic Strategy", 8, cards);
        if (size == 3 && max == 3) return new HandResult("Sector Focus", 5, cards);
        if (size == 4 && max == 2 && second == 2) return new HandResult("Bilateral Agreement", 4, cards);
        if (size == 2 && max == 2) return new HandResult("Grassroots Action", 2, cards);

        if (size == 1) return new HandResult("High Card", 1, cards);

        return null; // Invalid hand combination
    }

    private static boolean isSuit(Card c, String target) {
        return c.getSuit() != null && c.getSuit().toLowerCase().contains(target.toLowerCase());
    }

    private static int getPriority(String handName) {
        return switch (handName) {
            case "The Smart Grid" -> 13;
            case "Rewilding" -> 12;
            case "Bureaucratic Gridlock" -> 11;
            case "Net Zero Earthshot" -> 10;
            case "Industry Overhaul" -> 9;
            case "Symbiotic Loop" -> 8;
            case "Monoculture" -> 7;
            case "Holistic Strategy" -> 6;
            case "Sector Focus" -> 5;
            case "Bilateral Agreement" -> 4;
            case "Grassroots Action" -> 3;
            case "High Card" -> 2;
            default -> 0;
        };
    }

    private static List<List<Card>> generateCombinations(List<Card> list, int k) {
        List<List<Card>> result = new ArrayList<>();
        generateCombinationsHelper(list, k, 0, new ArrayList<>(), result);
        return result;
    }

    private static void generateCombinationsHelper(List<Card> list, int k, int start, List<Card> current, List<List<Card>> result) {
        if (current.size() == k) {
            result.add(new ArrayList<>(current));
            return;
        }
        for (int i = start; i < list.size(); i++) {
            current.add(list.get(i));
            generateCombinationsHelper(list, k, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }
}
