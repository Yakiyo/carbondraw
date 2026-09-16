package cs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Singleton state holder for the active game session.
 */
public class GameSession {
    private static GameSession instance;
    private String difficulty;
    private double difficultyMultiplier;
    private int currentAnte;
    private int maxAntes;
    private int baseTargetScore;
    private int targetPoints;
    private int currentScore;
    private List<Joker> ownedJokers = new ArrayList<>();
    private List<Joker> activeJokers = new ArrayList<>();
    private List<Tarot> ownedTarots = new ArrayList<>();
    private List<Tarot> activeTarots = new ArrayList<>();
    private List<Card> currentDeck = new ArrayList<>();
    private List<Joker> currentShopJokers = new ArrayList<>();
    private List<Tarot> currentShopTarots = new ArrayList<>();
    private int coins;
    private boolean shopResetUsed;
    private int extraHands;
    private int extraDiscards;

    private List<String> ownedVouchers = new ArrayList<>();
    private String currentShopVoucher = null;
    private int extraJokerSlots = 0;
    private boolean shopDiscountActive = false;
    private int extraShopSlots = 0;
    private boolean rerollDiscountActive = false;

    private GameSession() {}

    public static GameSession getInstance() {
        if (instance == null) {
            instance = new GameSession();
        }
        return instance;
    }

    public void startNewGame(String difficulty, double multiplier) {
        this.difficulty = difficulty;
        this.difficultyMultiplier = multiplier;
        this.currentAnte = 1;
        this.maxAntes = 10;
        this.baseTargetScore = 1000;
        this.targetPoints = baseTargetScore;
        this.currentScore = 0;
        
        this.ownedJokers.clear();
        this.activeJokers.clear();
        this.ownedTarots.clear();
        this.currentDeck = new ArrayList<>(CardData.CARDS); // Copy initial deck
        this.currentShopJokers.clear();
        this.currentShopTarots.clear();
        this.coins = 0;
        this.shopResetUsed = false;
        this.extraHands = 0;
        this.extraDiscards = 0;

        this.ownedVouchers.clear();
        this.currentShopVoucher = null;
        this.extraJokerSlots = 0;
        this.shopDiscountActive = false;
        this.extraShopSlots = 0;
        this.rerollDiscountActive = false;
        
        // Save the run immediately so it can be continued later
        saveRunToDatabase();
    }

    /**
     * Restore a run from saved database data.
     */
    public boolean loadFromDatabase() {
        PlayerData.RunData runData = PlayerDatabase.loadRun();
        if (runData == null) return false;

        this.difficulty = runData.getDifficulty();
        this.difficultyMultiplier = runData.getDifficultyMultiplier();
        this.currentAnte = runData.getCurrentAnte();
        this.maxAntes = runData.getMaxAntes();
        this.baseTargetScore = runData.getBaseTargetScore();
        this.targetPoints = runData.getTargetPoints();
        this.currentScore = 0;
        this.coins = runData.getCoins();
        this.shopResetUsed = runData.isShopResetUsed();
        this.extraHands = runData.getExtraHands();
        this.extraDiscards = runData.getExtraDiscards();
        
        this.ownedVouchers.clear();
        if (runData.getOwnedVouchers() != null) {
            this.ownedVouchers.addAll(runData.getOwnedVouchers());
        }
        this.currentShopVoucher = runData.getCurrentShopVoucher();
        this.extraJokerSlots = runData.getExtraJokerSlots();
        this.shopDiscountActive = runData.isShopDiscountActive();
        this.extraShopSlots = runData.getExtraShopSlots();
        this.rerollDiscountActive = runData.isRerollDiscountActive();

        // Restore owned jokers
        this.ownedJokers.clear();
        if (runData.getOwnedJokers() != null) {
            for (PlayerData.JokerData jd : runData.getOwnedJokers()) {
                this.ownedJokers.add(jokerFromData(jd));
            }
        }

        // Restore active jokers
        this.activeJokers.clear();
        if (runData.getActiveJokers() != null) {
            for (PlayerData.JokerData jd : runData.getActiveJokers()) {
                this.activeJokers.add(jokerFromData(jd));
            }
        }

        // Restore tarots
        this.ownedTarots.clear();
        if (runData.getOwnedTarots() != null) {
            for (PlayerData.TarotData td : runData.getOwnedTarots()) {
                this.ownedTarots.add(tarotFromData(td));
            }
        }
        
        this.activeTarots.clear();
        if (runData.getActiveTarots() != null) {
            for (PlayerData.TarotData td : runData.getActiveTarots()) {
                this.activeTarots.add(tarotFromData(td));
            }
        }

        // Restore deck
        this.currentDeck.clear();
        if (runData.getDeck() != null) {
            for (PlayerData.CardData cd : runData.getDeck()) {
                this.currentDeck.add(cardFromData(cd));
            }
        }

        // Restore shop inventory
        this.currentShopJokers.clear();
        if (runData.getShopJokers() != null) {
            for (PlayerData.JokerData jd : runData.getShopJokers()) {
                this.currentShopJokers.add(jokerFromData(jd));
            }
        }
        
        this.currentShopTarots.clear();
        if (runData.getShopTarots() != null) {
            for (PlayerData.TarotData td : runData.getShopTarots()) {
                this.currentShopTarots.add(tarotFromData(td));
            }
        }

        return true;
    }

    private Joker jokerFromData(PlayerData.JokerData jd) {
        return JokerRegistry.JOKERS.stream()
            .filter(j -> j.name().equals(jd.getName()))
            .findFirst()
            .orElseGet(() -> new Joker(
                jd.getName(), jd.getDescription(), jd.getImagePath(),
                Joker.JokerEffect.valueOf(jd.getEffectType()), jd.getEffectValue()
            ));
    }

    private PlayerData.JokerData dataFromJoker(Joker j) {
        return new PlayerData.JokerData(
            j.name(), j.description(), j.imagePath(),
            j.effectType().name(), j.effectValue()
        );
    }

    private Tarot tarotFromData(PlayerData.TarotData td) {
        return TarotRegistry.TAROTS.stream()
            .filter(t -> t.name().equals(td.getName()))
            .findFirst()
            .orElseGet(() -> new Tarot(
                td.getName(), td.getDescription(), td.getImagePath(),
                Tarot.TarotEffect.valueOf(td.getEffectType()), td.getTargetCount()
            ));
    }

    private PlayerData.TarotData dataFromTarot(Tarot t) {
        return new PlayerData.TarotData(
            t.name(), t.description(), t.imagePath(),
            t.effectType().name(), t.targetCount()
        );
    }

    private Card cardFromData(PlayerData.CardData cd) {
        Card.Enhancement enhancement = cd.getEnhancement() != null ? 
            Card.Enhancement.valueOf(cd.getEnhancement()) : Card.Enhancement.NONE;
        return new Card(
            cd.getName(), cd.getCategory(), cd.getImagePath(), cd.getPoints(), enhancement
        );
    }

    private PlayerData.CardData dataFromCard(Card c) {
        return new PlayerData.CardData(
            c.name(), c.category(), c.imagePath(), c.points(), 
            c.enhancement() != null ? c.enhancement().name() : Card.Enhancement.NONE.name()
        );
    }

    /**
     * Save current run state to the database.
     */
    public void saveRunToDatabase() {
        List<PlayerData.JokerData> ownedData = ownedJokers.stream()
            .map(this::dataFromJoker).collect(Collectors.toList());
        List<PlayerData.JokerData> activeData = activeJokers.stream()
            .map(this::dataFromJoker).collect(Collectors.toList());
        List<PlayerData.TarotData> tarotData = ownedTarots.stream()
            .map(this::dataFromTarot).collect(Collectors.toList());
        List<PlayerData.TarotData> activeTarotData = activeTarots.stream()
            .map(this::dataFromTarot).collect(Collectors.toList());
        List<PlayerData.CardData> deckData = currentDeck.stream()
            .map(this::dataFromCard).collect(Collectors.toList());
        List<PlayerData.JokerData> shopJokersData = currentShopJokers.stream()
            .map(this::dataFromJoker).collect(Collectors.toList());
        List<PlayerData.TarotData> shopTarotsData = currentShopTarots.stream()
            .map(this::dataFromTarot).collect(Collectors.toList());

        PlayerData.RunData runData = new PlayerData.RunData(
            difficulty, difficultyMultiplier, currentAnte,
            maxAntes, baseTargetScore, targetPoints, ownedData, activeData, tarotData, activeTarotData, deckData, 
            shopJokersData, shopTarotsData, coins, shopResetUsed, extraHands, extraDiscards,
            ownedVouchers, currentShopVoucher, extraJokerSlots, shopDiscountActive, extraShopSlots, rerollDiscountActive
        );
        PlayerDatabase.saveRun(runData);
    }

    /**
     * Advance to the next ante. Returns true if there are more antes, false if the run is complete.
     */
    public boolean advanceAnte() {
        if (currentAnte >= maxAntes) {
            return false;
        }
        currentAnte++;
        targetPoints = (int) Math.round(baseTargetScore * Math.pow(difficultyMultiplier, currentAnte - 1));
        currentScore = 0;
        shopResetUsed = false;
        currentShopJokers.clear();
        currentShopTarots.clear();
        
        saveRunToDatabase();
        return true;
    }

    public void endSession() {
        this.difficulty = null;
        this.targetPoints = 0;
        this.currentScore = 0;
        this.currentAnte = 0;
        this.ownedJokers.clear();
        this.activeJokers.clear();
        this.ownedTarots.clear();
        this.activeTarots.clear();
        this.currentDeck.clear();
        this.extraHands = 0;
        this.extraDiscards = 0;
        
        this.ownedVouchers.clear();
        this.currentShopVoucher = null;
        this.extraJokerSlots = 0;
        this.shopDiscountActive = false;
        this.extraShopSlots = 0;
        this.rerollDiscountActive = false;
        
        PlayerDatabase.clearRun();
    }

    public void addCoins(int amount) {
        this.coins += amount;
    }

    public void deductCoins(int amount) {
        this.coins = Math.max(0, this.coins - amount);
    }

    public int getCoins() {
        return coins;
    }

    public String getDifficulty() { return difficulty; }
    public double getDifficultyMultiplier() { return difficultyMultiplier; }
    public int getCurrentAnte() { return currentAnte; }
    public int getMaxAntes() { return maxAntes; }
    public int getTargetPoints() { return targetPoints; }
    public int getCurrentScore() { return currentScore; }
    public void setScore(int score) { this.currentScore = score; }
    public List<Joker> getOwnedJokers() { return ownedJokers; }
    public List<Joker> getActiveJokers() { return activeJokers; }
    public List<Tarot> getOwnedTarots() { return ownedTarots; }
    public List<Tarot> getActiveTarots() { return activeTarots; }
    public List<Card> getCurrentDeck() { return currentDeck; }
    public List<Joker> getCurrentShopJokers() { return currentShopJokers; }
    public List<Tarot> getCurrentShopTarots() { return currentShopTarots; }
    public boolean isShopResetUsed() { return shopResetUsed; }
    public void setShopResetUsed(boolean shopResetUsed) { this.shopResetUsed = shopResetUsed; }
    public int getExtraHands() { return extraHands; }
    public void addExtraHands(int amount) { this.extraHands += amount; }
    public int getExtraDiscards() { return extraDiscards; }
    public void addExtraDiscards(int amount) { this.extraDiscards += amount; }

    public List<String> getOwnedVouchers() { return ownedVouchers; }
    public String getCurrentShopVoucher() { return currentShopVoucher; }
    public void setCurrentShopVoucher(String currentShopVoucher) { this.currentShopVoucher = currentShopVoucher; }
    public int getExtraJokerSlots() { return extraJokerSlots; }
    public void setExtraJokerSlots(int extraJokerSlots) { this.extraJokerSlots = extraJokerSlots; }
    public boolean isShopDiscountActive() { return shopDiscountActive; }
    public void setShopDiscountActive(boolean shopDiscountActive) { this.shopDiscountActive = shopDiscountActive; }
    public int getExtraShopSlots() { return extraShopSlots; }
    public void setExtraShopSlots(int extraShopSlots) { this.extraShopSlots = extraShopSlots; }
    public boolean isRerollDiscountActive() { return rerollDiscountActive; }
    public void setRerollDiscountActive(boolean rerollDiscountActive) { this.rerollDiscountActive = rerollDiscountActive; }
}
