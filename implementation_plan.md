# Implementation Plan: Vouchers System Integration

We are ready to integrate the Vouchers into the game! We'll use the provided `images/vouchers` assets and adapt their effects to fit our economy and mechanics.

## User Review Required
Please review the proposed Voucher mechanics below. Some of these have been re-balanced to fit our specific economy (e.g., our reroll costs 50 Coins, so the standard Balatro $2 discount needs to scale accordingly).

## Proposed Vouchers & Effects
1. **Antimatter**: +1 Max Joker Slot (Increases limit from 3 to 4).
2. **Clearance Sale**: 25% discount on all Shop Items (Jokers and Tarots drop from 200 Coins to 150 Coins).
3. **Grabber**: +1 Starting Hand per round.
4. **Overstock**: +1 Shop Slot (Shop will roll 5 Jokers and 5 Tarots instead of 4).
5. **Reroll Surplus**: Reduces the Shop Refresh cost from 50 Coins to 25 Coins.
6. **Wasteful**: +1 Starting Discard per round.

> [!NOTE]
> Vouchers in Balatro typically cost $10 (twice the price of a standard Joker). Because our Jokers cost 200 Coins, I propose making **Vouchers cost 400 Coins**.

## Proposed Changes

---

### 1. Backend Data Structures
#### [NEW] `Voucher.java`
- A new record/class representing a Voucher with an `effectType()` and properties.

#### [NEW] `VoucherRegistry.java`
- A central registry defining the 6 Vouchers and pointing to their respective `.png` assets.

#### [MODIFY] `PlayerData.java` & `GameSession.java`
- Add a `List<Voucher> ownedVouchers`.
- Add state flags for `extraJokerSlots` (Antimatter), `shopDiscount` (Clearance Sale), `extraShopSlots` (Overstock), and `rerollDiscount` (Reroll Surplus).
- Ensure all states save and load correctly.

### 2. Shop Integration
#### [MODIFY] `ShopController.java`
- Implement `handleShowVouchers()` to render the Vouchers tab. The shop will offer **1 random Voucher per round**.
- Implement logic to apply `Clearance Sale` (reduce Joker/Tarot prices to 150) and `Reroll Surplus` (reduce reroll cost to 25).
- Modify the shop rolling logic to respect `Overstock` (roll 5 items instead of 4).

### 3. Gameplay Mechanics Integration
#### [MODIFY] `GameController.java` & `DeckController.java`
- Ensure Joker capacity respects the base (3) + `extraJokerSlots`.

## Open Questions
1. **Voucher Pricing:** Is 400 Coins per Voucher acceptable, or do you prefer a different price point?
2. **Reroll Discount:** Is reducing the reroll cost from 50 to 25 Coins balanced enough for `Reroll Surplus`?
