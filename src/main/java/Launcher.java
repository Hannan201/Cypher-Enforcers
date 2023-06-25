import commands.Command;
import commands.SwitchToDarkMode;
import commands.SwitchToHighContrastMode;
import commands.managers.ThemeSwitcher;
import data.Database;
import data.Storage;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import views.*;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is responsible for launching the backup
 * code manager application.
 */

public class Launcher extends Application {

    // File for storing the user's accounts and codes.
    private static final String PATH_TO_USERS_FILE = "src/main/java/data/users.ser";

    // File for storing the user's usernames, passwords, and theme
    // preference.
    private static final String PATH_TO_CONFIG_FILE = "src/main/java/data/config.ser";

    /**
     * Entry point for this application.
     *
     * @param args Any additional arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The main entry point for this JavaFX application.
     *
     * @param stage Primary stage for this application for
     *              which the scene can be set.
     */
    @Override
    public void start(Stage stage) {
        stage.setOnCloseRequest(windowEvent -> Database.disconnect());

        View view = loadView();
        Scene scene = new Scene(view.getRoot());
        adjustTheme();
        scene.getStylesheets().add(view.getCurrentThemePath());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Return the correct view based on if the user left the application
     * logged in or logged out.
     *
     * @return Correct view to load.
     */
    private static View loadView() {
        if (Storage.getToken() != null) {
            return AccountView.getInstance();
        }

        return HomePageView.getInstance();
    }

    /**
     * Adjust the theme of the application based on the user's setting
     * before they quit the application without logging out.
     */
    private static void adjustTheme() {
        if (Storage.getToken() != null) {
            String preferredTheme = Database.getTheme(Storage.getToken());
            if (preferredTheme != null) {

                // Switch to the respective theme.
                if (!preferredTheme.equals("Light")) {
                    List<View> views = new ArrayList<>(
                            List.of(
                                    HomePageView.getInstance(),
                                    SignInView.getInstance(),
                                    SignUpView.getInstance(),
                                    AccountView.getInstance(),
                                    AddAccountView.getInstance(),
                                    CodeView.getInstance(),
                                    SettingsView.getInstance()
                            )
                    );

                    Command command;

                    if (preferredTheme.equals("High Contrast")) {
                        command = new SwitchToHighContrastMode(views);
                        ThemeSwitcher switcher = new ThemeSwitcher(command);
                        switcher.switchTheme();
                    } else if (preferredTheme.equals("Dark")) {
                        command = new SwitchToDarkMode(views);
                        ThemeSwitcher switcher = new ThemeSwitcher(command);
                        switcher.switchTheme();
                    }
                }

                ((AccountView) AccountView.getInstance()).getAccountViewController()
                        .addAccounts(Database.getAccounts(Storage.getToken()));
            }
        }
    }
}
