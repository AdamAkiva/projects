package views;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


/**
 * A class for the AddCustomer window
 */
public class AddCustomerView extends BaseView {
    
    private final TextField tfCustomerName;
    private final TextField tfCustomerPhoneNumber;
    private final CheckBox cbDiscount;
    private final Button btnSubmit;
    
    public AddCustomerView() {
        this.tfCustomerName = new TextField();
        this.tfCustomerPhoneNumber = new TextField();
        this.cbDiscount = new CheckBox("Receive new discount offers?");
        this.btnSubmit = new Button("Submit");
    }
    
    public Parent buildView() {
        final VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(20);
        final ObservableList<Node> list = vBox.getChildren();
        
        final Text tTitle = new Text("Please add a customer: ");
        tTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        
        list.addAll(tTitle, buildForm(), btnSubmit);
        
        return vBox;
    }
    
    private GridPane buildForm() {
        final GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 5, 5, 10));
        pane.setVgap(10);
        pane.setHgap(5);
        pane.setAlignment(Pos.CENTER);
        
        final Text tCustomerName = new Text("Customer Name:");
        final Text tCustomerPhoneNumber = new Text("Customer Phone Number:");
        
        cbDiscount.setSelected(true);
        
        pane.add(tCustomerName, 0, 0);
        pane.add(tfCustomerName, 1, 0);
        pane.add(tCustomerPhoneNumber, 0, 1);
        pane.add(tfCustomerPhoneNumber, 1, 1);
        pane.add(cbDiscount, 0, 2);
        
        return pane;
    }
    
    public TextField getTfCustomerName() {
        return tfCustomerName;
    }
    
    public TextField getTfCustomerPhoneNumber() {
        return tfCustomerPhoneNumber;
    }
    
    public CheckBox getCbDiscount() {
        return cbDiscount;
    }
    
    public Button getBtnSubmit() {
        return btnSubmit;
    }
}
