package views;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import models.Customer;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class AddProductView extends BaseView {
    
    private final Set<Customer> customers;
    
    private final TextField tfId;
    private final TextField tfProductName;
    private final TextField tfProductShopPrice;
    private final TextField tfProductCustomerPrice;
    private final ListView<String> lvCustomers;
    private final Button btnSubmit;
    
    public AddProductView(Set<Customer> customers) {
        this.customers = customers;
        tfId = new TextField();
        tfProductName = new TextField();
        tfProductShopPrice = new TextField();
        tfProductCustomerPrice = new TextField();
        lvCustomers = new ListView<>();
        btnSubmit = new Button("Submit");
    }
    
    public Parent buildView() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(25);
        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        Text tTitle = new Text("Please add a product: ");
        tTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        hBox.getChildren().addAll(buildForm(), buildListView());
        vBox.getChildren().addAll(tTitle, hBox, btnSubmit);
        
        return vBox;
    }
    
    private GridPane buildForm() {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 5, 5, 10));
        pane.setVgap(10);
        pane.setHgap(5);
        pane.setAlignment(Pos.CENTER);
        
        Text tId = new Text("Product ID: ");
        Text tProductName = new Text("Product Name: ");
        Text tProductShopPrice = new Text("Shop Price: ");
        Text tCustomerPrice = new Text("Customer Price: ");
        
        pane.add(tId, 0, 1);
        pane.add(tfId, 1, 1);
        pane.add(tProductName, 0, 2);
        pane.add(tfProductName, 1, 2);
        pane.add(tProductShopPrice, 0, 3);
        pane.add(tfProductShopPrice, 1, 3);
        pane.add(tCustomerPrice, 0, 4);
        pane.add(tfProductCustomerPrice, 1, 4);
        
        return pane;
    }
    
    private GridPane buildListView() {
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 5, 5, 10));
        pane.setVgap(10);
        pane.setHgap(5);
        pane.setAlignment(Pos.CENTER);
        Text tAttachCustomer = new Text("Attach a customer: ");
        List<String> customersInfo = new ArrayList<>();
        for (Customer customer : customers) {
            customersInfo.add(String.format("Name: %s, Phone Number: %s", customer.getName(), customer.getPhoneNumber()));
        }
        ObservableList<String> obsNameList = FXCollections.observableArrayList(customersInfo);
        lvCustomers.setItems(obsNameList);
        lvCustomers.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        lvCustomers.getSelectionModel().select(0);
        
        pane.add(tAttachCustomer, 0, 0);
        pane.add(lvCustomers, 0, 1);
        
        return pane;
    }
    
    public TextField getTfId() {
        return tfId;
    }
    
    public TextField getTfProductName() {
        return tfProductName;
    }
    
    public TextField getTfProductShopPrice() {
        return tfProductShopPrice;
    }
    
    public TextField getTfProductCustomerPrice() {
        return tfProductCustomerPrice;
    }
    
    public ListView<String> getLvCustomers() {
        return lvCustomers;
    }
    
    public Button getBtnSubmit() {
        return btnSubmit;
    }
}
