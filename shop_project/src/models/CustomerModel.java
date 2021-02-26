package models;

import etc.IDiscount;

import java.util.*;

/**
 * Model for the customer
 */
public class CustomerModel {
    
    private static CustomerModel instance = null; // Singleton instance
    
    private final Set<Customer> customers;  // Set of all the customers in the system
    private final List<IDiscount> IDiscountList; // List for the observable design pattern
    private final Set<Customer> approvedCustomers;  // Set of all the customers interested in discounts
    
    public CustomerModel() {
        this.customers = new TreeSet<>();
        this.IDiscountList = new ArrayList<>();
        this.approvedCustomers = new LinkedHashSet<>();
    }
    
    /**
     * @return Singleton instance
     */
    public static CustomerModel getInstance() {
        if (instance == null) {
            instance = new CustomerModel();
        }
        return instance;
    }
    
    /**
     * @param customer Customer object to add to the list of customers
     */
    public void add(Customer customer) {
        customers.add(customer);
    }
    
    /**
     * @param customers Customer list to add to the list of customers
     */
    public void addAll(List<Customer> customers) {
        this.customers.addAll(customers);
    }
    
    public Set<Customer> getCustomers() {
        return customers;
    }
    
    public List<IDiscount> getIDiscountList() {
        return IDiscountList;
    }
    
    /**
     * @param buyer Customer object which buys a new product, used to include the customer from receiving an offer
     *              on a product he himself bought
     *
     * @return String containing the names of all the customer names that will be notified about the new product
     */
    public String getApprovedCustomers(Customer buyer) {
        StringBuilder sb = new StringBuilder();
        Iterator<Customer> itr = approvedCustomers.iterator();
        int counter = 0;
        while (itr.hasNext()) {
            Customer temp = itr.next();
            if (!buyer.equals(temp)) {
                counter++;
                if (counter > 1 && counter % 3 == 0) {
                    sb.append("\n");
                } else if (counter > 1) {
                    sb.append(", ");
                }
                sb.append(temp.getName());
                if (!itr.hasNext()) {
                    return sb.toString();
                }
            }
        }
        return sb.toString();
    }
    
    public void addObserver(IDiscount IDiscount) {
        IDiscountList.add(IDiscount);
    }
    
    public void addDiscountedProduct(Object o) {
        if (o instanceof Product) {
            Product product = (Product) o;
            for (IDiscount IDiscount : IDiscountList) {
                Customer customer = IDiscount.update(product);
                if (customer != null) {
                    approvedCustomers.add(customer);
                }
            }
        }
    }
}
