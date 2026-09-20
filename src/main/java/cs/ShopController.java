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
    private Voucher selectedVoucher = null;

    @FXML
    public void initialize() {
        // Update currency display
        int currency = GameSession.getInstance().getCoins();
        if (currencyLabel != null) {
            currencyLabel.setText("Coins: " + currency);
        }

        GameSession session = GameSession.getInstance();

        if (session.getCurrentShopJokers().isEmpty() || session.getCurrentShopTarots().isEmpty()) {
            int shopSlots = 4 + session.getExtraShopSlots();
            
            availableJokers = new ArrayList<>(JokerRegistry.JOKERS);
            Collections.shuffle(availableJokers);
            availableJokers = new ArrayList<>(availableJokers.subList(0, Math.min(shopSlots, availableJokers.size())));

            List<Tarot> allTarots = new ArrayList<>(TarotRegistry.TAROTS);
            Collections.shuffle(allTarots);
            availableTarots = new ArrayList<>(allTarots.subList(0, Math.min(shopSlots, allTarots.size())));

            // Pick a random unowned voucher
            List<Voucher> possibleVouchers = new ArrayList<>();
            for (Voucher v : VoucherRegistry.VOUCHERS) {
                if (!session.getOwnedVouchers().contains(v.name())) {
                    possibleVouchers.add(v);
                }
            }
            if (!possibleVouchers.isEmpty()) {
                Collections.shuffle(possibleVouchers);
                session.setCurrentShopVoucher(possibleVouchers.get(0).name());
            } else {
                session.setCurrentShopVoucher(null);
            }

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
        selectedVoucher = null;
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

            int itemCost = GameSession.getInstance().isShopDiscountActive() ? 150 : 200;

            // Click to select
            itemBox.setOnMouseClicked(e -> {
                for (javafx.scene.Node node : shopItemsContainer.getChildren()) {
                    node.setScaleX(1.0);
                    node.setScaleY(1.0);
                }
                itemBox.setScaleX(1.1);
                itemBox.setScaleY(1.1);
                selectedJoker = joker;
                
                if (buyButton != null) {
                    buyButton.setVisible(true);
                    buyButton.setText("Buy " + joker.name() + " (" + itemCost + " Coins)");
                }
            });

            Tooltip tooltip = new Tooltip(joker.name() + "\n" + joker.description());
            tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            tooltip.setShowDelay(Duration.millis(100));
            Tooltip.install(imageWrapper, tooltip);

            // Cost label
            Label costLabel = new Label(itemCost + " Coins");
            costLabel.setStyle("-fx-text-fill: #fca311; -fx-font-size: 18px; -fx-font-weight: bold;");

            itemBox.getChildren().addAll(imageWrapper, costLabel);
            if (shopItemsContainer != null) shopItemsContainer.getChildren().add(itemBox);
            }
        }
    }

    @FXML
    private void handleShowTarots(ActionEvent event) {
        currentTab = "tarots";
        if (shopSectionTitle != null) shopSectionTitle.setText("Breakthroughs");
        selectedJoker = null;
        selectedTarot = null;
        selectedVoucher = null;
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

                int itemCost = GameSession.getInstance().isShopDiscountActive() ? 150 : 200;

                itemBox.setOnMouseClicked(e -> {
                    for (javafx.scene.Node node : shopItemsContainer.getChildren()) {
                        node.setScaleX(1.0);
                        node.setScaleY(1.0);
                    }
                    itemBox.setScaleX(1.1);
                    itemBox.setScaleY(1.1);
                    selectedTarot = tarot;
                    selectedJoker = null;
                    
                    if (buyButton != null) {
                        buyButton.setVisible(true);
                        buyButton.setText("Buy " + tarot.name() + " (" + itemCost + " Coins)");
                    }
                });

                Tooltip tooltip = new Tooltip(tarot.name() + "\n" + tarot.description());
                tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                tooltip.setShowDelay(Duration.millis(100));
                Tooltip.install(imageWrapper, tooltip);

                Label costLabel = new Label(itemCost + " Coins");
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
        selectedVoucher = null;
        if (buyButton != null) buyButton.setVisible(false);
        if (shopItemsContainer != null) {
            shopItemsContainer.getChildren().clear();
            GameSession session = GameSession.getInstance();
            
            if (session.getCurrentShopVoucher() != null) {
                Voucher voucher = VoucherRegistry.getByName(session.getCurrentShopVoucher());
                if (voucher != null) {
                    VBox itemBox = new VBox(10);
                    itemBox.setStyle("-fx-alignment: center; -fx-padding: 10px; -fx-cursor: hand;");

                    StackPane imageWrapper = new StackPane();
                    imageWrapper.getStyleClass().add("joker-image-wrapper");

                    try {
                        Image img = new Image(getClass().getResource(voucher.imagePath()).toExternalForm());
                        ImageView imgView = new ImageView(img);
                        imgView.setFitWidth(200);
                        imgView.setFitHeight(200); // Vouchers are generally square or different aspect ratio
                        imgView.setPreserveRatio(true);

                        Rectangle clip = new Rectangle(200, 200);
                        clip.setArcWidth(15);
                        clip.setArcHeight(15);
                        imgView.setClip(clip);

                        imageWrapper.getChildren().add(imgView);
                    } catch (Exception e) {
                        System.err.println("Could not load voucher image: " + voucher.imagePath());
                        Label errorLabel = new Label(voucher.name());
                        errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
                        imageWrapper.getChildren().add(errorLabel);
                        imageWrapper.setPrefSize(200, 200);
                    }

                    itemBox.setOnMouseClicked(e -> {
                        for (javafx.scene.Node node : shopItemsContainer.getChildren()) {
                            node.setScaleX(1.0);
                            node.setScaleY(1.0);
                        }
                        itemBox.setScaleX(1.1);
                        itemBox.setScaleY(1.1);
                        selectedVoucher = voucher;
                        selectedJoker = null;
                        selectedTarot = null;
                        
                        if (buyButton != null) {
                            buyButton.setVisible(true);
                            buyButton.setText("Buy " + voucher.name() + " (800 Coins)");
                        }
                    });

                    Tooltip tooltip = new Tooltip(voucher.name() + "\n" + voucher.description());
                    tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                    tooltip.setShowDelay(Duration.millis(100));
                    Tooltip.install(imageWrapper, tooltip);

                    Label costLabel = new Label("800 Coins");
                    costLabel.setStyle("-fx-text-fill: #fca311; -fx-font-size: 24px; -fx-font-weight: bold;");

                    itemBox.getChildren().addAll(imageWrapper, costLabel);
                    shopItemsContainer.getChildren().add(itemBox);
                }
            } else {
                Label emptyLabel = new Label("Sold Out!");
                emptyLabel.setStyle("-fx-font-size: 32px; -fx-text-fill: white; -fx-font-weight: bold;");
                shopItemsContainer.getChildren().add(emptyLabel);
            }
        }
    }

    @FXML
    private void handleResetShop(ActionEvent event) {
        GameSession session = GameSession.getInstance();
        int resetCost = session.isRerollDiscountActive() ? 25 : 50;
        if (session.getCoins() >= resetCost && !session.isShopResetUsed()) {
            session.deductCoins(resetCost);
            session.setShopResetUsed(true);
            if (currencyLabel != null) {
                currencyLabel.setText("Coins: " + session.getCoins());
            }
            if (resetShopButton != null) {
                resetShopButton.setDisable(true);
            }

            int shopSlots = 4 + session.getExtraShopSlots();
            availableJokers = new ArrayList<>(JokerRegistry.JOKERS);
            Collections.shuffle(availableJokers);
            availableJokers = new ArrayList<>(availableJokers.subList(0, Math.min(shopSlots, availableJokers.size())));

            List<Tarot> allTarots = new ArrayList<>(TarotRegistry.TAROTS);
            Collections.shuffle(allTarots);
            availableTarots = new ArrayList<>(allTarots.subList(0, Math.min(shopSlots, allTarots.size())));

            session.getCurrentShopJokers().clear();
            session.getCurrentShopJokers().addAll(availableJokers);
            session.getCurrentShopTarots().clear();
            session.getCurrentShopTarots().addAll(availableTarots);

            session.saveRunToDatabase();

            if (currentTab.equals("jokers")) handleShowJokers(null);
            else if (currentTab.equals("tarots")) handleShowTarots(null);
            else handleShowVouchers(null);
        } else if (session.getCoins() < resetCost && !session.isShopResetUsed()) {
            if (resetShopButton != null) {
                resetShopButton.setText("Not enough Coins!");
            }
        }
    }

    @FXML
    private void handleBuyItem(ActionEvent event) {
        if (selectedJoker != null || selectedTarot != null || selectedVoucher != null) {
            GameSession session = GameSession.getInstance();
            int currentCoins = session.getCoins();
            
            if (selectedVoucher != null) {
                if (currentCoins >= 800) {
                    session.deductCoins(800);
                    session.getOwnedVouchers().add(selectedVoucher.name());
                    
                    switch (selectedVoucher.effectType()) {
                        case EXTRA_JOKER_SLOT -> session.setExtraJokerSlots(session.getExtraJokerSlots() + 1);
                        case CLEARANCE_SALE -> session.setShopDiscountActive(true);
                        case EXTRA_HAND -> session.addExtraHands(1);
                        case OVERSTOCK -> session.setExtraShopSlots(session.getExtraShopSlots() + 1);
                        case REROLL_SURPLUS -> session.setRerollDiscountActive(true);
                        case EXTRA_DISCARD -> session.addExtraDiscards(1);
                    }
                    
                    session.setCurrentShopVoucher(null); // Sold out for this shop
                    selectedVoucher = null;
                    if (buyButton != null) buyButton.setVisible(false);
                    if (currencyLabel != null) currencyLabel.setText("Coins: " + session.getCoins());
                    handleShowVouchers(null);
                    session.saveRunToDatabase();
                } else {
                    if (buyButton != null) buyButton.setText("Not enough Coins!");
                }
                return;
            }
            
            int itemCost = session.isShopDiscountActive() ? 150 : 200;
            if (currentCoins >= itemCost) {
                // Deduct coins
                session.deductCoins(itemCost);
                if (currencyLabel != null) {
                    currencyLabel.setText("Coins: " + session.getCoins());
                }

                if (selectedJoker != null) {
                    session.getOwnedJokers().add(selectedJoker);
                    if (session.getActiveJokers().size() < 3 + session.getExtraJokerSlots()) {
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
