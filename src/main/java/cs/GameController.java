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

    @FXML
    private Label gameInfoLabel;

    @FXML
    private javafx.scene.layout.AnchorPane handsPopup;

    @FXML
    private Pane cardHandContainer;

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
            gameInfoLabel.setText(String.format("Difficulty: %s  |  Target: %,d Pts", 
                session.getDifficulty(), session.getTargetPoints()));
        }

        // Deal 10 random cards
        List<Card> deck = new ArrayList<>(CardData.CARDS);
        Collections.shuffle(deck);
        List<Card> initialHand = deck.subList(0, Math.min(10, deck.size()));

        renderCards(initialHand);
    }

    private void renderCards(List<Card> cards) {
        if (cardHandContainer == null) return;
        
        cardHandContainer.getChildren().clear();
        
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
}
