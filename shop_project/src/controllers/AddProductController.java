package controllers;

import etc.IUserAction;
import etc.InvalidConstructorException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.*;
import views.AddProductView;
import views.BaseView;
import views.DialogView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import static etc.Constants.*;

/**
 * @author Adam Akiva
 * Controller class for managing the AddProductView window
 */
public class AddProductController extends BaseController implements IUserAction {
    
    private final ProductModel productModel; // ProductModel instance
    private final CustomerModel customerModel; // CustomerModel instance
    private final AddProductView view; // view
    
    private final Set<Customer> customers;  // Set to hold all the customers in the system
    
    private String id;  // holds the id for the new product
    private Product product; // holds the new product
    
    /**
     * @param productModel ProductModel instance
     * @param customerModel CustomerModel instance
     */
    public AddProductController(ProductModel productModel, CustomerModel customerModel) {
        this.productModel = productModel;
        this.customerModel = customerModel;
        this.customers = this.customerModel.getCustomers();
        this.view = new AddProductView(customers);
        
        view.attachButtonEvent(view.getBtnSubmit(), submitButtonAction(new ArrayList<>() {{
            add(view.getTfId());
            add(view.getTfProductName());
            add(view.getTfProductShopPrice());
            add(view.getTfProductCustomerPrice());
        }}, view.getLvCustomers()));
    }
    
    public BaseView getView() {
        return view;
    }
    
    /**
     * Method to add the new product to the product map in the model
     */
    @Override
    public void execute() throws FileNotFoundException {
        productModel.put(id, product);
    }
    
    
    /**
     * Method used to save the current state of the ProductModel
     */
    public void saveState() throws IOException, InvalidConstructorException{
        productModel.saveState();
    }
    
    public Parent buildView() {
        return view.buildView();
    }
    
    
    /**
     * Method used to for creating the action for the submit button in the view that this controller represent
     * @param tfFields List of TextFields for all the TextFields in the view to check if they are valid
     * @param lvCustomers ListView of Customers used to attach the chosen customer to the new product
     *
     * @return EventHandler Interface to attach to the submit button in the view
     */
    public EventHandler<ActionEvent> submitButtonAction(final List<TextField> tfFields, final ListView<String> lvCustomers) {
        // Putting a new value with the same key will replace the old one, hence doing what was asked
        return actionEvent -> {
            try {
                boolean validProductId = checkFieldValidation(tfFields.get(0), PRODUCT_ID_REGEX, false, CANT_BE_EMPTY_ERROR_MESSAGE);
                boolean validProductName = checkFieldValidation(tfFields.get(1), PRODUCT_NAME_REGEX, false, CANT_BE_EMPTY_ERROR_MESSAGE);
                boolean validProductShopPrice = checkFieldValidation(tfFields.get(2), PRODUCT_SHOP_PRICE_REGEX, true, "");
                boolean validProductCustomerPrice = checkFieldValidation(tfFields.get(3), PRODUCT_CUSTOMER_PRICE_REGEX, true, "");
                if (validProductId && validProductName && validProductShopPrice && validProductCustomerPrice) {
                    saveState();
                    id = tfFields.get(0).getText();
                    String name = tfFields.get(1).getText();
                    String shopPrice = tfFields.get(2).getText().isEmpty() ? "0" : tfFields.get(2).getText();
                    String customerPrice = tfFields.get(3).getText().isEmpty() ? "0" : tfFields.get(3).getText();
                    product = new Product(name, shopPrice, customerPrice);
                    Iterator<Customer> itr = customers.iterator();
                    int i = 0;
                    Customer customer = null;
                    while (i != customers.size()) {
                        customer = itr.next();
                        if (i == lvCustomers.getSelectionModel().getSelectedIndex()) {
                            product.setCustomer(customer);
                            break;
                        }
                        i++;
                    }
                    customerModel.addDiscountedProduct(product);
                    execute();
                    tfFields.get(0).setText("");
                    tfFields.get(1).setText("");
                    tfFields.get(2).setText("");
                    tfFields.get(3).setText("");
                    DialogView.buildDialog(Modality.APPLICATION_MODAL, (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow(), "Completed",
                            PRODUCT_ADDED_SUCCESSFULLY, true).show();
                    if (customerModel.getIDiscountList().size() > 1) {
                        DialogView.buildDialog(Modality.APPLICATION_MODAL, (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow(),
                                "Discounts", CUSTOMERS_INTERESTED_IN_NEW_PRODUCTS + customerModel.getApprovedCustomers(customer), false).show();
                    }
                }
            } catch (InvalidConstructorException | IOException e) {
                // should never happen, unless there was a security breach, if the file was not found it is created, and the only way that
                // the constructor throws an exception is if the data the user inputted and the data the server got is different
            }
        };
    }
}
