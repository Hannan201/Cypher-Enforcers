package views.utilities;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import user.Account;
import views.AccountView;
import views.CodeView;

import java.io.IOException;

public class AccountCell extends ListCell<Account> {

    @FXML
    private ImageView logo;

    @FXML
    private Label platformName;

    @FXML
    private Label username;

    @FXML
    private HBox cell;

    @FXML
    private VBox textbox;

    public AccountCell() {
        loadFXML();
    }

    private void loadFXML() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("view/AccountCell.fxml"));
            loader.setController(this);
            loader.setRoot(this);
            loader.load();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Creates an AccountCell which represents an account visually
     * within the listView.
     *
     * @param item the account that is being passed in.
     * @param empty
     */
    @Override
    protected void updateItem(Account item, boolean empty) {
        super.updateItem(item, empty);

        // deals with the case of the account being null or the boolean being empty

        if(empty || item == null) {
            setText(null);
            setContentDisplay(ContentDisplay.TEXT_ONLY);
        }
        else {

            // checks if the account's platform is either Google, Discord, Shopify, or Github
            // and if so, adds a custom icon to the account

            if (Account.getIcons().containsKey(item.getSocialMediaType().toLowerCase())) {
                String path = Account.getIcons().get(item.getSocialMediaType().toLowerCase());
                Image image = new Image(path);
                logo.setImage(image);
                logo.setFitWidth(50);
                logo.setFitHeight(50);
                cell.getChildren();
                HBox.setHgrow(cell, Priority.NEVER);

            // adds a default logo for the account if the it is not Discord, Google,
            // Github, or Shopify

            } else if (! Account.getIcons().containsKey(item.getSocialMediaType().toLowerCase())) {
                String path = Account.class.getClassLoader().getResource("images/icons8-app-100.png").toExternalForm();;
                Image image = new Image(path);
                logo.setImage(image);
                logo.setFitWidth(50);
                logo.setFitHeight(50);
                cell.getChildren();
                HBox.setHgrow(cell, Priority.NEVER);
            }
            setWrapText(true); // wraps the text in the ListCell to avoid long text

            platformName.setText(item.getSocialMediaType());
            platformName.setMaxWidth(200); // limits the amount of space that a username can take in the AccountCell
            username.setText(item.getName());
            platformName.setMaxWidth(200); // limits the amount of space that a platform can take in the AccountCell
            HBox.setHgrow(textbox, Priority.ALWAYS);
            cell.setSpacing(10);

            // If the AccountCell is double clicked, then this handle method will transition
            // the view from the AccountsView to the CodeView

            setOnMouseClicked(mouseClickedEvent -> {
                if (mouseClickedEvent.getButton().equals(MouseButton.PRIMARY) && mouseClickedEvent.getClickCount() == 2) {
                    Scene scene = AccountView.getInstance().getRoot().getScene();
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(CodeView.getInstance().getCurrentThemePath());
                    scene.setRoot(CodeView.getInstance().getRoot());
                }
            });

            setText(null);
            setGraphic(cell);

            setContentDisplay(ContentDisplay.GRAPHIC_ONLY); // displays the AccountCell accordingly
        }
    }
}