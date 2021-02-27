package controllers;

import etc.IUserAction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import models.Customer;
import models.CustomerModel;
import views.DiscountView;

/**
 * @author Adam Akiva
 * Controller class for managing the DiscountView window
 */
public class DiscountController extends BaseController implements IUserAction {
    
    private final CustomerModel model; // CustomerModel instance
    private final DiscountView view; // view
    
    private Customer customer; // Customer used by the thread cycling between all the customers that are interested in new discounts
    
    /**
     * @param model CustomerModel instance
     */
    public DiscountController(CustomerModel model) {
        this.model = model;
        this.view = new DiscountView();
        
        view.attachButtonEvent(view.getBtnShowCustomers(), showCustomers());
    }
    
    public Parent buildView() {
        return view.buildView();
    }
    
    /**
     * Method to add the display a new customer to interested in discounts
     */
    @Override
    public void execute() {
        view.getTCustomer().setText(customer.getName());
    }
    
    /**
     * Method used to for creating the action for the show customers button in the view that this controller represent
     *
     * @return EventHandler Interface to attach to the submit button in the view
     */
    public EventHandler<ActionEvent> showCustomers() {
        return actionEvent -> displayDiscountCustomers();
    }
    
    /**
     * A method used to begin a thread used to show all the customers that are interested in new discounts cycling every 2 seconds
     */
    private void displayDiscountCustomers() {
        new Thread(() -> {
            try {
                for (Customer customer : model.getCustomers())
                    if (customer.getDiscounts()) {
                        this.customer = customer;
                        execute();
                        Thread.sleep(2000);
                    }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
