# Project Analysis: Carbon Draw (Balatro-inspired)

## Current Implementation Status
I have analyzed the `d:\Codes\cdraw` workspace. You have successfully established the foundational framework for a Balatro-style game, themed around climate change and sustainability ("Carbon Draw"). 

Here is what has been implemented so far:

**1. Core Framework & Technologies**
*   **JavaFX:** Handling the graphical user interface via FXML (`game.fxml`, `home.fxml`, `difficulty.fxml`) and CSS.
*   **Architecture:** Follows an MVC-like pattern with Controllers (`GameController`, `HomeController`), Models (`Card`, `HandResult`), and State management (`GameSession`).
*   **Persistence:** Uses `Gson` to read/write persistent player currency into `player_data.json`.

**2. Game Mechanics & Loop**
*   **Themed Deck:** Cards have suits based on sustainability topics (`biosphere`, `green_tech`, `policy_and_society`, `renewable`) and base point values.
*   **Game State:** The player is dealt a 10-card hand. They have a limited number of **Hands (4)** and **Discards (3)** per round.
*   **Selection & Discard:** Players can select up to 5 cards to either Play or Discard. Discarding replenishes the selected cards from the deck.
*   **Scoring System (`HandEvaluator`):** You have a fully functional Poker hand evaluator that has been customized for your theme. Hands like "Net Zero Earthshot" (Straight Flush), "Industry Overhaul" (Four of a Kind), or "Monoculture" (Flush) map to specific Multipliers.
*   **Math:** The final score per hand is calculated as `(Sum of Played Card Points) * (Hand Multiplier)`.
*   **Economy:** Excess points scored over the target requirement are converted into "Coins" and saved to the player's bank (`PlayerDatabase`).

---

## Proposed Balatro Features to Implement Next
To fully capture the depth and roguelike replayability of Balatro, here are the key features we can build on top of your current foundation:

### 1. Jokers (Passive Modifiers)
*   **Concept:** The core of Balatro's build-crafting. These would be permanent passive cards (e.g., max 5 slots) that sit above the board and trigger during scoring.
*   **Theme Ideas:** "Innovations", "Advisors", or "Policies". 
*   **Mechanic:** Some add flat points (+Chips), some add flat Multiplier (+Mult), and some multiply the Multiplier (xMult). E.g., *"Solar Subsidies: +15 Mult if hand contains a Renewable card."*

### 2. The Ante & Blind Structure (Run Progression)
*   **Concept:** Instead of a single difficulty/target, the game should be structured into runs (Antes). 
*   **Mechanic:** Each Ante has 3 stages: Small Blind, Big Blind, and Boss Blind. 
*   **Boss Blinds:** These apply a specific debuff to the round (e.g., *"Corporate Lobbying: Policy cards do not score"* or *"Grid Failure: Must play 5 cards every hand"*).

### 3. The Shop & Economy
*   **Concept:** A between-round shop where players spend the Coins they earn from beating Blinds and cashing out unused hands/discards.
*   **Mechanic:** Players can buy new cards for their deck, purchase Jokers, or buy consumable packs.

### 4. Planet Cards (Hand Upgrades)
*   **Concept:** Consumable items bought in the shop that permanently level up specific poker hands for the duration of the run.
*   **Mechanic:** Upgrading "Monoculture" (Flush) to Level 2 might increase its base chips by 15 and its multiplier by 2 permanently. (Notice you already have "lvl.1" hardcoded in the UI, this is the perfect next step for that).

### 5. Tarot Cards (Card Enhancements)
*   **Concept:** Consumables that apply permanent modifiers to individual playing cards in the deck.
*   **Mechanic:** 
    *   **Editions:** Foil (+50 Chips), Holographic (+10 Mult), Polychrome (x1.5 Mult).
    *   **Enhancements:** Glass cards (x2 Mult but chance to break), Steel cards (+Mult while held in hand), or changing a card's suit.

### 6. Vouchers (Run Upgrades)
*   **Concept:** Expensive, permanent passive buffs for the entire run found in the shop.
*   **Mechanic:** e.g., +1 Hand per round, +1 Discard per round, -25% Shop Prices, or unlocking an extra Joker slot.

### 7. Tags (Skip Rewards)
*   **Concept:** Allowing players to skip the Small or Big Blind to receive an immediate reward (Tag) instead of playing the round and getting shop access.
*   **Mechanic:** e.g., "Free Reroll in the next shop", "The next Joker you see is free and Polychrome", or "Gain $15 for every Boss Blind defeated".

### 8. Starting Decks
*   **Concept:** Different colored decks that the player can choose before starting a run, offering different starting conditions.
*   **Mechanic:** E.g., "Green Deck" (Earn extra money per remaining Hand), "Blue Deck" (+1 Hand per round).
