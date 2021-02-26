package etc;

import java.io.FileNotFoundException;

/**
 * @author Adam Akiva
 * Interface used for the command implemention
 */
public interface IUserAction {
    void execute() throws FileNotFoundException;
}
