package views;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class DiscountView extends BaseView {
    
    private final Text tCustomer;
    private final Button btnShowCustomers;
    
    public DiscountView() {
        tCustomer = new Text();
        btnShowCustomers = new Button("Show Customers");
    }
    
    public Parent buildView() {
        
        VBox vBox = new VBox(35);
        vBox.setAlignment(Pos.CENTER);
        ObservableList<Node> vBoxChildren = vBox.getChildren();
        
        Text tTitle = new Text("Customers that would like to\nreceive discount offers: ");
        tTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        tCustomer.setFont(Font.font("Arial", FontWeight.BOLD, 16));
        tCustomer.setFill(Color.DARKGREEN);
        
        vBoxChildren.addAll(tTitle, btnShowCustomers, tCustomer);
        
        return vBox;
    }
    
    public Button getBtnShowCustomers() {
        return btnShowCustomers;
    }
    
    public Text getTCustomer() {
        return tCustomer;
    }
}
