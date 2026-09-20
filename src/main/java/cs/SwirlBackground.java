package cs;

import javafx.animation.AnimationTimer;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SwirlBackground extends Pane {

    private long startTime;

    public SwirlBackground() {
        setStyle("-fx-background-color: #051014;"); // very dark base
        
        Circle c1 = new Circle(700, Color.web("#154734")); // Balatro green
        c1.setEffect(new GaussianBlur(300));
        
        Circle c2 = new Circle(800, Color.web("#0c2b1f")); // Dark green
        c2.setEffect(new GaussianBlur(400));
        
        Circle c3 = new Circle(600, Color.web("#2a9df4")); // Soft blue
        c3.setOpacity(0.4);
        c3.setEffect(new GaussianBlur(300));
        
        Circle c4 = new Circle(750, Color.web("#103b2e"));
        c4.setEffect(new GaussianBlur(350));
        
        getChildren().addAll(c1, c2, c3, c4);
        
        startTime = System.nanoTime();
        
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double time = (now - startTime) / 1_000_000_000.0;
                
                // Slow fluid movement
                c1.setCenterX(1920 / 2.0 + Math.sin(time * 0.3) * 600);
                c1.setCenterY(1000 / 2.0 + Math.cos(time * 0.2) * 400);
                
                c2.setCenterX(1920 / 2.0 + Math.cos(time * 0.25) * 500);
                c2.setCenterY(1000 / 2.0 + Math.sin(time * 0.35) * 300);
                
                c3.setCenterX(1920 / 2.0 + Math.sin(time * 0.4) * 700);
                c3.setCenterY(1000 / 2.0 + Math.cos(time * 0.15) * 500);
                
                c4.setCenterX(1920 / 2.0 + Math.cos(time * 0.1) * 800);
                c4.setCenterY(1000 / 2.0 + Math.sin(time * 0.45) * 350);
            }
        };
        timer.start();
    }
}
