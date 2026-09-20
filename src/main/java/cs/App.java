package cs;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * JavaFX Application.
 */
public class App extends Application {

    private static Scene scene;
    private static StackPane rootContainer;
    private static MediaPlayer bgMediaPlayer;

    @Override
    public void start(Stage stage) throws IOException {
        rootContainer = new StackPane();
        rootContainer.getChildren().add(new SwirlBackground());
        
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("home.fxml"));
        Parent root = fxmlLoader.load();
        rootContainer.getChildren().add(root);
        
        scene = new Scene(rootContainer, 1920, 1000);

        stage.setTitle("Carbon Draw - The Roguelike Deckbuilder");
        stage.setScene(scene);

        startBackgroundMusic();

        stage.show();
    }

    private void startBackgroundMusic() {
        try {
            java.net.URL resource = App.class.getResource("/cs/audio/game_bg.mp3");
            if (resource != null) {
                Media bgMedia = new Media(resource.toString());
                bgMediaPlayer = new MediaPlayer(bgMedia);
                bgMediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                bgMediaPlayer.setVolume(0.5); // Set a reasonable volume
                bgMediaPlayer.play();
            } else {
                System.out.println("Background music file not found!");
            }
        } catch (Exception e) {
            System.err.println("Could not load background music:");
            e.printStackTrace();
        }
    }

    private static boolean isMuted = false;

    public static boolean toggleBackgroundMusic() {
        if (bgMediaPlayer != null) {
            if (isMuted) {
                bgMediaPlayer.play();
                isMuted = false;
            } else {
                bgMediaPlayer.pause();
                isMuted = true;
            }
        }
        return isMuted;
    }

    public static boolean isMusicMuted() {
        return isMuted;
    }

    public static void setRoot(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        Parent newRoot = fxmlLoader.load();
        
        if (rootContainer.getChildren().size() > 1) {
            rootContainer.getChildren().set(1, newRoot);
        } else {
            rootContainer.getChildren().add(newRoot);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
