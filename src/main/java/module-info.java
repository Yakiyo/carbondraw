module cs.cdraw {
    // 1. JavaFX UI requirements
    requires transitive javafx.controls;
    requires transitive javafx.graphics;
    requires javafx.fxml;
    
    // 2. Database requirements
    requires java.sql;

    // 3. Give JavaFX permission to read and interact with your 'cs' package
    opens cs to javafx.fxml;
    exports cs;
}
