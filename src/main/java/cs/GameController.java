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
    private Label finalScoreLabel;

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
            gameInfoLabel.setText(String.format("Difficulty: %s  |  Target: %,d Pts", 
                session.getDifficulty(), targetScore));
        }

        // Deal 10 random cards
        remainingDeck = new ArrayList<>(CardData.CARDS);
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

        renderCards(currentHand);
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
                Image img = new Image(getClass().getResourceAsStream("/cs/" + card.imagePath()));
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
    private void handlePlayHand(ActionEvent event) {
        if (selectedCards.isEmpty() || handsLeft <= 0) {
            return;
        }

        HandResult bestHand = HandEvaluator.evaluateSelectedCards(selectedCards);
        if (bestHand != null) {
            int baseChips = 0;
            for (Card c : bestHand.cardsUsed()) {
                baseChips += c.points();
            }
            int pointsEarned = baseChips * bestHand.mult();
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
                if (finalScoreLabel != null) {
                    finalScoreLabel.setText("Total Score Achieved: " + currentScore);
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
            
            baseChipsLabel.setText(String.valueOf(baseChips));
            multiplierLabel.setText(String.valueOf(bestHand.mult()));
        } else {
            handInfoBox.setVisible(false);
        }
    }
}
