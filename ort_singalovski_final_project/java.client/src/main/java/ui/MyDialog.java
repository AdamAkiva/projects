package ui;

import interfaces.IContainer;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import javax.annotation.Nullable;
import java.util.logging.Logger;

/**
 * A class that extends the javafx Dialog class which allows
 * for a custom made dialog window
 */
public class MyDialog extends Dialog {

    // Set the logger for the this file
    private static final Logger logger = Logger.getLogger(MyDialog.class.getName());

    // Variables which holds strings
    private static final String GOT_IT = "Got it";
    private static final String OK = "Ok";
    private static final String DEFAULT_ERROR_MESSAGE = "Something went wrong please try again later";

    /**
     * A constructor for a custom made dialog with custom title, title text, body text and expandable content
     *
     * @param title             String that indicates the title of the dialog
     * @param contentTextTitle  String that indicates the text in the top of the dialog
     * @param contentTextBody   String that indicates the text in the body of the dialog
     * @param expandableContent Nullable Node that holds a control for an expandable content for the dialog
     */
    public MyDialog(String title, String contentTextTitle, String contentTextBody, @Nullable Node expandableContent) {
        this.setTitle(title);
        this.setHeaderText(null);

        // Set the parameters in a GridPane structure
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 20, 10, 20));
        Text titleText = new Text(contentTextTitle);
        titleText.setFont(Font.font(null, FontWeight.BOLD, 18));
        titleText.setUnderline(true);
        grid.add(titleText, 0, 0);
        grid.add(new Label(contentTextBody), 0, 1);
        this.getDialogPane().setContent(grid);
        if (expandableContent != null) {
            grid.add(expandableContent, 0, 2);
        }
        this.getDialogPane().getButtonTypes().add(new ButtonType(GOT_IT, ButtonBar.ButtonData.OK_DONE));
    }

    /**
     * A constructor for an error dialog with an error that is received from the server
     *
     * @param container Container object which that holds the error that needs to be displayed to the user
     * @param title     String that indicates the title of the dialog
     */
    public MyDialog(IContainer container, String title) {
        this.setTitle(title);
        this.setHeaderText(null);

        // Set the parameters in a GridPane structure
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(10, 20, 10, 20));
        // Create a new label which displayed the error if it's length is greater than 0
        grid.add(new Label(container.getUserMessage().length() > 0 ? container.getUserMessage() : DEFAULT_ERROR_MESSAGE), 0, 0);
        this.getDialogPane().setContent(grid);

        this.getDialogPane().getButtonTypes().add(new ButtonType(OK, ButtonBar.ButtonData.OK_DONE));
    }

}
