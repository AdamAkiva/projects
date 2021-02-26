package views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;

/**
 * A base class for all views in the program
 */
public abstract class BaseView {
    
    /**
     * @return The view of built by the this class, used in Main to create new views
     */
    public abstract Parent buildView();
    
    /**
     * @param btn javaFX button to attach the event to
     * @param btnEvent EventHandler to attach to button
     */
    public void attachButtonEvent(Button btn, EventHandler<ActionEvent> btnEvent) {
        btn.setOnAction(btnEvent);
    }
}
