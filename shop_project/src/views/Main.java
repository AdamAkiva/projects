package views;

import controllers.AddCustomerController;
import controllers.AddProductController;
import controllers.DiscountController;
import controllers.RemoveProductController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Customer;
import models.CustomerModel;
import models.ProductModel;

import java.util.ArrayList;

import static etc.Constants.*;

public class Main extends Application {
    
    private final ProductModel productModel = ProductModel.getInstance(); // ProductModel instance
    private final CustomerModel customerModel = CustomerModel.getInstance(); // CustomerModel instance
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
        final Scene scene = new Scene(buildOptionMenu(stage), 400, 250);
        stage.setTitle("Shop");
        stage.setScene(scene);
        stage.show();
    }
    
    @Override
    public void stop() {
        Platform.exit();
    }
    
    /**
     * @param primaryStage JavaFX Stage object as the base window of the program
     *
     * @return JavaFX Parent object to be displayed as the main window of the program
     */
    public Parent buildOptionMenu(Stage primaryStage) {
        customerModel.addAll(productModel.getListOfCustomers());
        for (Customer customer : customerModel.getCustomers()) {
            if (customer.getDiscounts()) {
                customerModel.addObserver(customer);
            }
        }
        
        final GridPane gridPane = new GridPane();
        
        gridPane.setPadding(new Insets(10, 5, 5, 10));
        gridPane.setVgap(10);
        gridPane.setHgap(5);
        gridPane.setAlignment(Pos.CENTER);
        
        gridPane.add(buildAddCustomer(primaryStage), 0, 0);
        gridPane.add(buildAddProductButton(primaryStage), 0, 1);
        gridPane.add(buildRemoveProductButton(primaryStage), 0, 2);
        gridPane.add(buildPrintCustomersWithDiscountButton(primaryStage), 0, 3);
        gridPane.add(buildUndoButton(primaryStage), 0, 4);
        
        return gridPane;
    }
    
    /**
     * @param primaryStage JavaFX Stage which is the owner for this stage
     *
     * @return JavaFX Button which will open the AddCustomerView on user click
     */
    private Button buildAddCustomer(Stage primaryStage) {
        final Button btnAddCustomer = new Button("Add Customer");
        btnAddCustomer.setOnAction(actionEvent -> {
            final AddCustomerController addCustomerController = new AddCustomerController(customerModel);
            final AddCustomerView addCustomerView = (AddCustomerView) addCustomerController.getView();
            addCustomerView.attachButtonEvent(addCustomerView.getBtnSubmit(), addCustomerController.submitButtonAction(new ArrayList<TextField>() {{
                add(addCustomerView.getTfCustomerName());
                add(addCustomerView.getTfCustomerPhoneNumber());
            }}, addCustomerView.getCbDiscount()));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(primaryStage);
            stage.setTitle("Customer");
            stage.setScene(new Scene(addCustomerController.buildView(), 400, 250));
            stage.show();
        });
        return btnAddCustomer;
    }
    
    /**
     * @param primaryStage JavaFX Stage which is the owner for this stage
     *
     * @return JavaFX Button which will open the AddProductView on user click
     */
    private Button buildAddProductButton(Stage primaryStage) {
        final Button btnAddProductButton = new Button("Add Product");
        btnAddProductButton.setOnAction(actionEvent -> {
            if (!customerModel.getCustomers().isEmpty()) {
                final AddProductController addProductController = new AddProductController(productModel, customerModel);
                final AddProductView addProductView = (AddProductView) addProductController.getView();
                addProductView.attachButtonEvent(addProductView.getBtnSubmit(), addProductController.submitButtonAction(new ArrayList<TextField>() {{
                    add(addProductView.getTfId());
                    add(addProductView.getTfProductName());
                    add(addProductView.getTfProductShopPrice());
                    add(addProductView.getTfProductCustomerPrice());
                }}, addProductView.getLvCustomers()));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(primaryStage);
                stage.setTitle("Product");
                stage.setScene(new Scene(addProductController.buildView(), 700, 400));
                stage.show();
            } else {
                DialogView.buildDialog(Modality.APPLICATION_MODAL, primaryStage, "Error", ADD_PRODUCT_ERROR, false).show();
            }
        });
        return btnAddProductButton;
    }
    
    /**
     * @param primaryStage JavaFX Stage which is the owner for this stage
     *
     * @return JavaFX Button which will open the RemoveProductView on user click
     */
    private Button buildRemoveProductButton(Stage primaryStage) {
        final Button btnShowProduct = new Button("Remove Product");
        btnShowProduct.setOnAction(actionEvent -> {
            if (!productModel.getMap().isEmpty()) {
                final RemoveProductController removeProductController = new RemoveProductController(productModel);
                final RemoveProductView removeProductView = (RemoveProductView) removeProductController.getView();
                removeProductView.attachButtonEvent(removeProductView.getBtnRemove(),
                        removeProductController.removeSingleProduct());
                removeProductView.attachButtonEvent(removeProductView.getBtnRemoveAll(), removeProductController.removeAllProducts());
                removeProductView.geTTotalProfit().setText(String.valueOf(removeProductController.getTotalProfit()));
                Stage stage = new Stage();
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initOwner(primaryStage);
                stage.setTitle("Show Product");
                stage.setScene(new Scene(removeProductController.buildView(), 400, 300));
                stage.show();
            } else {
                DialogView.buildDialog(Modality.APPLICATION_MODAL, primaryStage, "Error", REMOVE_PRODUCT_ERROR, false).show();
            }
        });
        return btnShowProduct;
    }
    
    /**
     * @param primaryStage JavaFX Stage which is the owner for this stage
     *
     * @return JavaFX Button which will open the DiscountView on user click
     */
    private Button buildPrintCustomersWithDiscountButton(Stage primaryStage) {
        final Button btnDiscountButton = new Button("Display Customers");
        btnDiscountButton.setOnAction(actionEvent -> {
            if (!customerModel.getCustomers().isEmpty()) {
                final DiscountController discountController = new DiscountController(customerModel);
                final DiscountView discountView = (DiscountView) discountController.getView();
                discountView.attachButtonEvent(discountView.getBtnShowCustomers(), discountController.showCustomers());
                Stage stage = new Stage();
                stage.initModality(Modality.NONE);
                stage.initOwner(primaryStage);
                stage.setTitle("Customers");
                stage.setScene(new Scene(discountController.buildView(), 400, 250));
                stage.show();
            } else {
                DialogView.buildDialog(Modality.APPLICATION_MODAL, primaryStage, "Error", SHOW_CUSTOMERS_ERROR, false).show();
            }
        });
        return btnDiscountButton;
    }
    
    /**
     * @param primaryStage JavaFX Stage which is the owner for this stage
     *
     * @return JavaFX Button object which will undo the previous product input in the system
     */
    private Button buildUndoButton(Stage primaryStage) {
        final Button btnUndo = new Button("Undo Product");
        btnUndo.setOnAction(actionEvent -> {
            if (!productModel.getStates().isEmpty()) {
                productModel.restoreState();
                DialogView.buildDialog(Modality.APPLICATION_MODAL, primaryStage, "Success", UNDO_SUCCESSFUL, false).show();
            } else {
                DialogView.buildDialog(Modality.APPLICATION_MODAL, primaryStage, "Error", UNDO_ERROR, false).show();
            }
        });
        return btnUndo;
    }
}
