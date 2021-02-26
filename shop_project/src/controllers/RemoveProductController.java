package controllers;

import etc.IUserAction;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Product;
import models.ProductModel;
import views.BaseView;
import views.DialogView;
import views.RemoveProductView;

import java.io.FileNotFoundException;

import static etc.Constants.ALL_PRODUCTS_REMOVED_SUCCESSFULLY;
import static etc.Constants.PRODUCT_REMOVED_SUCCESSFULLY;

/**
 * @author Adam Akiva
 * Controller class for managing the RemoveProductView window
 */
public class RemoveProductController extends BaseController implements IUserAction {
    
    private final ProductModel model; // ProductModel instance
    private final RemoveProductView view; // view
    
    private String id; // String holding the id of the removed product, or null if remove all
    
    /**
     * @param model ProductModel instance
     */
    public RemoveProductController(ProductModel model) {
        this.model = model;
        this.view = new RemoveProductView(this.model.getMap());
    }
    
    public BaseView getView() {
        return view;
    }
    
    /**
     * Method to add the new customer to the customer list in the model
     */
    public void execute() throws FileNotFoundException {
        if (id != null) {
            model.remove(id);
        } else {
            model.removeAll();
        }
    }
    
    /**
     * @return the total profit form all the products
     */
    public int getTotalProfit() {
        int totalProfit = 0;
        for (Product product : model.getMap().values()) {
            totalProfit += product.getProfit();
        }
        return totalProfit;
    }
    
    public Parent buildView() {
        return view.buildView();
    }
    
    /**
     * A method used to create an EventHandler for pressing the remove product button
     * @return EventHandler for pressing the remove product button
     */
    public EventHandler<ActionEvent> removeSingleProduct() {
        return actionEvent -> {
            try {
                String temp = view.getLvProducts().getSelectionModel().getSelectedItem();
                id = temp.substring(temp.indexOf(" ") + 1, temp.indexOf("\t"));
                execute();
                view.geTTotalProfit().setText(String.valueOf(getTotalProfit()));
                DialogView.buildDialog(Modality.APPLICATION_MODAL, (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow(), "Completed",
                        PRODUCT_REMOVED_SUCCESSFULLY, true).show();
            } catch (FileNotFoundException e) {
                // should never happen
            }
        };
    }
    
    /**
     * A method used to create an EventHandler for pressing the remove all button
     * @return EventHandler for pressing the remove all button
     */
    public EventHandler<ActionEvent> removeAllProducts() {
        return actionEvent -> {
            try {
                id = null;
                execute();
                view.geTTotalProfit().setText("0");
                DialogView.buildDialog(Modality.APPLICATION_MODAL, (Stage) ((Node) (actionEvent.getSource())).getScene().getWindow(), "Completed",
                        ALL_PRODUCTS_REMOVED_SUCCESSFULLY, true).show();
            } catch (FileNotFoundException e) {
                // should never happen
            }
        };
    }
}
