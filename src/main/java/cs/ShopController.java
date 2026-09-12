package cs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class ShopController {

    @FXML
    private Label currencyLabel;

    @FXML
    private Label shopSectionTitle;

    @FXML
    private HBox shopItemsContainer;

    @FXML
    private javafx.scene.control.Button buyButton;

    private List<Joker> availableJokers;
    private Joker selectedJoker = null;

    @FXML
    public void initialize() {
        // Update currency display
        int currency = PlayerDatabase.getCurrency();
        if (currencyLabel != null) {
            currencyLabel.setText("Coins: " + currency);
        }

        // Initialize jokers available in the shop
        availableJokers = new ArrayList<>();
        availableJokers.add(new Joker("Basic Joker", "Adds +20 Chips\nCost: 1000 Coins", "/cs/images/joker/joker_1.jpg", Joker.JokerEffect.ADD_CHIPS, 20));
        availableJokers.add(new Joker("Multi Joker", "Adds +4 Mult\nCost: 1000 Coins", "/cs/images/joker/joker_2.jpg", Joker.JokerEffect.ADD_MULTI, 4));
        availableJokers.add(new Joker("Foil Joker", "Multiplies Mult by 2\nCost: 1000 Coins", "/cs/images/joker/joker_3.jpg", Joker.JokerEffect.MULT_MULTI, 2));

        if (shopSectionTitle != null) {
            shopSectionTitle.setText("Welcome to the Shop!");
        }

        // Show jokers by default
        handleShowJokers(null);
    }

    @FXML
    private void handleShowJokers(ActionEvent event) {
        if (shopSectionTitle != null) shopSectionTitle.setText("Joker Cards");
        if (shopItemsContainer != null) shopItemsContainer.getChildren().clear();

        for (Joker joker : availableJokers) {
            VBox itemBox = new VBox();
            itemBox.setAlignment(javafx.geometry.Pos.CENTER);
            itemBox.setSpacing(10);
            itemBox.getStyleClass().add("card-view");

            StackPane imageWrapper = new StackPane();
            imageWrapper.getStyleClass().add("joker-image-wrapper");

            try {
                Image img = new Image(getClass().getResourceAsStream(joker.imagePath()));
                ImageView imgView = new ImageView(img);
                imgView.setFitWidth(150);
                imgView.setFitHeight(217);
                imgView.setPreserveRatio(false);

                Rectangle clip = new Rectangle(150, 217);
                clip.setArcWidth(15);
                clip.setArcHeight(15);
                imgView.setClip(clip);

                imageWrapper.getChildren().add(imgView);
            } catch (Exception e) {
                System.err.println("Could not load joker image: " + joker.imagePath());
                Label errorLabel = new Label(joker.name());
                errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
                imageWrapper.getChildren().add(errorLabel);
                imageWrapper.setPrefSize(150, 217);
            }

            // Click to select
            itemBox.setOnMouseClicked(e -> {
                for (javafx.scene.Node node : shopItemsContainer.getChildren()) {
                    node.setStyle(""); // Clear selection border
                }
                itemBox.setStyle("-fx-border-color: #fca311; -fx-border-width: 4px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
                selectedJoker = joker;
                
                if (buyButton != null) {
                    buyButton.setVisible(true);
                    buyButton.setText("Buy " + joker.name() + " (1000 Coins)");
                }
            });

            Tooltip tooltip = new Tooltip(joker.name() + "\n" + joker.description());
            tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(imageWrapper, tooltip);

            // Cost label
            Label costLabel = new Label("1000 Coins");
            costLabel.setStyle("-fx-text-fill: #fca311; -fx-font-size: 18px; -fx-font-weight: bold;");

            itemBox.getChildren().addAll(imageWrapper, costLabel);
            if (shopItemsContainer != null) shopItemsContainer.getChildren().add(itemBox);
        }
    }

    @FXML
    private void handleShowTarots(ActionEvent event) {
        if (shopSectionTitle != null) shopSectionTitle.setText("Tarot Cards");
        if (shopItemsContainer != null) {
            shopItemsContainer.getChildren().clear();

            Label placeholder = new Label("Tarot cards coming soon...");
            placeholder.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5); -fx-font-size: 24px;");
            shopItemsContainer.getChildren().add(placeholder);
        }
    }

    @FXML
    private void handleNextRound(ActionEvent event) {
        // For now, next round just goes to difficulty selection
        try {
            App.setRoot("difficulty");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBuyItem(ActionEvent event) {
        if (selectedJoker != null) {
            int currentCoins = PlayerDatabase.getCurrency();
            if (currentCoins >= 1000) {
                // Deduct coins
                PlayerDatabase.deductCurrency(1000);
                if (currencyLabel != null) {
                    currencyLabel.setText("Coins: " + PlayerDatabase.getCurrency());
                }

                // Add joker to active loadout
                GameSession.getInstance().getActiveJokers().add(selectedJoker);

                // Remove from shop
                availableJokers.remove(selectedJoker);
                selectedJoker = null;
                
                // Hide buy button and refresh
                if (buyButton != null) {
                    buyButton.setVisible(false);
                }
                handleShowJokers(null);
            } else {
                if (buyButton != null) {
                    buyButton.setText("Not enough Coins!");
                }
            }
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
