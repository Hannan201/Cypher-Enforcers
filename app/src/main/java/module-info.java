module cypher.enforcers {
    // JavaFX.
    requires javafx.controls;
    requires javafx.fxml;

    // Logging API.
    requires org.slf4j;

    // ValidatorFX
    requires net.synedra.validatorfx;

    opens cypher.enforcers to javafx.fxml;
    exports cypher.enforcers;
}