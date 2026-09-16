package cs;

import java.io.IOException;
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

public class DeckController {

    @FXML private Label runStatusLabel;
    @FXML private Label deckSectionTitle;
    
    @FXML private Label activeSectionTitle;
    @FXML private HBox activeItemsContainer;
    
    @FXML private Label ownedSectionTitle;
    @FXML private HBox ownedItemsContainer;

    @FXML
    public void initialize() {
        GameSession session = GameSession.getInstance();
        if (session.getDifficulty() == null) {
            session.loadFromDatabase();
        }

        if (session.getDifficulty() != null && runStatusLabel != null) {
            runStatusLabel.setText(String.format("%s Run • Ante %d/%d",
                session.getDifficulty(), session.getCurrentAnte(), session.getMaxAntes()));
        }

        handleShowJokers(null);
    }

    @FXML
    private void handleShowJokers(ActionEvent event) {
        if (deckSectionTitle != null) deckSectionTitle.setText("Joker Loadout");
        
        GameSession session = GameSession.getInstance();
        List<Joker> owned = session.getOwnedJokers();
        List<Joker> active = session.getActiveJokers();

        if (activeSectionTitle != null) {
            activeSectionTitle.setText(String.format("Active Jokers (%d/3)", active.size()));
        }
        if (ownedSectionTitle != null) {
            ownedSectionTitle.setText("Owned Jokers");
        }
        if (activeItemsContainer != null) activeItemsContainer.getChildren().clear();
        if (ownedItemsContainer != null) ownedItemsContainer.getChildren().clear();

        if (owned.isEmpty()) {
            Label emptyLabel = new Label("No jokers owned yet. Buy some from the Shop!");
            emptyLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5); -fx-font-size: 20px;");
            if (ownedItemsContainer != null) ownedItemsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Joker joker : owned) {
            boolean isActive = active.stream().anyMatch(j -> j.name().equals(joker.name()));
            VBox itemBox = createJokerCardView(joker, isActive);
            if (isActive && activeItemsContainer != null) {
                activeItemsContainer.getChildren().add(itemBox);
            } else if (!isActive && ownedItemsContainer != null) {
                ownedItemsContainer.getChildren().add(itemBox);
            }
        }
    }

    private VBox createJokerCardView(Joker joker, boolean isActive) {
        VBox itemBox = new VBox();
        itemBox.setAlignment(javafx.geometry.Pos.CENTER);
        itemBox.setSpacing(10);

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

            if (!isActive) {
                imgView.setOpacity(0.4);
            }
            imageWrapper.getChildren().add(imgView);
        } catch (Exception e) {
            Label errorLabel = new Label(joker.name());
            errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
            imageWrapper.getChildren().add(errorLabel);
            imageWrapper.setPrefSize(150, 217);
        }

        Tooltip tooltip = new Tooltip(joker.name() + "\n" + joker.description());
        tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(imageWrapper, tooltip);

        Label statusLabel = new Label(isActive ? "✓ ACTIVE" : "INACTIVE");
        statusLabel.setStyle(isActive 
            ? "-fx-text-fill: #6fe3b1; -fx-font-size: 16px; -fx-font-weight: bold;"
            : "-fx-text-fill: rgba(255,255,255,0.4); -fx-font-size: 16px; -fx-font-weight: bold;");

        if (isActive) {
            itemBox.setStyle("-fx-border-color: #6fe3b1; -fx-border-width: 3px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        } else {
            itemBox.setStyle("-fx-border-color: rgba(255,255,255,0.15); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        }

        itemBox.setOnMouseClicked(e -> {
            toggleJoker(joker);
            handleShowJokers(null);
        });
        itemBox.setCursor(javafx.scene.Cursor.HAND);
        itemBox.getChildren().addAll(imageWrapper, statusLabel);
        
        return itemBox;
    }

    private void toggleJoker(Joker joker) {
        GameSession session = GameSession.getInstance();
        List<Joker> active = session.getActiveJokers();

        boolean isCurrentlyActive = active.stream().anyMatch(j -> j.name().equals(joker.name()));
        if (isCurrentlyActive) {
            active.removeIf(j -> j.name().equals(joker.name()));
        } else {
            if (active.size() < 3) {
                active.add(joker);
            }
        }
        session.saveRunToDatabase();
    }

    @FXML
    private void handleShowTarots(ActionEvent event) {
        if (deckSectionTitle != null) deckSectionTitle.setText("Tarot Loadout");
        
        GameSession session = GameSession.getInstance();
        List<Tarot> owned = session.getOwnedTarots();
        List<Tarot> active = session.getActiveTarots();

        if (activeSectionTitle != null) {
            activeSectionTitle.setText(String.format("Active Tarots (%d/5)", active.size()));
        }
        if (ownedSectionTitle != null) {
            ownedSectionTitle.setText("Owned Tarots");
        }
        if (activeItemsContainer != null) activeItemsContainer.getChildren().clear();
        if (ownedItemsContainer != null) ownedItemsContainer.getChildren().clear();

        if (owned.isEmpty()) {
            Label emptyLabel = new Label("No tarots owned yet. Buy some from the Shop!");
            emptyLabel.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5); -fx-font-size: 20px;");
            if (ownedItemsContainer != null) ownedItemsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Tarot tarot : owned) {
            boolean isActive = active.stream().anyMatch(t -> t.name().equals(tarot.name()));
            VBox itemBox = createTarotCardView(tarot, isActive);
            if (isActive && activeItemsContainer != null) {
                activeItemsContainer.getChildren().add(itemBox);
            } else if (!isActive && ownedItemsContainer != null) {
                ownedItemsContainer.getChildren().add(itemBox);
            }
        }
    }

    private VBox createTarotCardView(Tarot tarot, boolean isActive) {
        VBox itemBox = new VBox();
        itemBox.setAlignment(javafx.geometry.Pos.CENTER);
        itemBox.setSpacing(10);

        StackPane imageWrapper = new StackPane();
        imageWrapper.getStyleClass().add("joker-image-wrapper");

        try {
            Image img = new Image(getClass().getResourceAsStream(tarot.imagePath()));
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(150);
            imgView.setFitHeight(217);
            imgView.setPreserveRatio(false);

            Rectangle clip = new Rectangle(150, 217);
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imgView.setClip(clip);

            if (!isActive) {
                imgView.setOpacity(0.4);
            }
            imageWrapper.getChildren().add(imgView);
        } catch (Exception e) {
            Label errorLabel = new Label(tarot.name());
            errorLabel.setStyle("-fx-text-fill: white; -fx-padding: 10px;");
            imageWrapper.getChildren().add(errorLabel);
            imageWrapper.setPrefSize(150, 217);
        }

        Tooltip tooltip = new Tooltip(tarot.name() + "\n" + tarot.description());
        tooltip.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        tooltip.setShowDelay(Duration.millis(100));
        Tooltip.install(imageWrapper, tooltip);

        Label statusLabel = new Label(isActive ? "✓ ACTIVE" : "INACTIVE");
        statusLabel.setStyle(isActive 
            ? "-fx-text-fill: #6fe3b1; -fx-font-size: 16px; -fx-font-weight: bold;"
            : "-fx-text-fill: rgba(255,255,255,0.4); -fx-font-size: 16px; -fx-font-weight: bold;");

        if (isActive) {
            itemBox.setStyle("-fx-border-color: #6fe3b1; -fx-border-width: 3px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        } else {
            itemBox.setStyle("-fx-border-color: rgba(255,255,255,0.15); -fx-border-width: 2px; -fx-border-radius: 10px; -fx-background-radius: 10px; -fx-padding: 10px;");
        }

        itemBox.setOnMouseClicked(e -> {
            toggleTarot(tarot);
            handleShowTarots(null);
        });
        itemBox.setCursor(javafx.scene.Cursor.HAND);
        itemBox.getChildren().addAll(imageWrapper, statusLabel);
        
        return itemBox;
    }

    private void toggleTarot(Tarot tarot) {
        GameSession session = GameSession.getInstance();
        List<Tarot> active = session.getActiveTarots();

        boolean isCurrentlyActive = active.stream().anyMatch(t -> t.name().equals(tarot.name()));
        if (isCurrentlyActive) {
            active.removeIf(t -> t.name().equals(tarot.name()));
        } else {
            if (active.size() < 5) {
                active.add(tarot);
            }
        }
        session.saveRunToDatabase();
    }

    @FXML
    private void handleShowVouchers(ActionEvent event) {
        if (deckSectionTitle != null) deckSectionTitle.setText("Vouchers");
        if (activeSectionTitle != null) activeSectionTitle.setText("");
        if (ownedSectionTitle != null) ownedSectionTitle.setText("");
        if (activeItemsContainer != null) activeItemsContainer.getChildren().clear();
        
        if (ownedItemsContainer != null) {
            ownedItemsContainer.getChildren().clear();
            Label placeholder = new Label("Vouchers Coming Soon...");
            placeholder.setStyle("-fx-text-fill: rgba(255, 255, 255, 0.5); -fx-font-size: 24px;");
            ownedItemsContainer.getChildren().add(placeholder);
        }
    }

    @FXML
    private void handleReturnHome(ActionEvent event) {
        try {
            App.setRoot("home");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
