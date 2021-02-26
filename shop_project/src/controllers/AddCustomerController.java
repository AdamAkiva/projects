package controllers;

import etc.IUserAction;
import etc.InvalidConstructorException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Customer;
import models.CustomerModel;
import views.AddCustomerView;
import views.BaseView;
import views.DialogView;

import java.util.List;

import static etc.Constants.*;

/**
 * @author Adam Akiva
 * Controller class for managing the AddProductView window
 */
public class AddCustomerController extends BaseController implements IUserAction {
    
    private final CustomerModel model; // customer model instance
    private final AddCustomerView view; // view
    
    private Customer customer;  // used to hold the new customer created by the user
    
    /**
     * @param model CustomerModel instance
     */
    public AddCustomerController(CustomerModel model) {
        this.model = model;
        this.view = new AddCustomerView();
    }
    
    /**
     * Method to add the new customer to the customer list in the model
     */
    @Override
    public void execute() {
        model.add(customer);
    }
    
    public Parent buildView() {
        return view.buildView();
    }
    
    public BaseView getView() {
        return view;
    }
    
    /**
     * Method used to for creating the action for the submit button in the view that this controller represent
     * @param tfFields List of TextFields for all the TextFields in the view to check if they are valid
     * @param cbBox CheckBox to know if the customer wants to get discount offers or not
     *
     * @return EventHandler Interface to attach to the submit button in the view
     */
    public EventHandler<ActionEvent> submitButtonAction(final List<TextField> tfFields, CheckBox cbBox) {
        return actionEvent -> {
            try {
                if (checkFieldValidation(tfFields.get(0), CUSTOMER_NAME_REGEX, null, CUSTOMER_NAME_ERROR_MESSAGE) &&
                        checkFieldValidation(tfFields.get(1), CUSTOMER_PHONE_NUMBER_REGEX, null, CUSTOMER_PHONE_NUMBER_ERROR_MESSAGE)) {
                    customer = new Customer(tfFields.get(0).getText(), tfFields.get(1).getText(), cbBox.isSelected());
                    execute();
                    if (customer.getDiscounts()) {
                        model.addObserver(customer);
                    }
                    tfFields.get(0).setText("");
                    tfFields.get(1).setText("");
                    DialogView.buildDialog(Modality.APPLICATION_MODAL, (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow(), "Completed",
                            CUSTOMER_ADDED_SUCCESSFULLY, true).show();
                }
            } catch (InvalidConstructorException e) {
                // should never happen, the only way that the constructor throws an exception is if the data the user inputted and the data
                // the server got is different
            }
        };
    }
}
