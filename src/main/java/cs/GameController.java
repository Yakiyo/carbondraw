package cs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.Node;
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

    // Keeps track of which visual node belongs to each Card
    private Map<Card, StackPane> cardImageNodes = new HashMap<>();

    // Keeps track of the actual card container (the VBox with
    // the layoutX/Y and the mouse handlers) for each Card, so
    // the hand can be reflowed without hunting through the
    // scene graph for it.
    private Map<Card, VBox> cardViewNodes = new HashMap<>();

    // Prevents multiple Play Hand clicks during animation
    private boolean isPlayingHand = false;

    // Prevents hover/selection interaction while the hand
    // is being dealt out from the stack at round start
    private boolean isDealingHand = false;

    // Tracks which of the center "staging" slots each
    // selected card currently occupies, so several selected
    // cards can sit side by side at the middle of the table
    // instead of stacking exactly on top of one another, and
    // so a deselected card frees its slot for later picks.
    private Map<Card, Integer> selectionSlots = new HashMap<>();

    private static final int MAX_SELECTED_SLOTS = 5;
    private static final String SELECT_ANIM_KEY = "cs.selectAnim";
    private static final String RELAYOUT_ANIM_KEY = "cs.relayoutAnim";

    // How far a selected card rises above the hand. Cards in
    // the hand render at layoutY = 30 with a 260px tall image,
    // so their visible bottom edge sits at y = 290. Rising by
    // at least 260 clears that completely; the extra margin
    // below keeps a visible gap instead of just grazing it.
    private static final double TABLE_Y = -280;

    // Full card width plus a gap, so cards staged at the
    // center of the table never overlap one another. Sized to
    // match STAGE_SCALE below (a smaller card needs less room).
    private static final double STAGE_SLOT_SPACING = 175;

    // Selected cards shrink slightly instead of growing, so
    // several of them still fit comfortably in the table area
    // at once.
    private static final double STAGE_SCALE = 0.85;

    // Short UI sound effect for selecting / deselecting a
    // card. Loaded lazily; if the audio file isn't present
    // in resources, playback is silently skipped so nothing
    // else in the game is affected.
    private javafx.scene.media.AudioClip cardClickSound;
    private boolean selectionSoundLoaded = false;

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


    // =========================================================
    // SHOW / CLOSE HANDS
    // =========================================================

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


    // =========================================================
    // INITIALIZE GAME
    // =========================================================

    @FXML
    public void initialize() {

        GameSession session = GameSession.getInstance();

        if (session.getDifficulty() != null) {

            targetScore = session.getTargetPoints();

            gameInfoLabel.setText(
                String.format(
                    "Ante %d/%d  |  %s  |  Target: %,d Pts",
                    session.getCurrentAnte(),
                    session.getMaxAntes(),
                    session.getDifficulty(),
                    targetScore
                )
            );
        }

        // Deal 10 random cards from the persistent deck
        remainingDeck = new ArrayList<>(session.getCurrentDeck());
        Collections.shuffle(remainingDeck);

        currentHand = new ArrayList<>();

        for (int i = 0; i < 10 && !remainingDeck.isEmpty(); i++) {
            currentHand.add(remainingDeck.remove(0));
        }

        int scaleBonus = (session.getCurrentAnte() - 1) / 2;

        discardsLeft =
            4 + scaleBonus + session.getExtraDiscards();

        currentScore = 0;

        handsLeft =
            4 + scaleBonus + session.getExtraHands();

        if (discardsLabel != null) {
            discardsLabel.setText(String.valueOf(discardsLeft));
        }

        if (discardButton != null) {
            discardButton.setDisable(false);
        }

        if (scoreLabel != null) {
            scoreLabel.setText("0");
        }

        if (handsLabel != null) {
            handsLabel.setText(String.valueOf(handsLeft));
        }

        if (gameOverPopup != null) {
            gameOverPopup.setVisible(false);
        }

        renderJokers(session.getActiveJokers());
        renderTarots(session.getActiveTarots());
        renderCards(currentHand, true);
    }


    // =========================================================
    // RENDER JOKERS
    // =========================================================

    private void renderJokers(List<Joker> jokers) {

        if (jokersContainer == null) {
            return;
        }

        jokersContainer.getChildren().clear();

        for (Joker joker : jokers) {

            StackPane imageWrapper = new StackPane();

            imageWrapper.getStyleClass()
                        .add("joker-image-wrapper");

            try {

                Image img = new Image(
                    getClass()
                        .getResource(joker.imagePath())
                        .toExternalForm()
                );

                ImageView imgView = new ImageView(img);

                imgView.setFitWidth(100);
                imgView.setFitHeight(145);
                imgView.setPreserveRatio(false);

                Rectangle clip =
                    new Rectangle(100, 145);

                clip.setArcWidth(10);
                clip.setArcHeight(10);

                imgView.setClip(clip);

                imageWrapper.getChildren().add(imgView);

            } catch (Exception e) {

                System.err.println(
                    "Could not load joker image: "
                    + joker.imagePath()
                );

                Label errorLabel =
                    new Label(joker.name());

                errorLabel.setStyle(
                    "-fx-text-fill: white; -fx-padding: 10px;"
                );

                imageWrapper.getChildren().add(errorLabel);

                imageWrapper.setPrefSize(100, 145);
            }

            Tooltip tooltip =
                new Tooltip(
                    joker.name()
                    + "\n"
                    + joker.description()
                );

            tooltip.setStyle(
                "-fx-font-size: 14px; "
                + "-fx-font-weight: bold;"
            );

            tooltip.setShowDelay(
                Duration.millis(100)
            );

            Tooltip.install(
                imageWrapper,
                tooltip
            );

            jokersContainer
                .getChildren()
                .add(imageWrapper);
        }
    }


    // =========================================================
    // RENDER TAROTS
    // =========================================================

    private void renderTarots(List<Tarot> tarots) {

        if (tarotsContainer == null) {
            return;
        }

        tarotsContainer.getChildren().clear();

        for (Tarot tarot : tarots) {

            StackPane imageWrapper = new StackPane();

            imageWrapper.getStyleClass()
                        .add("joker-image-wrapper");

            try {

                Image img = new Image(
                    getClass()
                        .getResource(tarot.imagePath())
                        .toExternalForm()
                );

                ImageView imgView =
                    new ImageView(img);

                imgView.setFitWidth(100);
                imgView.setFitHeight(145);
                imgView.setPreserveRatio(false);

                Rectangle clip =
                    new Rectangle(100, 145);

                clip.setArcWidth(10);
                clip.setArcHeight(10);

                imgView.setClip(clip);

                imageWrapper
                    .getChildren()
                    .add(imgView);

            } catch (Exception e) {

                System.err.println(
                    "Could not load tarot image: "
                    + tarot.imagePath()
                );

                Label errorLabel =
                    new Label(tarot.name());

                errorLabel.setStyle(
                    "-fx-text-fill: white; -fx-padding: 10px;"
                );

                imageWrapper
                    .getChildren()
                    .add(errorLabel);

                imageWrapper.setPrefSize(100, 145);
            }

            Tooltip tooltip =
                new Tooltip(
                    tarot.name()
                    + "\n"
                    + tarot.description()
                );

            tooltip.setStyle(
                "-fx-font-size: 14px; "
                + "-fx-font-weight: bold;"
            );

            tooltip.setShowDelay(
                Duration.millis(100)
            );

            Tooltip.install(
                imageWrapper,
                tooltip
            );

            // Tarot selection
            imageWrapper.setOnMouseClicked(e -> {

                for (Node node :
                        tarotsContainer.getChildren()) {

                    node.setStyle("");
                }

                if (selectedTarot == tarot) {

                    selectedTarot = null;

                    if (useTarotButton != null) {
                        useTarotButton.setVisible(false);
                    }

                } else {

                    imageWrapper.setStyle(
                        "-fx-border-color: #fca311; "
                        + "-fx-border-width: 3px; "
                        + "-fx-border-radius: 10px;"
                    );

                    selectedTarot = tarot;

                    updateTarotButtonState();
                }
            });

            tarotsContainer
                .getChildren()
                .add(imageWrapper);
        }
    }


    // =========================================================
    // CARD ANIMATION HELPER
    // =========================================================

    private void animateCard(
            Node card,
            double y,
            double scale) {

        TranslateTransition move =
            new TranslateTransition(
                Duration.millis(120),
                card
            );

        move.setToY(y);

        ScaleTransition zoom =
            new ScaleTransition(
                Duration.millis(120),
                card
            );

        zoom.setToX(scale);
        zoom.setToY(scale);

        ParallelTransition animation =
            new ParallelTransition(
                move,
                zoom
            );

        animation.play();
    }


    // =========================================================
    // SELECTION SOUND EFFECT
    // =========================================================

    private void loadSelectionSound() {

        if (selectionSoundLoaded) {
            return;
        }

        selectionSoundLoaded = true;

        try {

            cardClickSound =
                new javafx.scene.media.AudioClip(
                    getClass()
                        .getResource(
                            "/cs/audio/click_002.mp3"
                        )
                        .toExternalForm()
                );

        } catch (Exception e) {

            System.err.println(
                "Could not load card click sound: "
                + e.getMessage()
            );
        }
    }

    // Played on both selecting and deselecting a card.
    private void playCardClickSound() {

        loadSelectionSound();

        if (cardClickSound != null) {
            cardClickSound.play();
        }
    }


    // =========================================================
    // SELECT / DESELECT: MOVE TO / FROM CENTER OF TABLE
    // =========================================================

    private void stopRunningSelectAnimation(Node node) {

        Object running =
            node.getProperties().get(SELECT_ANIM_KEY);

        if (running instanceof Animation) {
            ((Animation) running).stop();
        }
    }

    /**
     * Card is selected: it turns and flies from its spot in
     * the hand to an open slot at the center of the table.
     * Full-width slot spacing keeps every staged card
     * completely visible, never overlapping its neighbors.
     */
    private void animateCardToCenter(VBox cardView, Card card) {

        // Claim the lowest free staging slot (0..4)
        int slot = 0;

        while (
            slot < MAX_SELECTED_SLOTS
            && selectionSlots.containsValue(slot)
        ) {
            slot++;
        }

        selectionSlots.put(card, slot);

        double cardWidth = 180;
        double paneWidth = 1400;

        double centerX =
            (paneWidth - cardWidth) / 2.0;

        double slotOffset =
            (slot - (MAX_SELECTED_SLOTS - 1) / 2.0)
            * STAGE_SLOT_SPACING;

        double targetTranslateX =
            (centerX + slotOffset)
            - cardView.getLayoutX();

        stopRunningSelectAnimation(cardView);

        // Also cancel a still-running reflow (from a very
        // recent deselect) on this exact card, so it can't
        // fight this transition for the same translateX/Y.
        Object runningReflow =
            cardView.getProperties().get(RELAYOUT_ANIM_KEY);

        if (runningReflow instanceof Animation) {
            ((Animation) runningReflow).stop();
            cardView.getProperties().remove(RELAYOUT_ANIM_KEY);
        }

        TranslateTransition move =
            new TranslateTransition(
                Duration.millis(320),
                cardView
            );

        move.setToX(targetTranslateX);
        move.setToY(TABLE_Y);
        move.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition zoom =
            new ScaleTransition(
                Duration.millis(320),
                cardView
            );

        zoom.setToX(STAGE_SCALE);
        zoom.setToY(STAGE_SCALE);
        zoom.setInterpolator(Interpolator.EASE_OUT);

        // The card "turns" on its own as it travels to the
        // center of the table.
        RotateTransition spin =
            new RotateTransition(
                Duration.millis(320),
                cardView
            );

        spin.setToAngle(360);
        spin.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition toCenter =
            new ParallelTransition(
                move,
                zoom,
                spin
            );

        // Raised above both the hand and every other card
        // still resting, so nothing clips through it in transit.
        cardView.setViewOrder(-1);

        cardView
            .getProperties()
            .put(SELECT_ANIM_KEY, toCenter);

        toCenter.setOnFinished(e ->
            cardView.getProperties().remove(SELECT_ANIM_KEY)
        );

        toCenter.play();
    }

    /**
     * Card is deselected while staged at the center of the
     * table: it turns and shrinks back down to normal size.
     * Its actual return position/x-position is handled by
     * relayoutHand(), called right alongside this, so it
     * lands exactly in its rightful spot in the reflowed hand
     * rather than snapping back to wherever it started.
     */
    private void animateCardFromCenter(VBox cardView, Card card) {

        selectionSlots.remove(card);

        stopRunningSelectAnimation(cardView);

        ScaleTransition zoom =
            new ScaleTransition(
                Duration.millis(280),
                cardView
            );

        zoom.setToX(1.0);
        zoom.setToY(1.0);
        zoom.setInterpolator(Interpolator.EASE_OUT);

        RotateTransition spin =
            new RotateTransition(
                Duration.millis(280),
                cardView
            );

        spin.setToAngle(0);
        spin.setInterpolator(Interpolator.EASE_OUT);

        ParallelTransition fromCenter =
            new ParallelTransition(
                zoom,
                spin
            );

        cardView
            .getProperties()
            .put(SELECT_ANIM_KEY, fromCenter);

        fromCenter.setOnFinished(e -> {
            cardView.getProperties().remove(SELECT_ANIM_KEY);
            cardView.setViewOrder(0);
        });

        fromCenter.play();
    }

    /**
     * Re-fans every card that is NOT currently selected, using
     * only the count of those remaining cards - so the hand
     * closes ranks into the space a selected card leaves
     * behind, and a deselected card lands in its rightful spot
     * in that reflowed fan (not necessarily where it started).
     * Selected cards themselves are untouched here; their
     * position is entirely owned by animateCardToCenter.
     */
    private void relayoutHand() {

        List<Card> unselected = new ArrayList<>();

        for (Card c : currentHand) {
            if (!selectedCards.contains(c)) {
                unselected.add(c);
            }
        }

        int n = unselected.size();

        if (n == 0) {
            return;
        }

        double centerIndex = (n - 1) / 2.0;
        double cardWidth = 180;
        double spacingX = cardWidth * 0.75;
        double paneWidth = 1400;
        double baseX = (paneWidth - cardWidth) / 2.0;

        for (int i = 0; i < n; i++) {

            Card card = unselected.get(i);

            VBox cardView = cardViewNodes.get(card);

            if (cardView == null) {
                continue;
            }

            double offset = i - centerIndex;
            double newX = baseX + (offset * spacingX);

            double targetTranslateX =
                newX - cardView.getLayoutX();

            Object running =
                cardView.getProperties().get(RELAYOUT_ANIM_KEY);

            if (running instanceof Animation) {
                ((Animation) running).stop();
            }

            TranslateTransition reflow =
                new TranslateTransition(
                    Duration.millis(260),
                    cardView
                );

            reflow.setToX(targetTranslateX);

            // Every unselected card belongs back at the hand's
            // resting height, whether it's sliding sideways to
            // close a gap or returning from the center table.
            reflow.setToY(0);
            reflow.setInterpolator(Interpolator.EASE_OUT);

            cardView
                .getProperties()
                .put(RELAYOUT_ANIM_KEY, reflow);

            reflow.setOnFinished(e ->
                cardView.getProperties().remove(RELAYOUT_ANIM_KEY)
            );

            reflow.play();
        }
    }

 /* */
    // =========================================================
    // RENDER PLAYABLE CARDS
    // =========================================================
// =========================================================
// RENDER PLAYABLE CARDS
// =========================================================

private void renderCards(List<Card> cards) {
    renderCards(cards, false);
}

/**
 * Renders the player's hand.
 *
 * dealAnimation == true plays a "deal from the deck" intro:
 * every card starts stacked in one pile at the center of the
 * hand area, then flies out to its normal fanned position.
 * dealAnimation == false (the original behaviour) places every
 * card straight at its final position, with no animation.
 */
private void renderCards(List<Card> cards, boolean dealAnimation) {

    if (cardHandContainer == null) {
        return;
    }

    cardHandContainer
        .getChildren()
        .clear();

    selectedCards.clear();

    cardImageNodes.clear();

    cardViewNodes.clear();

    selectionSlots.clear();

    int n = cards.size();

    if (n == 0) {
        return;
    }

    double centerIndex =
        (n - 1) / 2.0;

    double cardWidth = 180;
    double cardHeight = 260;

    double spacingX =
        cardWidth * 0.75;

    double paneWidth = 1400;

    // Collects the per-card "fly to fan position" animations
    // so they can all be started together, staggered, once
    // every card node has been built.
    List<Animation> dealAnimations = new ArrayList<>();

    for (int i = 0; i < n; i++) {

        Card card = cards.get(i);

        VBox cardView =
            new VBox();

        cardView
            .getStyleClass()
            .add("card-view");

        StackPane imageWrapper =
            new StackPane();

        imageWrapper
            .getStyleClass()
            .add("card-image-wrapper");

        // Remember which visual node belongs to this card
        cardImageNodes.put(
            card,
            imageWrapper
        );

        cardViewNodes.put(
            card,
            cardView
        );

        try {

            Image img =
                new Image(
                    getClass()
                        .getResource(
                            "/cs/"
                            + card.imagePath()
                        )
                        .toExternalForm()
                );

            ImageView imgView =
                new ImageView(img);

            imgView.setFitWidth(cardWidth);
            imgView.setFitHeight(cardHeight);
            imgView.setPreserveRatio(false);

            Rectangle clip =
                new Rectangle(
                    cardWidth,
                    cardHeight
                );

            clip.setArcWidth(15);
            clip.setArcHeight(15);

            imgView.setClip(clip);

            imageWrapper
                .getChildren()
                .add(imgView);

        } catch (Exception e) {

            System.err.println(
                "Could not load image: "
                + card.imagePath()
            );

            Label errorLabel =
                new Label(card.name());

            errorLabel.setStyle(
                "-fx-text-fill: white; "
                + "-fx-padding: 20px;"
            );

            imageWrapper
                .getChildren()
                .add(errorLabel);

            imageWrapper.setPrefSize(
                cardWidth,
                cardHeight
            );
        }

        cardView
            .getChildren()
            .add(imageWrapper);

        // Arrange cards in a straight line
        double offset =
            i - centerIndex;

        double baseX =
            (paneWidth - cardWidth)
            / 2.0;

        double x =
            baseX
            + (offset * spacingX);

        double y = 30.0;

        cardView.setLayoutX(x);
        cardView.setLayoutY(y);

        if (dealAnimation) {

            // Start every card stacked on top of one another
            // at the center of the hand area, as if fanning
            // out from a single dealt-from-the-deck pile.
            double stackX =
                (paneWidth - cardWidth) / 2.0;

            double stackY = y;

            cardView.setTranslateX(stackX - x);
            cardView.setTranslateY(stackY - y);

            // Small stagger in the stack so it doesn't look
            // like one flat sheet of identical cards.
            cardView.setRotate((i % 2 == 0) ? -4 : 4);
            cardView.setScaleX(0.94);
            cardView.setScaleY(0.94);

            // Deal that card above every earlier one so the
            // pile still reads correctly mid-animation.
            cardView.setViewOrder(-(n - i));

            TranslateTransition dealMove =
                new TranslateTransition(
                    Duration.millis(380),
                    cardView
                );

            dealMove.setToX(0);
            dealMove.setToY(0);
            dealMove.setInterpolator(
                Interpolator.EASE_OUT
            );

            RotateTransition dealRotate =
                new RotateTransition(
                    Duration.millis(380),
                    cardView
                );

            dealRotate.setToAngle(0);
            dealRotate.setInterpolator(
                Interpolator.EASE_OUT
            );

            ScaleTransition dealScale =
                new ScaleTransition(
                    Duration.millis(380),
                    cardView
                );

            dealScale.setToX(1.0);
            dealScale.setToY(1.0);
            dealScale.setInterpolator(
                Interpolator.EASE_OUT
            );

            ParallelTransition dealCard =
                new ParallelTransition(
                    dealMove,
                    dealRotate,
                    dealScale
                );

            // Cards deal out one after another, left to right.
            dealCard.setDelay(
                Duration.millis(i * 70)
            );

            final VBox dealtCardView = cardView;

            dealCard.setOnFinished(e ->
                dealtCardView.setViewOrder(0)
            );

            dealAnimations.add(dealCard);
        }

        // Tooltip
        Tooltip tooltip =
            new Tooltip(
                "Points: "
                + card.points()
            );

        tooltip.setStyle(
            "-fx-font-size: 14px; "
            + "-fx-font-weight: bold;"
        );

        tooltip.setShowDelay(
            Duration.millis(100)
        );

        Tooltip.install(
            imageWrapper,
            tooltip
        );

        // Hand cursor
        cardView.setCursor(
            Cursor.HAND
        );


        // =====================================================
        // HOVER ANIMATION
        // =====================================================

        cardView.setOnMouseEntered(e -> {

            if (!selectedCards.contains(card)
                    && !isPlayingHand
                    && !isDealingHand) {

                animateCard(
                    cardView,
                    -10,
                    1.04
                );
            }
        });

        cardView.setOnMouseExited(e -> {

            if (!selectedCards.contains(card)
                    && !isPlayingHand
                    && !isDealingHand) {

                animateCard(
                    cardView,
                    0,
                    1.0
                );
            }
        });


        // =====================================================
        // CARD SELECTION
        // =====================================================

        cardView.setOnMouseClicked(e -> {

            if (isPlayingHand || isDealingHand) {
                return;
            }

            if (selectedCards.contains(card)) {

                // Deselect: card shrinks back down and the
                // hand reflows to give it back its rightful
                // spot in the fan
                selectedCards.remove(card);

                animateCardFromCenter(cardView, card);
                playCardClickSound();
                relayoutHand();

            } else {

                // Maximum 5 cards
                if (selectedCards.size() < 5) {

                    selectedCards.add(card);

                    // Select: card turns and flies to the
                    // center of the table, and the rest of
                    // the hand closes ranks into the space
                    // it leaves behind
                    animateCardToCenter(cardView, card);
                    playCardClickSound();
                    relayoutHand();
                }
            }

            updateHandInfoDisplay();
            updateTarotButtonState();
        });

        cardHandContainer
            .getChildren()
            .add(cardView);
    }

    if (dealAnimation && !dealAnimations.isEmpty()) {

        isDealingHand = true;

        if (discardButton != null) {
            discardButton.setDisable(true);
        }

        ParallelTransition dealSequence =
            new ParallelTransition();

        dealSequence
            .getChildren()
            .addAll(dealAnimations);

        dealSequence.setOnFinished(e -> {

            isDealingHand = false;

            if (discardButton != null) {
                discardButton.setDisable(
                    discardsLeft <= 0
                );
            }
        });

        dealSequence.play();
    }
}
    



    // =========================================================
    // REAL CARD PLAY ANIMATION
    // =========================================================

    // =========================================================
// REAL CARD PLAY ANIMATION
// =========================================================

private void animatePlayedCards(
        List<Card> cards,
        Runnable onFinished) {

    ParallelTransition allAnimations =
        new ParallelTransition();

    double cardWidth = 180;
    double cardHeight = 260;

    /*
     * Target position.
     *
     * The cards move toward the middle/top area
     * of the game before disappearing.
     */
    double targetX =
        (cardHandContainer.getWidth()
         - cardWidth) / 2.0;

    double targetY = TABLE_Y;

    int index = 0;

    for (Card card : cards) {

        StackPane imageWrapper =
            cardImageNodes.get(card);

        if (imageWrapper == null) {
            continue;
        }

        /*
         * IMPORTANT:
         *
         * imageWrapper is inside cardView.
         * The layoutX/layoutY belong to cardView,
         * so we animate cardView instead.
         */
        Node cardNode =
            imageWrapper.getParent();

        if (cardNode == null) {
            continue;
        }

        double currentX =
            cardNode.getLayoutX();

        double currentY =
            cardNode.getLayoutY();

        // Cancel any select/deselect animation still in
        // flight on this card so it doesn't fight the play
        // animation for the same properties.
        stopRunningSelectAnimation(cardNode);

        // NOTE: translateX/Y and scale are intentionally NOT
        // reset here. A played card was, by definition, sitting
        // selected at the center of the table (see
        // animateCardToCenter), so this animation continues
        // smoothly from wherever it currently is instead of
        // snapping it back to the hand first. Rotation is
        // normalized rather than reset, because forcing it to
        // exactly 0 would visibly snap a card back from a 360
        // degree turn even though 0 and 360 look identical.
        cardNode.setRotate(cardNode.getRotate() % 360);
        cardNode.setOpacity(1.0);

        // =====================================================
        // MOVE
        // =====================================================

        TranslateTransition move =
            new TranslateTransition(
                Duration.millis(500),
                cardNode
            );

        move.setToX(
            targetX - currentX
        );

        move.setToY(
            targetY - currentY
        );


        // =====================================================
        // SHRINK
        // =====================================================

        ScaleTransition scale =
            new ScaleTransition(
                Duration.millis(500),
                cardNode
            );

        scale.setToX(0.72);
        scale.setToY(0.72);


        // =====================================================
        // FADE
        // =====================================================

        FadeTransition fade =
            new FadeTransition(
                Duration.millis(500),
                cardNode
            );

        fade.setToValue(0);


        // =====================================================
        // ROTATION
        // =====================================================

        RotateTransition rotate =
            new RotateTransition(
                Duration.millis(500),
                cardNode
            );

        if (index % 2 == 0) {
            rotate.setToAngle(-10);
        } else {
            rotate.setToAngle(10);
        }


        // =====================================================
        // COMBINE ANIMATIONS
        // =====================================================

        ParallelTransition cardAnimation =
            new ParallelTransition(
                move,
                scale,
                fade,
                rotate
            );

        allAnimations
            .getChildren()
            .add(cardAnimation);

        index++;
    }

    allAnimations.setOnFinished(e -> {

        if (onFinished != null) {
            onFinished.run();
        }
    });

    allAnimations.play();
}


    // =========================================================
    // RETURN HOME
    // =========================================================

    @FXML
    private void handleReturnHome(ActionEvent event) {

        GameSession
            .getInstance()
            .endSession();

        try {

            App.setRoot("home");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // GO TO SHOP
    // =========================================================

    @FXML
    private void handleGoToShop(ActionEvent event) {

        try {

            App.setRoot("shop");

        } catch (IOException e) {

            e.printStackTrace();
        }
    }


    // =========================================================
    // NEXT ROUND
    // =========================================================

    @FXML
    private void handleNextRoundAction(
            ActionEvent event) {

        GameSession session =
            GameSession.getInstance();

        boolean hasMore =
            session.advanceAnte();

        if (hasMore) {

            try {

                App.setRoot("game");

            } catch (IOException e) {

                e.printStackTrace();
            }

        } else {

            // All 10 antes beaten
            session.endSession();

            try {

                App.setRoot("home");

            } catch (IOException e) {

                e.printStackTrace();
            }
        }
    }


    // =========================================================
    // CALCULATE SCORE WITH JOKERS
    // =========================================================

    private int[] calculateScoreWithJokers(
            HandResult bestHand,
            int discardsLeft,
            int baseChips,
            int baseMult) {

        int finalChips = baseChips;
        int finalMult = baseMult;

        long renewableCount =
            bestHand == null
                ? 0
                : bestHand.cardsUsed()
                    .stream()
                    .filter(
                        c ->
                            c.getOriginalSuit() != null
                            &&
                            c.getOriginalSuit()
                                .contains("renewable")
                    )
                    .count();

        long biosphereCount =
            bestHand == null
                ? 0
                : bestHand.cardsUsed()
                    .stream()
                    .filter(
                        c ->
                            c.getOriginalSuit() != null
                            &&
                            c.getOriginalSuit()
                                .contains("biosphere")
                    )
                    .count();

        long greenTechCount =
            bestHand == null
                ? 0
                : bestHand.cardsUsed()
                    .stream()
                    .filter(
                        c ->
                            c.getOriginalSuit() != null
                            &&
                            c.getOriginalSuit()
                                .contains("green_tech")
                    )
                    .count();

        long policyCount =
            bestHand == null
                ? 0
                : bestHand.cardsUsed()
                    .stream()
                    .filter(
                        c ->
                            c.getOriginalSuit() != null
                            &&
                            c.getOriginalSuit()
                                .contains("policy")
                    )
                    .count();

        String handName =
            bestHand == null
                ? ""
                : bestHand.handName();


        for (Joker joker :
                GameSession
                    .getInstance()
                    .getActiveJokers()) {

            switch (joker.effectType()) {

                case ADD_CHIPS ->
                    finalChips +=
                        joker.effectValue();

                case ADD_MULTI ->
                    finalMult +=
                        joker.effectValue();

                case MULT_MULTI ->
                    finalMult *=
                        joker.effectValue();

                case ADD_MULT_PER_RENEWABLE ->
                    finalMult +=
                        joker.effectValue()
                        * (int) renewableCount;

                case ADD_MULT_PER_BIOSPHERE ->
                    finalMult +=
                        joker.effectValue()
                        * (int) biosphereCount;

                case ADD_MULT_PER_GREENTECH ->
                    finalMult +=
                        joker.effectValue()
                        * (int) greenTechCount;

                case ADD_MULT_PER_POLICY ->
                    finalMult +=
                        joker.effectValue()
                        * (int) policyCount;

                case ADD_MULT_IF_PAIR -> {

                    if (
                        handName.contains(
                            "Grassroots Action"
                        )
                        ||
                        handName.contains(
                            "Bilateral Agreement"
                        )
                        ||
                        handName.contains(
                            "Symbiotic Loop"
                        )
                    ) {
                        finalMult +=
                            joker.effectValue();
                    }
                }

                case ADD_MULT_IF_THREE_KIND -> {

                    if (
                        handName.contains(
                            "Sector Focus"
                        )
                        ||
                        handName.contains(
                            "Symbiotic Loop"
                        )
                    ) {
                        finalMult +=
                            joker.effectValue();
                    }
                }

                case ADD_MULT_IF_FOUR_KIND -> {

                    if (
                        handName.contains(
                            "Industry Overhaul"
                        )
                    ) {
                        finalMult +=
                            joker.effectValue();
                    }
                }

                case ADD_MULT_IF_STRAIGHT -> {

                    if (
                        handName.contains(
                            "Holistic Strategy"
                        )
                        ||
                        handName.contains(
                            "Net Zero Earthshot"
                        )
                    ) {
                        finalMult +=
                            joker.effectValue();
                    }
                }

                case ADD_MULT_IF_FLUSH -> {

                    if (
                        handName.contains(
                            "Monoculture"
                        )
                        ||
                        handName.contains(
                            "Net Zero Earthshot"
                        )
                    ) {
                        finalMult +=
                            joker.effectValue();
                    }
                }

                case ADD_CHIPS_IF_PAIR -> {

                    if (
                        handName.contains(
                            "Grassroots Action"
                        )
                        ||
                        handName.contains(
                            "Bilateral Agreement"
                        )
                        ||
                        handName.contains(
                            "Symbiotic Loop"
                        )
                    ) {
                        finalChips +=
                            joker.effectValue();
                    }
                }

                case ADD_CHIPS_IF_TWO_PAIR -> {

                    if (
                        handName.contains(
                            "Bilateral Agreement"
                        )
                    ) {
                        finalChips +=
                            joker.effectValue();
                    }
                }

                case ADD_CHIPS_IF_THREE_KIND -> {

                    if (
                        handName.contains(
                            "Sector Focus"
                        )
                        ||
                        handName.contains(
                            "Symbiotic Loop"
                        )
                    ) {
                        finalChips +=
                            joker.effectValue();
                    }
                }

                case ADD_CHIPS_IF_STRAIGHT -> {

                    if (
                        handName.contains(
                            "Holistic Strategy"
                        )
                        ||
                        handName.contains(
                            "Net Zero Earthshot"
                        )
                    ) {
                        finalChips +=
                            joker.effectValue();
                    }
                }

                case ADD_CHIPS_IF_FLUSH -> {

                    if (
                        handName.contains(
                            "Monoculture"
                        )
                        ||
                        handName.contains(
                            "Net Zero Earthshot"
                        )
                    ) {
                        finalChips +=
                            joker.effectValue();
                    }
                }

                case ADD_MULT_IF_SMALL_HAND -> {

                    if (
                        bestHand != null
                        &&
                        bestHand.cardsUsed()
                            .size() <= 3
                    ) {
                        finalMult +=
                            joker.effectValue();
                    }
                }

                case ADD_CHIPS_PER_DISCARD ->
                    finalChips +=
                        joker.effectValue()
                        * discardsLeft;

                case ADD_MULT_IF_NO_DISCARDS -> {

                    if (discardsLeft == 0) {
                        finalMult +=
                            joker.effectValue();
                    }
                }
            }
        }

        return new int[] {
            finalChips,
            finalMult
        };
    }


    // =========================================================
    // PLAY HAND
    // =========================================================

    @FXML
    private void handlePlayHand(
            ActionEvent event) {

        // Prevent another click while animation is running
        if (isPlayingHand) {
            return;
        }

        if (
            selectedCards.isEmpty()
            ||
            handsLeft <= 0
        ) {
            return;
        }


        HandResult bestHand =
            HandEvaluator
                .evaluateSelectedCards(
                    selectedCards
                );

        if (bestHand == null) {

            System.out.println(
                "No valid hand found for selection!"
            );

            return;
        }


        // Save the selected cards
        // because selectedCards will be cleared later
        List<Card> cardsBeingPlayed =
            new ArrayList<>(
                selectedCards
            );


        int baseChips = 0;

        int mult =
            bestHand.mult();

        List<Card> cardsToDestroy =
            new ArrayList<>();


        // =====================================================
        // CARD EFFECTS
        // =====================================================

        for (Card c :
                bestHand.cardsUsed()) {

            baseChips +=
                c.points();


            if (
                c.enhancement()
                ==
                Card.Enhancement.MULT
            ) {

                mult += 4;

            } else if (
                c.enhancement()
                ==
                Card.Enhancement.BONUS
            ) {

                baseChips += 30;

            } else if (
                c.enhancement()
                ==
                Card.Enhancement.GLASS
            ) {

                mult *= 2;

                if (Math.random() < 0.25) {

                    cardsToDestroy.add(c);
                }
            }


            // Lucky effect
            if (
                c.enhancement()
                ==
                Card.Enhancement.LUCKY
            ) {

                if (Math.random() < 0.2) {

                    mult += 20;
                }

                if (Math.random() < 0.066) {

                    GameSession
                        .getInstance()
                        .addCoins(20);
                }
            }
        }


        // =====================================================
        // STEEL CARDS STILL IN HAND
        // =====================================================

        for (Card held :
                currentHand) {

            if (
                !selectedCards.contains(held)
                &&
                held.enhancement()
                    ==
                    Card.Enhancement.STEEL
            ) {

                mult =
                    (int)
                    Math.round(
                        mult * 1.5
                    );
            }
        }


        // =====================================================
        // FINAL SCORE
        // =====================================================

        int[] calculated =
            calculateScoreWithJokers(
                bestHand,
                discardsLeft,
                baseChips,
                mult
            );

        int pointsEarned =
            calculated[0]
            *
            calculated[1];


        // =====================================================
        // START ANIMATION
        // =====================================================

        isPlayingHand = true;


        animatePlayedCards(
            cardsBeingPlayed,

            () -> {

                // =================================================
                // ORIGINAL GAME LOGIC
                // =================================================

                currentScore +=
                    pointsEarned;


                if (scoreLabel != null) {

                    scoreLabel.setText(
                        String.valueOf(
                            currentScore
                        )
                    );
                }


                // =================================================
                // REMOVE PLAYED CARDS
                // =================================================

                for (Card playedCard :
                        cardsBeingPlayed) {

                    int index =
                        currentHand.indexOf(
                            playedCard
                        );

                    if (index != -1) {

                        currentHand.remove(
                            index
                        );
                    }


                    // Glass card broke
                    if (
                        cardsToDestroy
                            .contains(
                                playedCard
                            )
                    ) {

                        GameSession
                            .getInstance()
                            .getCurrentDeck()
                            .remove(
                                playedCard
                            );
                    }
                }


                selectedCards.clear();

                handsLeft--;


                if (handsLabel != null) {

                    handsLabel.setText(
                        String.valueOf(
                            handsLeft
                        )
                    );
                }


                // Render remaining cards
                renderCards(currentHand);

                updateHandInfoDisplay();


                // =================================================
                // VICTORY
                // =================================================

                if (
                    currentScore
                    >=
                    targetScore
                ) {

                    if (gameOverPopup != null) {

                        gameOverPopup
                            .setVisible(true);
                    }


                    if (
                        gameOverTitleLabel
                        != null
                    ) {

                        gameOverTitleLabel
                            .setText(
                                "Victory!"
                            );
                    }


                    if (winButtonsBox != null) {

                        winButtonsBox
                            .setVisible(true);

                        winButtonsBox
                            .setManaged(true);
                    }


                    if (
                        endGameButton
                        != null
                    ) {

                        endGameButton
                            .setVisible(false);

                        endGameButton
                            .setManaged(false);
                    }


                    int excess =
                        currentScore
                        -
                        targetScore;


                    if (excess > 0) {

                        GameSession
                            .getInstance()
                            .addCoins(
                                excess
                            );


                        if (
                            finalScoreLabel
                            != null
                        ) {

                            finalScoreLabel.setText(
                                "Total Score Achieved: "
                                + currentScore
                                + "\nCoins Earned: "
                                + excess
                            );
                        }

                    } else {

                        if (
                            finalScoreLabel
                            != null
                        ) {

                            finalScoreLabel.setText(
                                "Total Score Achieved: "
                                + currentScore
                            );
                        }
                    }


                // =================================================
                // DEFEAT
                // =================================================

                } else if (
                    handsLeft <= 0
                ) {

                    if (
                        gameOverPopup
                        != null
                    ) {

                        gameOverPopup
                            .setVisible(true);
                    }


                    if (
                        gameOverTitleLabel
                        != null
                    ) {

                        gameOverTitleLabel
                            .setText(
                                "Defeated"
                            );
                    }


                    if (winButtonsBox != null) {

                        winButtonsBox
                            .setVisible(false);

                        winButtonsBox
                            .setManaged(false);
                    }


                    if (
                        endGameButton
                        != null
                    ) {

                        endGameButton
                            .setVisible(true);

                        endGameButton
                            .setManaged(true);
                    }


                    if (
                        finalScoreLabel
                        != null
                    ) {

                        finalScoreLabel.setText(
                            "Total Score Achieved: "
                            + currentScore
                            + " / "
                            + targetScore
                        );
                    }
                }


                // Animation finished
                isPlayingHand = false;
            }
        );
    }


    // =========================================================
    // DISCARD
    // =========================================================

    @FXML
    private void handleDiscard(
            ActionEvent event) {

        if (
            selectedCards.isEmpty()
            ||
            discardsLeft <= 0
            ||
            isPlayingHand
        ) {
            return;
        }


        System.out.println(
            "Discard clicked! Selected cards: "
            + selectedCards.size()
        );


        // Put selected cards back into deck
        remainingDeck.addAll(
            selectedCards
        );

        Collections.shuffle(
            remainingDeck
        );


        // Replace each selected card
        // in its original position
        for (Card discardedCard :
                selectedCards) {

            int index =
                currentHand.indexOf(
                    discardedCard
                );

            if (
                index != -1
                &&
                !remainingDeck.isEmpty()
            ) {

                currentHand.set(
                    index,
                    remainingDeck.remove(0)
                );

            } else if (index != -1) {

                currentHand.remove(
                    index
                );
            }
        }


        selectedCards.clear();

        discardsLeft--;


        if (discardsLabel != null) {

            discardsLabel.setText(
                String.valueOf(
                    discardsLeft
                )
            );
        }


        if (
            discardsLeft <= 0
            &&
            discardButton != null
        ) {

            discardButton.setDisable(
                true
            );
        }


        renderCards(currentHand);

        updateHandInfoDisplay();
    }


    // =========================================================
    // TAROT BUTTON STATE
    // =========================================================

    private void updateTarotButtonState() {

        if (useTarotButton == null) {
            return;
        }


        if (selectedTarot == null) {

            useTarotButton
                .setVisible(false);

            return;
        }


        int requiredTargets =
            selectedTarot.targetCount();


        if (requiredTargets > 0) {

            if (
                selectedCards.size()
                ==
                requiredTargets
            ) {

                useTarotButton
                    .setVisible(true);

            } else {

                useTarotButton
                    .setVisible(false);
            }

        } else {

            useTarotButton
                .setVisible(true);
        }
    }


    // =========================================================
    // USE TAROT
    // =========================================================

    @FXML
    private void handleUseTarot(
            ActionEvent event) {

        if (
            selectedTarot == null
            ||
            isPlayingHand
        ) {
            return;
        }


        GameSession session =
            GameSession.getInstance();

        boolean used = false;


        switch (
            selectedTarot.effectType()
        ) {

            case GAIN_MONEY:

                int currentCoins =
                    session.getCoins();

                int gain =
                    Math.min(
                        currentCoins,
                        20
                    );

                if (
                    gain == 0
                    &&
                    currentCoins == 0
                ) {

                    gain = 5;
                }

                session.addCoins(gain);

                used = true;

                break;


            case ENHANCE_CHIPS:

                for (Card c :
                        selectedCards) {

                    c.setEnhancement(
                        Card.Enhancement.BONUS
                    );
                }

                used = true;

                break;


            case ENHANCE_MULTI:

                for (Card c :
                        selectedCards) {

                    c.setEnhancement(
                        Card.Enhancement.MULT
                    );
                }

                used = true;

                break;


            case ENHANCE_LUCKY:

                for (Card c :
                        selectedCards) {

                    c.setEnhancement(
                        Card.Enhancement.LUCKY
                    );
                }

                used = true;

                break;


            case ENHANCE_WILD:

                for (Card c :
                        selectedCards) {

                    c.setEnhancement(
                        Card.Enhancement.WILD
                    );
                }

                used = true;

                break;


            case ENHANCE_STEEL:

                for (Card c :
                        selectedCards) {

                    c.setEnhancement(
                        Card.Enhancement.STEEL
                    );
                }

                used = true;

                break;


            case ENHANCE_GLASS:

                for (Card c :
                        selectedCards) {

                    c.setEnhancement(
                        Card.Enhancement.GLASS
                    );
                }

                used = true;

                break;


            case DESTROY_CARD:

                for (Card c :
                        selectedCards) {

                    currentHand.remove(c);

                    session
                        .getCurrentDeck()
                        .remove(c);
                }

                used = true;

                break;


            case SPAWN_JOKER:

                if (
                    session
                        .getActiveJokers()
                        .size()
                    < 5
                ) {

                    List<Joker> jokers =
                        new ArrayList<>(
                            JokerRegistry.JOKERS
                        );

                    Collections.shuffle(
                        jokers
                    );

                    session
                        .getActiveJokers()
                        .add(
                            jokers.get(0)
                        );

                    session
                        .getOwnedJokers()
                        .add(
                            jokers.get(0)
                        );

                    used = true;
                }

                break;


            case SPAWN_TAROT:

                if (
                    session
                        .getActiveTarots()
                        .size()
                    < 5
                ) {

                    List<Tarot> tarots =
                        new ArrayList<>(
                            TarotRegistry.TAROTS
                        );

                    Collections.shuffle(
                        tarots
                    );

                    Tarot newTarot =
                        tarots.get(0);

                    session
                        .getOwnedTarots()
                        .add(
                            newTarot
                        );

                    session
                        .getActiveTarots()
                        .add(
                            newTarot
                        );

                    used = true;
                }

                break;
        }


        if (used) {

            session
                .getOwnedTarots()
                .remove(
                    selectedTarot
                );

            session
                .getActiveTarots()
                .remove(
                    selectedTarot
                );

            selectedTarot = null;

            selectedCards.clear();

            session.saveRunToDatabase();


            // Re-render everything
            renderTarots(
                session.getActiveTarots()
            );

            renderJokers(
                session.getActiveJokers()
            );

            renderCards(
                currentHand
            );

            updateTarotButtonState();
        }
    }


    // =========================================================
    // HAND INFORMATION
    // =========================================================

    private void updateHandInfoDisplay() {

        if (selectedCards.isEmpty()) {

            handInfoBox.setVisible(false);

            return;
        }


        HandResult bestHand =
            HandEvaluator
                .evaluateSelectedCards(
                    selectedCards
                );


        if (bestHand != null) {

            handInfoBox.setVisible(true);

            handNameLabel.setText(
                bestHand.handName()
                + " lvl.1"
            );


            int baseChips = 0;


            for (Card c :
                    bestHand.cardsUsed()) {

                baseChips +=
                    c.points();
            }


            int[] calculated =
                calculateScoreWithJokers(
                    bestHand,
                    discardsLeft,
                    baseChips,
                    bestHand.mult()
                );


            baseChipsLabel.setText(
                String.valueOf(
                    calculated[0]
                )
            );

            multiplierLabel.setText(
                String.valueOf(
                    calculated[1]
                )
            );

        } else {

            handInfoBox.setVisible(false);
        }
    }
}