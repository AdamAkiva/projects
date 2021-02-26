package controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import views.BaseView;

/**
 * @author Adam Akiva
 * Base abstract class for the Controllers
 */
public abstract class BaseController {
    
    /**
     * Function used to build the view for this controller
     * @return Javafx Parent object representing the created view
     */
    public abstract Parent buildView();
    
    /**
     * @return BaseView object holding the view object
     */
    public abstract BaseView getView();
    
    /**
     * A method used to validate a field with the given regex
     * @param textField TextField object to validate
     * @param regex String holding a regex to match with the text of the TextField
     * @param defaultValue Optional parameter, if the TextField can be empty use this for a default value, if not input null
     * @param errorMessage String holding the message the user will see as an error
     *
     * @return True if valid, false if not
     */
    public boolean checkFieldValidation(final TextField textField, final String regex, final String defaultValue, final String errorMessage) {
        if (textField.getText().matches(regex)) {
            return true;
        }
        if (textField.getText().isEmpty() && defaultValue != null) {
            textField.setText(defaultValue);
            return true;
        }
        textField.setText(errorMessage);
        textField.setStyle("-fx-text-fill: red");
        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean oldValue, Boolean newValue) {
                if (newValue) {
                    textField.setText("");
                    textField.setStyle("-fx-text-fill: black");
                    textField.focusedProperty().removeListener(this);
                }
            }
        });
        return false;
    }
}
