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

    @FXML
    private javafx.scene.control.Button playNextRoundButton;

    @FXML
    private javafx.scene.control.Button resetShopButton;

    private List<Joker> availableJokers;
    private Joker selectedJoker = null;
    
    private String currentTab = "jokers";

    private List<Tarot> availableTarots;
    private Tarot selectedTarot = null;

    @FXML
    public void initialize() {
        // Update currency display
        int currency = GameSession.getInstance().getCoins();
        if (currencyLabel != null) {
            currencyLabel.setText("Coins: " + currency);
        }

        GameSession session = GameSession.getInstance();

        if (session.getCurrentShopJokers().isEmpty() || session.getCurrentShopTarots().isEmpty()) {
            availableJokers = new ArrayList<>(JokerRegistry.JOKERS);
            Collections.shuffle(availableJokers);
            availableJokers = new ArrayList<>(availableJokers.subList(0, Math.min(4, availableJokers.size())));

            List<Tarot> allTarots = new ArrayList<>(TarotRegistry.TAROTS);
            Collections.shuffle(allTarots);
            availableTarots = new ArrayList<>(allTarots.subList(0, Math.min(4, allTarots.size())));

            session.getCurrentShopJokers().clear();
            session.getCurrentShopJokers().addAll(availableJokers);
            session.getCurrentShopTarots().clear();
            session.getCurrentShopTarots().addAll(availableTarots);
            session.saveRunToDatabase();
        } else {
            availableJokers = new ArrayList<>(session.getCurrentShopJokers());
            availableTarots = new ArrayList<>(session.getCurrentShopTarots());
        }

        if (shopSectionTitle != null) {
            shopSectionTitle.setText("Welcome to the Shop!");
        }

        if (playNextRoundButton != null) {
            playNextRoundButton.setDisable(!PlayerDatabase.hasActiveRun());
        }

        if (resetShopButton != null) {
            resetShopButton.setDisable(GameSession.getInstance().isShopResetUsed());
        }

        // Show jokers by default
        handleShowJokers(null);
    }

    @FXML
    private void handleShowJokers(ActionEvent event) {
        currentTab = "jokers";
        if (shopSectionTitle != null) shopSectionTitle.setText("Joker Cards");
        selectedJoker = null;
        selectedTarot = null;
        if (buyButton != null) buyButton.setVisible(false);
        if (shopItemsContainer != null) {
            shopItemsContainer.getChildren().clear();

            for (Joker joker : availableJokers) {
                VBox itemBox = new VBox();
                itemBox.setAlignment(javafx.geometry.Pos.CENTER);
                itemBox.setSpacing(10);
                itemBox.getStyleClass().add("card-view");

                boolean owned = false;
                for (Joker oj : GameSession.getInstance().getOwnedJokers()) {
                    if (oj.name().equals(joker.name())) { owned = true; break; }
                }
                if (owned) {
                    itemBox.setOpacity(0.4);
                    itemBox.setDisable(true);
                }

                StackPane imageWrapper = new StackPane();
                imageWrapper.getStyleClass().add("joker-image-wrapper");

            try {
                Image img = new Image(getClass().getResource(joker.imagePath()).toExternalForm());
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
                    buyButton.setText("Buy " + joker.name() + " (200 Coins)");
                }
            });

            Tooltip tooltip = new Tooltip(joker.name() + "\n" + joker.description());
            tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(imageWrapper, tooltip);

            // Cost label
            Label costLabel = new Label("200 Coins");
            costLabel.setStyle("-fx-text-fill: #fca311; -fx-font-size: 18px; -fx-font-weight: bold;");

            itemBox.getChildren().addAll(imageWrapper, costLabel);
            if (shopItemsContainer != null) shopItemsContainer.getChildren().add(itemBox);
            }
        }
    }

    @FXML
    private void handleShowTarots(ActionEvent event) {
        currentTab = "tarots";
        if (shopSectionTitle != null) shopSectionTitle.setText("Tarot Cards");
        selectedJoker = null;
        selectedTarot = null;
        if (buyButton != null) buyButton.setVisible(false);
        if (shopItemsContainer != null) {
            shopItemsContainer.getChildren().clear();

            for (Tarot tarot : availableTarots) {
                VBox itemBox = new VBox(10);
                itemBox.setStyle("-fx-alignment: center; -fx-padding: 10px; -fx-cursor: hand;");
                
                boolean owned = false;
                for (Tarot ot : GameSession.getInstance().getOwnedTarots()) {
                    if (ot.name().equals(tarot.name())) { owned = true; break; }
                }
                if (owned) {
                    itemBox.setOpacity(0.4);
                    itemBox.setDisable(true);
                }

                StackPane imageWrapper = new StackPane();
                imageWrapper.getStyleClass().add("joker-image-wrapper");

                try {
                    Image img = new Image(getClass().getResource(tarot.imagePath()).toExternalForm());
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
                    System.err.println("Could not load tarot image: " + tarot.imagePath());
                    Label errorLabel = new Label(tarot.name());
                    errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
                    imageWrapper.getChildren().add(errorLabel);
                    imageWrapper.setPrefSize(150, 217);
                }

                itemBox.setOnMouseClicked(e -> {
                    for (javafx.scene.Node node : shopItemsContainer.getChildren()) {
                        node.setStyle(""); // Clear selection border
                    }
                    itemBox.setStyle("-fx-border-color: #fca311; -fx-border-width: 4px; -fx-border-radius: 10px; -fx-background-radius: 10px;");
                    selectedTarot = tarot;
                    selectedJoker = null;
                    
                    if (buyButton != null) {
                        buyButton.setVisible(true);
                        buyButton.setText("Buy " + tarot.name() + " (200 Coins)");
                    }
                });

                Tooltip tooltip = new Tooltip(tarot.name() + "\n" + tarot.description());
                tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(imageWrapper, tooltip);

                Label costLabel = new Label("200 Coins");
                costLabel.setStyle("-fx-text-fill: #fca311; -fx-font-size: 18px; -fx-font-weight: bold;");

                itemBox.getChildren().addAll(imageWrapper, costLabel);
                shopItemsContainer.getChildren().add(itemBox);
            }
        }
    }

    @FXML
    private void handleShowVouchers(ActionEvent event) {
        currentTab = "vouchers";
        if (shopSectionTitle != null) shopSectionTitle.setText("Vouchers");
        selectedJoker = null;
        selectedTarot = null;
        if (buyButton != null) buyButton.setVisible(false);
        if (shopItemsContainer != null) {
            shopItemsContainer.getChildren().clear();
            Label emptyLabel = new Label("Vouchers Coming Soon...");
            emptyLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");
            shopItemsContainer.getChildren().add(emptyLabel);
        }
    }

    @FXML
    private void handleResetShop(ActionEvent event) {
        GameSession session = GameSession.getInstance();
        if (session.getCoins() >= 50 && !session.isShopResetUsed()) {
            session.deductCoins(50);
            session.setShopResetUsed(true);
            if (currencyLabel != null) {
                currencyLabel.setText("Coins: " + session.getCoins());
            }
            if (resetShopButton != null) {
                resetShopButton.setDisable(true);
            }

            availableJokers = new ArrayList<>(JokerRegistry.JOKERS);
            Collections.shuffle(availableJokers);
            availableJokers = new ArrayList<>(availableJokers.subList(0, Math.min(4, availableJokers.size())));

            List<Tarot> allTarots = new ArrayList<>(TarotRegistry.TAROTS);
            Collections.shuffle(allTarots);
            availableTarots = new ArrayList<>(allTarots.subList(0, Math.min(4, allTarots.size())));

            session.getCurrentShopJokers().clear();
            session.getCurrentShopJokers().addAll(availableJokers);
            session.getCurrentShopTarots().clear();
            session.getCurrentShopTarots().addAll(availableTarots);

            session.saveRunToDatabase();

            if (currentTab.equals("jokers")) handleShowJokers(null);
            else if (currentTab.equals("tarots")) handleShowTarots(null);
            else handleShowVouchers(null);
        } else if (session.getCoins() < 50 && !session.isShopResetUsed()) {
            if (resetShopButton != null) {
                resetShopButton.setText("Not enough Coins!");
            }
        }
    }

    @FXML
    private void handleBuyItem(ActionEvent event) {
        if (selectedJoker != null || selectedTarot != null) {
            GameSession session = GameSession.getInstance();
            int currentCoins = session.getCoins();
            if (currentCoins >= 200) {
                // Deduct coins
                session.deductCoins(200);
                if (currencyLabel != null) {
                    currencyLabel.setText("Coins: " + session.getCoins());
                }

                if (selectedJoker != null) {
                    session.getOwnedJokers().add(selectedJoker);
                    if (session.getActiveJokers().size() < 3) {
                        session.getActiveJokers().add(selectedJoker);
                    }
                    selectedJoker = null;
                    if (buyButton != null) buyButton.setVisible(false);
                    handleShowJokers(null);
                } else if (selectedTarot != null) {
                    session.getOwnedTarots().add(selectedTarot);
                    if (session.getActiveTarots().size() < 5) {
                        session.getActiveTarots().add(selectedTarot);
                    }
                    selectedTarot = null;
                    if (buyButton != null) buyButton.setVisible(false);
                    handleShowTarots(null);
                }

                // Save updated run state
                session.saveRunToDatabase();
            } else {
                if (buyButton != null) {
                    buyButton.setText("Not enough Coins!");
                }
            }
        }
    }

    @FXML
    private void handleReturnHome(ActionEvent event) {
        // Don't end the session — the run persists in the database
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handlePlayNextRound(ActionEvent event) {
        GameSession session = GameSession.getInstance();
        if (session.getDifficulty() == null) {
            session.loadFromDatabase();
        }
        if (session.advanceAnte()) {
            try {
                App.setRoot("game");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            session.endSession();
            try {
                App.setRoot("home");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
