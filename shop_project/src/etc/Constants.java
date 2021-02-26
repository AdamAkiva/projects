package etc;

/**
 * @author Adam Akiva
 * Class used for constants around all the program
 */
public abstract class Constants {
    
    public static final String ADD_PRODUCT_ERROR = "Can't add a product without\nany customers to attach to.\nPlease create a customer first";
    public static final String REMOVE_PRODUCT_ERROR = "No products to remove";
    public static final String UNDO_SUCCESSFUL = "Undo successful";
    public static final String UNDO_ERROR = "Nothing to undo";
    public static final String PRODUCT_REMOVED_SUCCESSFULLY = "Product removed successfully";
    public static final String ALL_PRODUCTS_REMOVED_SUCCESSFULLY = "All products removed successfully";
    public static final String SHOW_CUSTOMERS_ERROR = "No customers interested in discount offers";
    
    public static final String CUSTOMERS_INTERESTED_IN_NEW_PRODUCTS = "Customers informed about new product:\n";
    public static final String PRODUCT_ADDED_SUCCESSFULLY = "Product added successfully";
    public static final String CUSTOMER_ADDED_SUCCESSFULLY = "Customer added successfully";
    
    public static final String PRODUCT_FILE = "products.bin";
    
    public static final int NUMBER_OF_VARIABLES = 8;
    public static final int SIZE_OF_INTEGER = 4;
    
    public static final int ASCENDING = 0;
    public static final int DESCENDING = 1;
    public static final int INSERT = 2;
    
    public static final String PRODUCT_ID_REGEX = "^(?=\\s*\\S).*$";
    public static final String PRODUCT_NAME_REGEX = "^[a-zA-Z0-9 ]*$";
    public static final String PRODUCT_SHOP_PRICE_REGEX = "^[1-9]\\d*$";
    public static final String PRODUCT_CUSTOMER_PRICE_REGEX = PRODUCT_SHOP_PRICE_REGEX;
    
    public static final String CANT_BE_EMPTY_ERROR_MESSAGE = "Can't be empty";
    public static final String CUSTOMER_NAME_ERROR_MESSAGE = "Letters and number only";
    public static final String CUSTOMER_PHONE_NUMBER_ERROR_MESSAGE = "9-10 numbers";
    
    public static final String CUSTOMER_NAME_REGEX = "^[a-zA-Z ]+$";
    public static final String CUSTOMER_PHONE_NUMBER_REGEX = "[0-9]{9,10}";
}
