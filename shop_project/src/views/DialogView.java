package views;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class DialogView {
    
    public static Stage buildDialog(Modality modality, Stage owner, String title, String message, boolean closeOwnerWindow) {
        final Stage dialog = new Stage();
        dialog.initModality(modality);
        dialog.initOwner(owner);
        dialog.setTitle(title);
        VBox errorVBox = new VBox(15);
        errorVBox.setAlignment(Pos.CENTER);
        Text errorText = new Text(message);
        errorText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        errorVBox.getChildren().addAll(errorText, closeOwnerWindow ? submitButton(owner) : submitButton());
        Scene scene = new Scene(errorVBox, 300, 200);
        dialog.setScene(scene);
        return dialog;
    }
    
    private static Button submitButton() {
        final Button btnSubmit = new Button("Ok");
        btnSubmit.setOnAction(actionEvent -> ((Node) (actionEvent.getSource())).getScene().getWindow().hide());
        return btnSubmit;
    }
    
    private static Button submitButton(Window owner) {
        final Button btnSubmit = new Button("Ok");
        btnSubmit.setOnAction(actionEvent -> {
            ((Node) (actionEvent.getSource())).getScene().getWindow().hide();
            owner.hide();
        });
        return btnSubmit;
    }
}
