package models;

import etc.InvalidConstructorException;
import etc.MyFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import static etc.Constants.*;

/**
 * Model for the product
 */
public class ProductModel {
    
    private static ProductModel instance = null; // Singleton instance
    
    private ProductModelStates<ProductModel> states; // ProductModelStates class used to hold the state of the productModel to allow for undo actions
    
    private Map<String, Product> map; // Map holding all the Product objects with their String identifiers
    private int mapType; // Integer used to dictate how to order the map
    private MyFile file; // MyFile object used to read/write product data to the file
    
    /**
     * @param map Map holding all the Product objects with their String identifiers
     * @param mapType Integer used to dictate how to order the map
     * @param file MyFile object used to read/write product data to the file
     *
     * @throws IOException If the file was not found
     * @throws InvalidConstructorException If the file is invalid
     */
    public ProductModel(Map<String, Product> map, int mapType, File file) throws IOException, InvalidConstructorException {
        if (map != null) this.map = new LinkedHashMap<>(map);
        else this.map = new LinkedHashMap<>();
        this.mapType = mapType;
        this.file = new MyFile(file, this.map);
        states = new ProductModelStates<>();
    }
    
    /**
     * @return Singleton instance
     */
    public static ProductModel getInstance() {
        if (instance == null) {
            try {
                instance = new ProductModel(null, ASCENDING, new File(PRODUCT_FILE));
            } catch (IOException | InvalidConstructorException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
    
    /**
     * @return ArrayList holding all the customers in the system
     */
    public List<Customer> getListOfCustomers() {
        List<Customer> customers = new ArrayList<>();
        for (final Map.Entry<String, Product> entry : map.entrySet()) {
            customers.add(entry.getValue().getCustomer());
        }
        return customers;
    }
    
    /**
     * @return Map holding all the products
     */
    public Map<String, Product> getMap() {
        Map<String, Product> returnMap;
        switch (mapType) {
            case ASCENDING:
                returnMap = new TreeMap<>(map);
                break;
            case DESCENDING:
                returnMap = new TreeMap<>(Collections.reverseOrder());
                returnMap.putAll(map);
                break;
            case INSERT:
                return map;
            default:
                throw new IllegalArgumentException("Should never happen");
        }
        return returnMap;
    }
    
    public int getMapOrderType() {
        return mapType;
    }
    
    public MyFile getMapFile() {
        return file;
    }
    
    public ProductModelStates<ProductModel> getStates() {
        return states;
    }
    
    public void put(String key, Product product) throws FileNotFoundException {
        if (!map.containsKey(key)) {
            map.put(key, product);
            file.writeToFile(Product.toBytes(key, product));
        } else {
            map.put(key, product);
            file.updateInFile(key, Product.toBytes(key, product));
        }
    }
    
    public void remove(String key) throws FileNotFoundException {
        if (map.remove(key) != null) {
            file.removeProductFromFileById(key);
        }
    }
    
    public void removeAll() throws FileNotFoundException {
        map.clear();
        file.removeAllProductsFromFile();
    }
    
    public void saveState() throws IOException, InvalidConstructorException {
        states.add(new ProductModel(map, mapType, file.getFile()));
    }
    
    public void restoreState() {
        if (!states.isEmpty()) {
            try {
                remove(new TreeMap<>(map).pollLastEntry().getKey());
            } catch (FileNotFoundException e) {
                // should never happen
            }
            ProductModel temp = states.undo();
            this.map = temp.getMap();
            this.mapType = temp.getMapOrderType();
            this.file = temp.getMapFile();
            this.states = temp.getStates();
        }
    }
}
