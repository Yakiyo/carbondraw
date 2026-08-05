package cs;

/**
 * A separate launcher class that does not extend javafx.application.Application.
 * This prevents the "JavaFX runtime components are missing" error when running directly from an IDE.
 */
public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
