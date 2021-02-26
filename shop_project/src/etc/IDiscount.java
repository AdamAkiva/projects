package etc;

import models.Customer;

/**
 * @author Adam Akiva
 * Interface used for the observer implemention
 */
public interface IDiscount {
    Customer update(Object o);
}
