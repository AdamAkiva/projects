package models;

import etc.IDiscount;
import etc.InvalidConstructorException;

import static etc.Constants.CUSTOMER_NAME_REGEX;

/**
 * A class used to represent a Customer
 */
public class Customer implements IDiscount, Comparable<Customer> {
    
    private final boolean discounts;  // Indicates whether the customer is interested in new discounts
    private String name; // Used to hold the name of the customer
    private int phoneNumber; // Used to hold the customer phone number
    
    /**
     * @param name String holding the name of the customer
     * @param phoneNumber String holding the customer's phone number
     * @param discounts Indicates whether the customer is interested in new discounts
     *
     * @throws InvalidConstructorException If the name or phone number does not match their regexes
     */
    public Customer(String name, String phoneNumber, boolean discounts) throws InvalidConstructorException {
        setName(name);
        setPhoneNumber(phoneNumber);
        this.discounts = discounts;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) throws InvalidConstructorException {
        if (name.matches(CUSTOMER_NAME_REGEX)) {
            this.name = name;
        } else {
            throw new InvalidConstructorException();
        }
    }
    
    public int getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) throws InvalidConstructorException {
        if (phoneNumber.matches(phoneNumber)) {
            try {
                this.phoneNumber = Integer.parseInt(phoneNumber);
            } catch (NumberFormatException e) {
                throw new InvalidConstructorException();
            }
        } else {
            throw new InvalidConstructorException();
        }
    }
    
    public boolean getDiscounts() {
        return discounts;
    }
    
    @Override
    public int hashCode() {
        return name.hashCode() + phoneNumber;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Customer) {
            Customer temp = (Customer) obj;
            if (temp.getName().equals(name)) {
                return temp.getPhoneNumber() == phoneNumber;
            }
        }
        return false;
    }
    
    @Override
    public String toString() {
        return String.format("Customer name: %s\nCustomer phone number: %d\n%s interested in additional discounts: %s\n", name, phoneNumber, name,
                discounts ? "Yes" : "No");
    }
    
    @Override
    public Customer update(Object o) {
        if (o instanceof Product && discounts) {
            return this;
        }
        return null;
    }
    
    @Override
    public int compareTo(Customer customer) {
        if (equals(customer)) {
            return 0;
        } else if (customer.getName().charAt(0) < name.charAt(0)) {
            return 1;
        }
        return -1;
    }
}
