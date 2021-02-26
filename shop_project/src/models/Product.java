package models;

import etc.InvalidConstructorException;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static etc.Constants.*;

/**
 * A class used to represent a product in the store
 */
public class Product {
    
    private String name; // String holding the name of the product
    private int shopPrice; // Integer holding the price the shop had to pay to buy the product from the supplier
    private int customerPrice; // Integer holding the price the customer need to pay for the product
    private Customer customer; // Customer object associated with the product
    
    /**
     * @param name String holding the name of the product
     * @param shopPrice Integer holding the price the shop had to pay to buy the product from the supplier
     * @param customerPrice Integer holding the price the customer need to pay for the product
     * @param customer Customer object associated with the product
     *
     * @throws InvalidConstructorException If any of the parameters is invalid
     */
    public Product(String name, String shopPrice, String customerPrice, Customer customer) throws InvalidConstructorException {
        setName(name);
        setShopPrice(shopPrice);
        setCustomerPrice(customerPrice);
        this.customer = customer;
    }
    
    /**
     * @param name String holding the name of the product
     * @param shopPrice Integer holding the price the shop had to pay to buy the product from the supplier
     * @param customerPrice Integer holding the price the customer need to pay for the product
     *
     * @throws InvalidConstructorException If any of the parameters is invalid
     */
    public Product(String name, String shopPrice, String customerPrice) throws InvalidConstructorException {
        this.name = name;
        setShopPrice(shopPrice);
        setCustomerPrice(customerPrice);
        this.customer = null;
    }
    
    /**
     * @param key String identifier for product
     * @param product Product object to be casted to a byte[]
     *
     * @return byte[] containing the identifier and the product data
     */
    public static byte[] toBytes(String key, Product product) {
        final int keyLen = key.length();
        final int nameLen = product.getName().length();
        final int shopPriceLen = String.valueOf(product.getShopPrice()).length();
        final int customerPriceLen = String.valueOf(product.getCustomerPrice()).length();
        final int customerNameLen = product.getCustomer().getName().length();
        final int customerPhoneNumberLen = String.valueOf(product.getCustomer().getPhoneNumber()).length();
        final int discountLen = SIZE_OF_INTEGER;
        final int tLen = keyLen + nameLen + shopPriceLen + customerPriceLen + customerNameLen + customerPhoneNumberLen + discountLen;
        final ByteBuffer byteBuffer = ByteBuffer.allocate(NUMBER_OF_VARIABLES * SIZE_OF_INTEGER + tLen)
                .putInt(NUMBER_OF_VARIABLES * SIZE_OF_INTEGER + tLen) // the total size of the bytes from this point to the end
                .putInt(key.length()).put(key.getBytes())  // length of key followed by the bytes of key
                .putInt(product.getName().length()).put(product.getName().getBytes()) // length of name followed by the bytes of name
                .putInt(String.valueOf(product.getShopPrice()).length()).put(String.valueOf(product.getShopPrice()).getBytes())
                // length of shopPrice followed by the bytes of key
                .putInt(String.valueOf(product.getCustomerPrice()).length()).put(String.valueOf(product.getCustomerPrice()).getBytes())
                // length of key customerPrice by the bytes of customerPrice
                .putInt(product.getCustomer().getName().length()).put(product.getCustomer().getName().getBytes())
                // length of customerName followed by the bytes of customerName
                .putInt(String.valueOf(product.getCustomer().getPhoneNumber()).length()).put(String.valueOf(product.getCustomer().getPhoneNumber()).getBytes())
                // length of customerPhoneNumber followed by the bytes of customerPhoneNumber
                .putInt(product.getCustomer().getDiscounts() ? 1 : 0);
        // length of integer followed by 1 for true or 0 for false
        return byteBuffer.array();
    }
    
    /**
     * @param bytes byte[] containing an identifier and the product data
     *
     * @return Product object with the data that was in the byte[]
     *
     * @throws InvalidConstructorException If the given byte[] was corrupted
     */
    public static Product toProduct(byte[] bytes) throws InvalidConstructorException {
        final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        byteBuffer.position(byteBuffer.position() + SIZE_OF_INTEGER); // skips the total size of bytes written
        byteBuffer.position(byteBuffer.position() + SIZE_OF_INTEGER + byteBuffer.getInt()); // skips the id
        final byte[] name = new byte[byteBuffer.getInt()];
        byteBuffer.get(name);
        final byte[] shopPrice = new byte[byteBuffer.getInt()];
        byteBuffer.get(shopPrice);
        final byte[] customerPrice = new byte[byteBuffer.getInt()];
        byteBuffer.get(customerPrice);
        final byte[] customerName = new byte[byteBuffer.getInt()];
        byteBuffer.get(customerName);
        final byte[] customerPhoneNumber = new byte[byteBuffer.getInt()];
        byteBuffer.get(customerPhoneNumber);
        final boolean discount = byteBuffer.getInt() == 1;
        return new Product(new String(name, StandardCharsets.UTF_8), new String(shopPrice, StandardCharsets.UTF_8),
                new String(customerPrice, StandardCharsets.UTF_8), new Customer(new String(customerName, StandardCharsets.UTF_8),
                new String(customerPhoneNumber, StandardCharsets.UTF_8), discount));
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) throws InvalidConstructorException {
        if (name.matches(PRODUCT_NAME_REGEX)) {
            this.name = name;
        } else {
            throw new InvalidConstructorException();
        }
    }
    
    public int getShopPrice() {
        return shopPrice;
    }
    
    public void setShopPrice(String shopPrice) throws InvalidConstructorException {
        if (shopPrice.isEmpty()) {
            this.shopPrice = 0;
        } else if (shopPrice.matches(PRODUCT_SHOP_PRICE_REGEX)) {
            try {
                this.shopPrice = Integer.parseInt(shopPrice);
            } catch (NumberFormatException e) {
                throw new InvalidConstructorException();
            }
        } else {
            throw new InvalidConstructorException();
        }
    }
    
    public int getCustomerPrice() {
        return customerPrice;
    }
    
    public void setCustomerPrice(String customerPrice) throws InvalidConstructorException {
        if (customerPrice.isEmpty()) {
            this.customerPrice = 0;
        } else if (customerPrice.matches(PRODUCT_CUSTOMER_PRICE_REGEX)) {
            try {
                this.customerPrice = Integer.parseInt(customerPrice);
            } catch (NumberFormatException e) {
                throw new InvalidConstructorException();
            }
        } else {
            throw new InvalidConstructorException();
        }
    }
    
    /**
     * @return Integer holding the profit from the product
     */
    public int getProfit() {
        return customerPrice - shopPrice;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() + shopPrice + customerPrice;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {
            Product temp = (Product) obj;
            if (temp.getName().equals(name)) {
                if (temp.getShopPrice() == shopPrice) {
                    return temp.getCustomerPrice() == customerPrice;
                }
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("Name: %s, Shop Price: %d, Customer Price: %d, Profit: %d", name, shopPrice, customerPrice, getProfit());
    }
}
