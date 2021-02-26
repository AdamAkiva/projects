package models;

import java.util.Stack;

/**
 * A class used to save states of an e object for undo operation
 */
public class ProductModelStates<e> {
    
    private final Stack<e> mapStates;
    
    public ProductModelStates() {
        this.mapStates = new Stack<>();
    }
    
    public void add(e mapState) {
        mapStates.push(mapState);
    }
    
    public e undo() {
        return mapStates.pop();
    }
    
    public boolean isEmpty() {
        return mapStates.isEmpty();
    }
}
