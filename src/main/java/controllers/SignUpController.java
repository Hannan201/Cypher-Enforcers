package controllers;

import data.Database;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;
import views.AccountView;
import views.HomePageView;
import views.View;

import java.net.URL;
import java.util.ResourceBundle;

public class SignUpController implements Initializable {

    private final Validator validator = new Validator();

    @FXML
    private BorderPane background;

    @FXML
    private Label title;

    @FXML
    public Button signUp;

    @FXML
    public VBox box;

    @FXML
    private HBox initialPasswordRow;

    @FXML
    private Label initialPasswordLabel;

    @FXML
    public PasswordField initialPassword;

    @FXML
    private HBox verifiedPasswordRow;

    @FXML
    private Label verifiedPasswordLabel;

    @FXML
    public PasswordField verifiedPassword;

    @FXML
    private HBox initialUsernameRow;

    @FXML
    private Label initialUsernameLabel;

    @FXML
    public TextField initialUsername;

    @FXML
    private HBox verifiedUsernameRow;

    @FXML
    private Label verifiedUsernameLabel;

    @FXML
    public TextField verifiedUsername;

    // Don't want the font to be too large.
    private final int MAX_FONT_SIZE = 50;

    // To change the font size of the title.
    private final ObjectProperty<Font> titleFontTracking = new SimpleObjectProperty<>(Font.getDefault());

    // To dynamically calculate the font size needed, of the title.
    private final DoubleProperty titleFontSize = new SimpleDoubleProperty();

    // To dynamically calculate the padding needed, of the title
    // based on the font size.
    private final ObjectProperty<Insets> titlePaddingSize = new SimpleObjectProperty<>(new Insets(5, 0, 5, 0));

    // To dynamically calculate the font size needed, of the labels.
    private final ObjectProperty<Font> labelFontSize = new SimpleObjectProperty<>(Font.getDefault());

    // To dynamically calculate the width/height size needed,
    // of the password and username text fields.
    private final DoubleProperty fieldWidthSize = new SimpleDoubleProperty();
    private final DoubleProperty fieldHeightSize = new SimpleDoubleProperty();

    // To dynamically calculate the padding needed, of the button
    // based on the font size.
    private final ObjectProperty<Insets> buttonPaddingSize = new SimpleObjectProperty<>(new Insets(5, 38, 5, 38.5));

    // To dynamically calculate the padding needed, of the labels
    // based on the font size.
    private final ObjectProperty<Insets> initialUsernameLabelPaddingSize = new SimpleObjectProperty<>(new Insets(0, 37, 0, 0));
    private final ObjectProperty<Insets> verifiedUsernameLabelPaddingSize = new SimpleObjectProperty<>(new Insets(0, 36, 0, 0));
    private final ObjectProperty<Insets> initialPasswordLabelPaddingSize = new SimpleObjectProperty<>(new Insets(0, 82, 0, 0));
    private final ObjectProperty<Insets> verifiedPasswordLabelPaddingSize = new SimpleObjectProperty<>(new Insets(0, 40, 0, 0));

    public void initialize(URL url, ResourceBundle resourceBundle) {
        box.sceneProperty().addListener(((observableValue, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener(((observableValue1, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        newWindow.setOnCloseRequest((windowEvent -> {
                            initialUsername.clear();
                            initialPassword.clear();
                            verifiedUsername.clear();
                            verifiedPassword.clear();
                        }));
                    }
                }));
            }
        }));

        signUp = new Button("Sign Up");
        signUp.setPrefSize(Button.USE_COMPUTED_SIZE, Button.USE_COMPUTED_SIZE);
        signUp.fontProperty().bind(labelFontSize);
        signUp.paddingProperty().bind(buttonPaddingSize);
        // creates the decorated button
        TooltipWrapper<Button> createAccountWrapper = new TooltipWrapper<>(
                signUp,
                validator.containsErrorsProperty(),
                Bindings.concat("Cannot sign up:\n", validator.createStringBinding()));
        signUp.setOnAction(this::handleSignUp);
        box.getChildren().add(createAccountWrapper);

        // checks if the username field is empty

        validator.createCheck()
                .withMethod(c -> {
                    if (initialUsername.getText().equals("")){
                        c.error("Please add your username!");
                    }
                })
                .dependsOn("initialUsername", initialUsername.textProperty())
                .decorates(initialUsername)
                .immediate()
        ;

        // tells the user to add their verified username

        validator.createCheck()
                .withMethod(c -> {
                    if (verifiedUsername.getText().equals("") && !initialUsername.getText().equals("")){
                        c.error("Please verify your username!");
                    }
                })
                .dependsOn("verifiedUsername", verifiedUsername.textProperty())
                .decorates(verifiedUsername)
                .immediate()
        ;

        // checks if both usernames are the same

        validator.createCheck()
                .withMethod(c -> {
                    if (! initialUsername.getText().equals(verifiedUsername.getText())){
                        c.error("Your usernames do not match, please try again.");
                    }
                })
                .dependsOn("initialUsername", initialUsername.textProperty())
                .dependsOn("verifiedUsername", verifiedUsername.textProperty())
                .decorates(initialUsername)
                .decorates(verifiedUsername)
                .immediate()
        ;

        // checks if the password was inputted

        validator.createCheck()
                .withMethod(c -> {
                    if (initialPassword.getText().equals("")){
                        c.error("Please add your password!");
                    }
                })
                .dependsOn("initialPassword", initialPassword.textProperty())
                .decorates(initialPassword)
                .immediate()
        ;

        // checks if the verified password was inputted
        // after the user puts their normal password

        validator.createCheck()
                .withMethod(c -> {
                    if (verifiedPassword.getText().equals("") && !initialPassword.getText().equals("")){
                        c.error("Please verify your password!");
                    }
                })
                .dependsOn("verifiedPassword", verifiedPassword.textProperty())
                .decorates(verifiedPassword)
                .immediate();

        // checks if both passwords inputted match each other

        validator.createCheck()
                .withMethod(c -> {
                    if (! initialPassword.getText().equals(verifiedPassword.getText())){
                        c.error("Your passwords do not match, please try again.");
                    }
                })
                .dependsOn("initialPassword", initialPassword.textProperty())
                .dependsOn("verifiedPassword", verifiedPassword.textProperty())
                .decorates(initialPassword)
                .decorates(verifiedPassword)
                .immediate();

        // checks if the Account is already registered

        validator.createCheck()
                .withMethod(c -> {
                    if (Database.checkUsername(initialUsername.getText())){
                        c.error("This account is already registered.");
                    }
                })
                .dependsOn("initialUsername", initialUsername.textProperty())
                .dependsOn("verifiedUsername", verifiedUsername.textProperty())
                .decorates(initialUsername)
                .decorates(verifiedUsername)
                .immediate();

        titleFontSize.bind(background.widthProperty()
                .add(background.heightProperty())
                .divide(1280 + 720)
                .multiply(100)
                .multiply(24.0 / 34.0)
        );

        background.widthProperty().addListener(((observableValue, number, t1) -> {
            double result = Math.min(MAX_FONT_SIZE, titleFontSize.doubleValue());
            titleFontTracking.set(Font.font(result));
            titlePaddingSize.set(new Insets(result * (5.0 / 24.0), 0, result * (5.0 / 24.0), 0));
            labelFontSize.set(Font.font(result * 0.625));
            fieldWidthSize.set(result * (149.0 / 24.0));
            fieldHeightSize.set(result * (31.0 / 24.0));
            buttonPaddingSize.set(new Insets(result * (5.0 / 24.0), result * (38.0 / 24.0), result * (5.0 / 24.0), result * (38.5 / 24.0)));
            initialUsernameLabelPaddingSize.set(new Insets(0, result * (37.0 / 24.0), 0, 0));
            verifiedUsernameLabelPaddingSize.set(new Insets(0, result * 1.5 , 0, 0));
            initialPasswordLabelPaddingSize.set(new Insets(0, result * (82.0 / 24.0), 0, 0));
            verifiedPasswordLabelPaddingSize.set(new Insets(0, result * (40.0 / 24.0), 0, 0));
            VBox.setMargin(initialUsernameRow, new Insets(result * (8.0 / 24.0), 0, result * (8.0 / 24.0), 0));
            VBox.setMargin(initialPasswordRow, new Insets(result * (11.0 / 24.0), 0, 0, 0));
            VBox.setMargin(verifiedPasswordRow, new Insets(result * (4.0 / 24.0), 0, result, 0));
        }));

        background.heightProperty().addListener(((observableValue, number, t1) -> {
            double result = Math.min(MAX_FONT_SIZE, titleFontSize.doubleValue());
            titleFontTracking.set(Font.font(result));
            titlePaddingSize.set(new Insets(result * (5.0 / 24.0), 0, result * (5.0 / 24.0), 0));
            labelFontSize.set(Font.font(result * 0.625));
            fieldWidthSize.set(result * (149.0 / 24.0));
            fieldHeightSize.set(result * (31.0 / 24.0));
            buttonPaddingSize.set(new Insets(result * (5.0 / 24.0), result * (38.0 / 24.0), result * (5.0 / 24.0), result * (38.5 / 24.0)));
            initialUsernameLabelPaddingSize.set(new Insets(0, result * (37.0 / 24.0), 0, 0));
            verifiedUsernameLabelPaddingSize.set(new Insets(0, result * 1.5 , 0, 0));
            initialPasswordLabelPaddingSize.set(new Insets(0, result * (82.0 / 24.0), 0, 0));
            verifiedPasswordLabelPaddingSize.set(new Insets(0, result * (40.0 / 24.0), 0, 0));
            VBox.setMargin(initialUsernameRow, new Insets(result * (8.0 / 24.0), 0, result * (8.0 / 24.0), 0));
            VBox.setMargin(initialPasswordRow, new Insets(result * (11.0 / 24.0), 0, 0, 0));
            VBox.setMargin(verifiedPasswordRow, new Insets(result * (4.0 / 24.0), 0, result, 0));
        }));

        title.fontProperty().bind(titleFontTracking);
        title.paddingProperty().bind(titlePaddingSize);

        initialUsername.prefWidthProperty().bind(fieldWidthSize);
        initialUsername.prefHeightProperty().bind(fieldHeightSize);
        verifiedUsername.prefWidthProperty().bind(fieldWidthSize);
        verifiedUsername.prefHeightProperty().bind(fieldHeightSize);

        initialPassword.prefWidthProperty().bind(fieldWidthSize);
        initialPassword.prefHeightProperty().bind(fieldHeightSize);
        verifiedPassword.prefWidthProperty().bind(fieldWidthSize);
        verifiedPassword.prefHeightProperty().bind(fieldHeightSize);

        initialUsername.fontProperty().bind(labelFontSize);
        verifiedUsername.fontProperty().bind(labelFontSize);

        initialPassword.fontProperty().bind(labelFontSize);
        verifiedPassword.fontProperty().bind(labelFontSize);

        initialUsernameLabel.fontProperty().bind(labelFontSize);
        verifiedUsernameLabel.fontProperty().bind(labelFontSize);

        initialPasswordLabel.fontProperty().bind(labelFontSize);
        verifiedPasswordLabel.fontProperty().bind(labelFontSize);

        initialUsernameLabel.paddingProperty().bind(initialUsernameLabelPaddingSize);
        verifiedUsernameLabel.paddingProperty().bind(verifiedUsernameLabelPaddingSize);

        initialPasswordLabel.paddingProperty().bind(initialPasswordLabelPaddingSize);
        verifiedPasswordLabel.paddingProperty().bind(verifiedPasswordLabelPaddingSize);

        VBox.setMargin(initialUsernameRow, new Insets(8, 0, 8, 0));
        VBox.setMargin(verifiedUsernameRow, new Insets(0, 0, 0, 0));
        VBox.setMargin(initialPasswordRow, new Insets(11, 0, 0, 0));
        VBox.setMargin(verifiedPasswordRow, new Insets(4.0, 0, 4.0 + 20, 0));
    }

    /**
     * A handle method tied to the Sign-Up button in the initialize method
     * which is responsible for transitioning from the Sign-Up view
     * to the Accounts view.
     *
     * @param e ActionEven that triggered this
     *          handle method.
     */
    public void handleSignUp(ActionEvent e) {
        // Switching theme (sample code), should be included in the Controller class event handlers
        // This does not work for popups because those need a new stage to be created

        // open pop up

        View.closeWindow(e);

        View.switchSceneTo(HomePageView.getInstance(), AccountView.getInstance());

        // Clear the attributes such that when the signs out
        // they do not have access to the credentials

        Database.registerUser(initialUsername.getText(), initialPassword.getText());

        initialUsername.clear();
        initialPassword.clear();
        verifiedUsername.clear();
        verifiedPassword.clear();
    }
}
