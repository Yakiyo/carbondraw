package cs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * Controller for the main Game page view (game.fxml).
 */
public class GameController {

    private List<Card> selectedCards = new ArrayList<>();

    @FXML
    private Label gameInfoLabel;

    @FXML
    private javafx.scene.layout.HBox jokersContainer;

    @FXML
    private javafx.scene.layout.HBox tarotsContainer;

    @FXML
    private javafx.scene.control.Button useTarotButton;
    
    private Tarot selectedTarot = null;

    @FXML
    private javafx.scene.layout.AnchorPane handsPopup;

    @FXML
    private Pane cardHandContainer;

    @FXML
    private VBox handInfoBox;

    @FXML
    private Label handNameLabel;

    @FXML
    private Label baseChipsLabel;

    @FXML
    private Label multiplierLabel;

    @FXML
    private Label discardsLabel;

    @FXML
    private javafx.scene.control.Button discardButton;

    private List<Card> remainingDeck = new ArrayList<>();
    private List<Card> currentHand = new ArrayList<>();
    private int discardsLeft = 3;

    @FXML
    private Label scoreLabel;

    @FXML
    private Label handsLabel;
    
    @FXML
    private javafx.scene.layout.AnchorPane gameOverPopup;
    
    @FXML
    private Label gameOverTitleLabel;

    @FXML
    private Label finalScoreLabel;

    @FXML
    private javafx.scene.control.Button endGameButton;

    @FXML
    private javafx.scene.layout.HBox winButtonsBox;

    private int currentScore = 0;
    private int handsLeft = 4;
    private int targetScore = 0;

    @FXML
    private void handleShowHands(ActionEvent event) {
        if (handsPopup != null) {
            handsPopup.setVisible(true);
        }
    }

    @FXML
    private void handleCloseHands(ActionEvent event) {
        if (handsPopup != null) {
            handsPopup.setVisible(false);
        }
    }

    @FXML
    public void initialize() {
        GameSession session = GameSession.getInstance();
        if (session.getDifficulty() != null) {
            targetScore = session.getTargetPoints();
            gameInfoLabel.setText(String.format("Ante %d/%d  |  %s  |  Target: %,d Pts", 
                session.getCurrentAnte(), session.getMaxAntes(),
                session.getDifficulty(), targetScore));
        }

        // Deal 10 random cards from the persistent deck
        remainingDeck = new ArrayList<>(session.getCurrentDeck());
        Collections.shuffle(remainingDeck);
        currentHand = new ArrayList<>();
        for (int i = 0; i < 10 && !remainingDeck.isEmpty(); i++) {
            currentHand.add(remainingDeck.remove(0));
        }

        discardsLeft = 3;
        currentScore = 0;
        handsLeft = 4;
        
        if (discardsLabel != null) discardsLabel.setText(String.valueOf(discardsLeft));
        if (discardButton != null) discardButton.setDisable(false);
        if (scoreLabel != null) scoreLabel.setText("0");
        if (handsLabel != null) handsLabel.setText(String.valueOf(handsLeft));
        if (gameOverPopup != null) gameOverPopup.setVisible(false);

        renderJokers(session.getActiveJokers());
        renderTarots(session.getOwnedTarots());
        renderCards(currentHand);
    }

    private void renderJokers(List<Joker> jokers) {
        if (jokersContainer == null) return;
        jokersContainer.getChildren().clear();

        for (Joker joker : jokers) {
            StackPane imageWrapper = new StackPane();
            imageWrapper.getStyleClass().add("joker-image-wrapper");

            try {
                Image img = new Image(getClass().getResource(joker.imagePath()).toExternalForm());
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(100);
                imgView.setFitHeight(145);
                imgView.setPreserveRatio(false);

                Rectangle clip = new Rectangle(100, 145);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                imgView.setClip(clip);

                imageWrapper.getChildren().add(imgView);
            } catch (Exception e) {
                System.err.println("Could not load joker image: " + joker.imagePath());
                Label errorLabel = new Label(joker.name());
                errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
                imageWrapper.getChildren().add(errorLabel);
                imageWrapper.setPrefSize(100, 145);
            }

            Tooltip tooltip = new Tooltip(joker.name() + "\n" + joker.description());
            tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(imageWrapper, tooltip);

            jokersContainer.getChildren().add(imageWrapper);
        }
    }

    private void renderTarots(List<Tarot> tarots) {
        if (tarotsContainer == null) return;
        tarotsContainer.getChildren().clear();

        for (Tarot tarot : tarots) {
            StackPane imageWrapper = new StackPane();
            imageWrapper.getStyleClass().add("joker-image-wrapper"); // same styling as joker

            try {
                Image img = new Image(getClass().getResource(tarot.imagePath()).toExternalForm());
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(100);
                imgView.setFitHeight(145);
                imgView.setPreserveRatio(false);

                Rectangle clip = new Rectangle(100, 145);
                clip.setArcWidth(10);
                clip.setArcHeight(10);
                imgView.setClip(clip);

                imageWrapper.getChildren().add(imgView);
            } catch (Exception e) {
                System.err.println("Could not load tarot image: " + tarot.imagePath());
                Label errorLabel = new Label(tarot.name());
                errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
                imageWrapper.getChildren().add(errorLabel);
                imageWrapper.setPrefSize(100, 145);
            }

            Tooltip tooltip = new Tooltip(tarot.name() + "\n" + tarot.description());
            tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(imageWrapper, tooltip);
            
            imageWrapper.setOnMouseClicked(e -> {
                for (javafx.scene.Node node : tarotsContainer.getChildren()) {
                    node.setStyle(""); // Clear selection
                }
                if (selectedTarot == tarot) {
                    selectedTarot = null; // Deselect
                    if (useTarotButton != null) useTarotButton.setVisible(false);
                } else {
                    imageWrapper.setStyle("-fx-border-color: #fca311; -fx-border-width: 3px; -fx-border-radius: 10px;");
                    selectedTarot = tarot;
                    updateTarotButtonState();
                }
            });

            tarotsContainer.getChildren().add(imageWrapper);
        }
    }

    private void renderCards(List<Card> cards) {
        if (cardHandContainer == null) return;
        
        cardHandContainer.getChildren().clear();
        selectedCards.clear();
        
        int n = cards.size();
        if (n == 0) return;
        
        double centerIndex = (n - 1) / 2.0;
        double cardWidth = 180;
        double cardHeight = 260;
        double spacingX = cardWidth * 0.75; // 3/4 card width for 1/4 overlap
        double paneWidth = 1400;
        
        for (int i = 0; i < n; i++) {
            Card card = cards.get(i);
            VBox cardView = new VBox();
            cardView.getStyleClass().add("card-view");
            
            StackPane imageWrapper = new StackPane();
            imageWrapper.getStyleClass().add("card-image-wrapper");

            try {
                Image img = new Image(getClass().getResource("/cs/" + card.imagePath()).toExternalForm());
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(cardWidth);
                imgView.setFitHeight(cardHeight);
                imgView.setPreserveRatio(false); 
                
                Rectangle clip = new Rectangle(cardWidth, cardHeight);
                clip.setArcWidth(15);
                clip.setArcHeight(15);
                imgView.setClip(clip);

                imageWrapper.getChildren().add(imgView);
            } catch (Exception e) {
                System.err.println("Could not load image: " + card.imagePath());
                Label errorLabel = new Label(card.name());
                errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 20px;");
                imageWrapper.getChildren().add(errorLabel);
                imageWrapper.setPrefSize(cardWidth, cardHeight);
            }

            cardView.getChildren().add(imageWrapper);
            
            // Fanning math removed, arrange straight with overlap
            double offset = i - centerIndex;
            
            double baseX = (paneWidth - cardWidth) / 2.0;
            double x = baseX + (offset * spacingX);
            double y = 30.0; // Straight line
            
            cardView.setLayoutX(x);
            cardView.setLayoutY(y);
            
            // Add a tooltip to show the points
            Tooltip tooltip = new Tooltip("Points: " + card.points());
            tooltip.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
            tooltip.setShowDelay(Duration.millis(100)); // Show almost instantly
            // Install on imageWrapper instead of cardView for better hit detection
            Tooltip.install(imageWrapper, tooltip);

            // Selection Logic
            imageWrapper.setOnMouseClicked(e -> {
                if (selectedCards.contains(card)) {
                    selectedCards.remove(card);
                    imageWrapper.setTranslateY(0);
                } else {
                    if (selectedCards.size() < 5) {
                        selectedCards.add(card);
                        imageWrapper.setTranslateY(-15);
                    }
                }
                updateHandInfoDisplay();
                updateTarotButtonState();
            });

            cardHandContainer.getChildren().add(cardView);
        }
    }

    @FXML
    private void handleReturnHome(ActionEvent event) {
        GameSession.getInstance().endSession();
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleGoToShop(ActionEvent event) {
        try {
            App.setRoot("shop");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleNextRoundAction(ActionEvent event) {
        GameSession session = GameSession.getInstance();
        boolean hasMore = session.advanceAnte();
        if (hasMore) {
            try {
                App.setRoot("game");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // All 10 antes beaten — run complete!
            session.endSession();
            try {
                App.setRoot("home");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int[] calculateScoreWithJokers(HandResult bestHand, int discardsLeft, int baseChips, int baseMult) {
        int finalChips = baseChips;
        int finalMult = baseMult;

        long renewableCount = bestHand == null ? 0 : bestHand.cardsUsed().stream().filter(c -> c.getOriginalSuit() != null && c.getOriginalSuit().contains("renewable")).count();
        long biosphereCount = bestHand == null ? 0 : bestHand.cardsUsed().stream().filter(c -> c.getOriginalSuit() != null && c.getOriginalSuit().contains("biosphere")).count();
        long greenTechCount = bestHand == null ? 0 : bestHand.cardsUsed().stream().filter(c -> c.getOriginalSuit() != null && c.getOriginalSuit().contains("green_tech")).count();
        long policyCount = bestHand == null ? 0 : bestHand.cardsUsed().stream().filter(c -> c.getOriginalSuit() != null && c.getOriginalSuit().contains("policy")).count();
        
        String handName = bestHand == null ? "" : bestHand.handName();

        for (Joker joker : GameSession.getInstance().getActiveJokers()) {
            switch (joker.effectType()) {
                case ADD_CHIPS -> finalChips += joker.effectValue();
                case ADD_MULTI -> finalMult += joker.effectValue();
                case MULT_MULTI -> finalMult *= joker.effectValue();
                case ADD_MULT_PER_RENEWABLE -> finalMult += (joker.effectValue() * (int)renewableCount);
                case ADD_MULT_PER_BIOSPHERE -> finalMult += (joker.effectValue() * (int)biosphereCount);
                case ADD_MULT_PER_GREENTECH -> finalMult += (joker.effectValue() * (int)greenTechCount);
                case ADD_MULT_PER_POLICY -> finalMult += (joker.effectValue() * (int)policyCount);
                case ADD_MULT_IF_PAIR -> { if (handName.contains("Grassroots Action") || handName.contains("Bilateral Agreement") || handName.contains("Symbiotic Loop")) finalMult += joker.effectValue(); }
                case ADD_MULT_IF_THREE_KIND -> { if (handName.contains("Sector Focus") || handName.contains("Symbiotic Loop")) finalMult += joker.effectValue(); }
                case ADD_MULT_IF_FOUR_KIND -> { if (handName.contains("Industry Overhaul")) finalMult += joker.effectValue(); }
                case ADD_MULT_IF_STRAIGHT -> { if (handName.contains("Holistic Strategy") || handName.contains("Net Zero Earthshot")) finalMult += joker.effectValue(); }
                case ADD_MULT_IF_FLUSH -> { if (handName.contains("Monoculture") || handName.contains("Net Zero Earthshot")) finalMult += joker.effectValue(); }
                case ADD_CHIPS_IF_PAIR -> { if (handName.contains("Grassroots Action") || handName.contains("Bilateral Agreement") || handName.contains("Symbiotic Loop")) finalChips += joker.effectValue(); }
                case ADD_CHIPS_IF_TWO_PAIR -> { if (handName.contains("Bilateral Agreement")) finalChips += joker.effectValue(); }
                case ADD_CHIPS_IF_THREE_KIND -> { if (handName.contains("Sector Focus") || handName.contains("Symbiotic Loop")) finalChips += joker.effectValue(); }
                case ADD_CHIPS_IF_STRAIGHT -> { if (handName.contains("Holistic Strategy") || handName.contains("Net Zero Earthshot")) finalChips += joker.effectValue(); }
                case ADD_CHIPS_IF_FLUSH -> { if (handName.contains("Monoculture") || handName.contains("Net Zero Earthshot")) finalChips += joker.effectValue(); }
                case ADD_MULT_IF_SMALL_HAND -> { if (bestHand != null && bestHand.cardsUsed().size() <= 3) finalMult += joker.effectValue(); }
                case ADD_CHIPS_PER_DISCARD -> finalChips += (joker.effectValue() * discardsLeft);
                case ADD_MULT_IF_NO_DISCARDS -> { if (discardsLeft == 0) finalMult += joker.effectValue(); }
            }
        }
        return new int[]{finalChips, finalMult};
    }

    @FXML
    private void handlePlayHand(ActionEvent event) {
        if (selectedCards.isEmpty() || handsLeft <= 0) {
            return;
        }

        HandResult bestHand = HandEvaluator.evaluateSelectedCards(selectedCards);
        if (bestHand != null) {
            int baseChips = 0;
            int mult = bestHand.mult();
            List<Card> cardsToDestroy = new ArrayList<>();

            for (Card c : bestHand.cardsUsed()) {
                baseChips += c.points();
                if (c.enhancement() == Card.Enhancement.MULT) {
                    mult += 4;
                } else if (c.enhancement() == Card.Enhancement.BONUS) {
                    baseChips += 30;
                } else if (c.enhancement() == Card.Enhancement.GLASS) {
                    mult *= 2;
                    if (Math.random() < 0.25) { // 1 in 4 chance to break
                        cardsToDestroy.add(c);
                    }
                }
                // LUCKY could be handled here too: 1 in 5 chance +20 mult, 1 in 15 chance $20
                if (c.enhancement() == Card.Enhancement.LUCKY) {
                    if (Math.random() < 0.2) mult += 20;
                    if (Math.random() < 0.066) GameSession.getInstance().addCoins(20);
                }
            }

            for (Card held : currentHand) {
                if (!selectedCards.contains(held) && held.enhancement() == Card.Enhancement.STEEL) {
                    mult = (int) Math.round(mult * 1.5);
                }
            }

            int[] calculated = calculateScoreWithJokers(bestHand, discardsLeft, baseChips, mult);
            int pointsEarned = calculated[0] * calculated[1];
            currentScore += pointsEarned;
            
            if (scoreLabel != null) {
                scoreLabel.setText(String.valueOf(currentScore));
            }

            // Replace played cards with new cards from remaining deck
            for (Card playedCard : selectedCards) {
                int index = currentHand.indexOf(playedCard);
                if (index != -1 && !remainingDeck.isEmpty()) {
                    currentHand.set(index, remainingDeck.remove(0));
                } else if (index != -1) {
                    currentHand.remove(index);
                }

                // If glass broke, remove it permanently from the session deck
                if (cardsToDestroy.contains(playedCard)) {
                    GameSession.getInstance().getCurrentDeck().remove(playedCard);
                }
            }

            selectedCards.clear();
            handsLeft--;
            
            if (handsLabel != null) {
                handsLabel.setText(String.valueOf(handsLeft));
            }

            renderCards(currentHand);
            updateHandInfoDisplay();
            
            if (currentScore >= targetScore) {
                if (gameOverPopup != null) {
                    gameOverPopup.setVisible(true);
                }
                
                if (gameOverTitleLabel != null) {
                    gameOverTitleLabel.setText("Victory!");
                }
                
                if (winButtonsBox != null) {
                    winButtonsBox.setVisible(true);
                    winButtonsBox.setManaged(true);
                }
                if (endGameButton != null) {
                    endGameButton.setVisible(false);
                    endGameButton.setManaged(false);
                }

                int excess = currentScore - targetScore;
                if (excess > 0) {
                    GameSession.getInstance().addCoins(excess);
                    if (finalScoreLabel != null) {
                        finalScoreLabel.setText("Total Score Achieved: " + currentScore + "\nCoins Earned: " + excess);
                    }
                } else {
                    if (finalScoreLabel != null) {
                        finalScoreLabel.setText("Total Score Achieved: " + currentScore);
                    }
                }
            } else if (handsLeft <= 0) {
                if (gameOverPopup != null) {
                    gameOverPopup.setVisible(true);
                }
                
                if (gameOverTitleLabel != null) {
                    gameOverTitleLabel.setText("Defeated");
                }
                
                if (winButtonsBox != null) {
                    winButtonsBox.setVisible(false);
                    winButtonsBox.setManaged(false);
                }
                if (endGameButton != null) {
                    endGameButton.setVisible(true);
                    endGameButton.setManaged(true);
                }
                
                if (finalScoreLabel != null) {
                    finalScoreLabel.setText("Total Score Achieved: " + currentScore + " / " + targetScore);
                }
            }
        } else {
            System.out.println("No valid hand found for selection!");
        }
    }

    @FXML
    private void handleDiscard(ActionEvent event) {
        if (selectedCards.isEmpty() || discardsLeft <= 0) {
            return;
        }

        System.out.println("Discard clicked! Selected cards: " + selectedCards.size());

        // Put them back to the remaining deck
        remainingDeck.addAll(selectedCards);
        Collections.shuffle(remainingDeck); // Shuffle so it's a random deck again

        // Replace each selected card in its original position
        for (Card discardedCard : selectedCards) {
            int index = currentHand.indexOf(discardedCard);
            if (index != -1 && !remainingDeck.isEmpty()) {
                currentHand.set(index, remainingDeck.remove(0));
            } else if (index != -1) {
                currentHand.remove(index);
            }
        }

        // Clear selection
        selectedCards.clear();

        // Update discards counter
        discardsLeft--;
        if (discardsLabel != null) {
            discardsLabel.setText(String.valueOf(discardsLeft));
        }

        if (discardsLeft <= 0 && discardButton != null) {
            discardButton.setDisable(true);
        }

        // Update UI
        renderCards(currentHand);
        updateHandInfoDisplay();
    }

    private void updateTarotButtonState() {
        if (useTarotButton == null) return;
        
        if (selectedTarot == null) {
            useTarotButton.setVisible(false);
            return;
        }

        // Check if the condition for using the Tarot is met
        int requiredTargets = selectedTarot.targetCount();
        if (requiredTargets > 0) {
            if (selectedCards.size() == requiredTargets) {
                useTarotButton.setVisible(true);
            } else {
                useTarotButton.setVisible(false);
            }
        } else {
            useTarotButton.setVisible(true);
        }
    }

    @FXML
    private void handleUseTarot(ActionEvent event) {
        if (selectedTarot == null) return;

        GameSession session = GameSession.getInstance();
        boolean used = false;

        switch (selectedTarot.effectType()) {
            case GAIN_MONEY:
                int currentCoins = session.getCoins();
                int gain = Math.min(currentCoins, 20);
                if (gain == 0 && currentCoins == 0) gain = 5; // Give at least 5 if broke
                session.addCoins(gain);
                used = true;
                break;

            case ENHANCE_CHIPS:
                for (Card c : selectedCards) c.setEnhancement(Card.Enhancement.BONUS);
                used = true;
                break;

            case ENHANCE_MULTI:
                for (Card c : selectedCards) c.setEnhancement(Card.Enhancement.MULT);
                used = true;
                break;

            case ENHANCE_LUCKY:
                for (Card c : selectedCards) c.setEnhancement(Card.Enhancement.LUCKY);
                used = true;
                break;

            case ENHANCE_WILD:
                for (Card c : selectedCards) c.setEnhancement(Card.Enhancement.WILD);
                used = true;
                break;

            case ENHANCE_STEEL:
                for (Card c : selectedCards) c.setEnhancement(Card.Enhancement.STEEL);
                used = true;
                break;

            case ENHANCE_GLASS:
                for (Card c : selectedCards) c.setEnhancement(Card.Enhancement.GLASS);
                used = true;
                break;

            case DESTROY_CARD:
                for (Card c : selectedCards) {
                    currentHand.remove(c);
                    session.getCurrentDeck().remove(c);
                }
                used = true;
                break;

            case SPAWN_JOKER:
                if (session.getActiveJokers().size() < 5) {
                    List<Joker> jokers = new ArrayList<>(JokerRegistry.JOKERS);
                    Collections.shuffle(jokers);
                    session.getActiveJokers().add(jokers.get(0));
                    session.getOwnedJokers().add(jokers.get(0));
                    used = true;
                }
                break;

            case SPAWN_TAROT:
                if (session.getOwnedTarots().size() < 2) {
                    List<Tarot> tarots = new ArrayList<>(TarotRegistry.TAROTS);
                    Collections.shuffle(tarots);
                    session.getOwnedTarots().add(tarots.get(0));
                    used = true;
                }
                break;
        }

        if (used) {
            session.getOwnedTarots().remove(selectedTarot);
            selectedTarot = null;
            selectedCards.clear();
            session.saveRunToDatabase();
            
            // Re-render everything
            renderTarots(session.getOwnedTarots());
            renderJokers(session.getActiveJokers());
            renderCards(currentHand);
            updateTarotButtonState();
        }
    }

    private void updateHandInfoDisplay() {
        if (selectedCards.isEmpty()) {
            handInfoBox.setVisible(false);
            return;
        }

        HandResult bestHand = HandEvaluator.evaluateSelectedCards(selectedCards);
        if (bestHand != null) {
            handInfoBox.setVisible(true);
            handNameLabel.setText(bestHand.handName() + " lvl.1");
            
            int baseChips = 0;
            for (Card c : bestHand.cardsUsed()) {
                baseChips += c.points();
            }
            
            int[] calculated = calculateScoreWithJokers(bestHand, discardsLeft, baseChips, bestHand.mult());
            
            baseChipsLabel.setText(String.valueOf(calculated[0]));
            multiplierLabel.setText(String.valueOf(calculated[1]));
        } else {
            handInfoBox.setVisible(false);
        }
    }
}
