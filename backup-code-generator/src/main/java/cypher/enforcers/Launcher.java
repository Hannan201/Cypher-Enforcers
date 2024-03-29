package cypher.enforcers;

import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cypher.enforcers.utilities.Utilities;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import cypher.enforcers.views.*;

import java.io.IOException;

/**
 * This class is responsible for launching the backup
 * code manager application.
 */
public class Launcher extends Application {

    /**
     * Constructor for the Launcher.
     * No reason to create an instance of this object, instead use the static
     * method provided.
     * <br>
     * Mainly here to avoid warnings.
     */
    public Launcher() {

    }

    /** Logger used for logging. */
    private static final Logger logger = LoggerFactory.getLogger(Launcher.class);

    /**
     * Entry point for this application.
     *
     * @param args Any additional arguments.
     */
    public static void main(String[] args) {
        try {
            logger.info("Launching application...");
            launch(args);
        } catch (Exception e) {
            logger.error("Error occurred, shutting down. Cause: ", e);
        }
    }

    /**
     * The main entry point for this JavaFX application.
     *
     * @param stage Primary stage for this application for
     *              which the scene can be set.
     */
    @Override
    public void start(Stage stage) {
        try {
            Utilities.prepare(stage);
            View view = HomePageView.getInstance();
            Scene scene = new Scene(view.getRoot());
            scene.getStylesheets().add(view.getCurrentThemePath());
            stage.setScene(scene);
            stage.show();
            logger.info("Displaying application window.");
        } catch (Exception e) {
            Utilities.onException(stage, e);
        }
    }
}