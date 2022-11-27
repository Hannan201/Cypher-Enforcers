package views;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * This class is responsible for displaying a view
 * to change the settings for this application. Such
 * as the font and theme.
 */

public class SettingsView extends View {

    // An instance for this settings view.
    private static View firstInstance = null;
    private StackPane layout;

    /**
     * Create a new settings view.
     */
    private SettingsView() {

    }

    /**
     * Return the instance of this settings view.
     *
     * @return Instance of this settings view.
     */
    public static View getInstance() {
        if (firstInstance == null) {
            firstInstance = new SettingsView();
        }

        return firstInstance;
    }

    /**
     * Initialise the UI elements for this settings
     * view.
     */
    @Override
    protected void initUI() {
        ToggleButton TB1 = new ToggleButton();
    }

    /**
     * Switch this settings view to light mode.
     */
    @Override
    public void switchToLightMode() {

    }

    /**
     * Switch this settings view to dark mode.
     */
    @Override
    public void switchToDarkMode() {

    }

    /**
     * Switch this settings view to high contrast mode.
     */
    @Override
    public void switchToHighContrastMode() {

    }

    public Parent getRoot() {
        throw new UnsupportedOperationException();
    }
}
