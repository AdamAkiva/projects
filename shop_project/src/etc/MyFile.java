package etc;

import models.Product;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Map;

import static etc.Constants.SIZE_OF_INTEGER;

/**
 * @author Adam Akiva
 * Class used to represent a file with products and customers
 */
public class MyFile {
    
    private final Map<String, Product> products; // map holding the products
    private File file; // file object
    
    /**
     * @param file File object
     * @param products Map Object
     *
     * @throws IOException If the file can't be found
     * @throws InvalidConstructorException If the map read from the file is corrupted
     */
    public MyFile(File file, Map<String, Product> products) throws IOException, InvalidConstructorException {
        setFile(file);
        this.products = products;
        buildMapFromFile();
    }
    
    /**
     * A method used to create a new file if there is not one
     * @param file File object
     *
     * @throws IOException If the file can't be created
     */
    private void setFile(File file) throws IOException {
        this.file = file;
        if (!file.exists()) {
            file.createNewFile();
        }
    }
    
    public File getFile() {
        return file;
    }
    
    /**
     * A method used to write bytes to the associated file
     * @param bytes byte[] that will be written to the file
     *
     * @throws FileNotFoundException If the file was not found
     */
    public void writeToFile(byte[] bytes) throws FileNotFoundException {
        MyFileIterator iterator = new MyFileIterator();
        iterator.write(bytes);
        iterator.finish();
    }
    
    /**
     * A method used to update a specific set of bytes in the associated file
     * @param id String identifier to find which bytes to update
     * @param bytes byte[] to replace with the current one
     *
     * @throws FileNotFoundException If the file was not found
     */
    public void updateInFile(String id, byte[] bytes) throws FileNotFoundException {
        MyFileIterator iterator = new MyFileIterator();
        iterator.updateValue(iterator.findById(id), bytes);
        iterator.finish();
    }
    
    /**
     * A method used to build a Map<String, Product> from the associated file
     * @throws FileNotFoundException If the file was not found
     * @throws InvalidConstructorException If the file is corrupted (meaning someone manually changed the file)
     */
    private void buildMapFromFile() throws FileNotFoundException, InvalidConstructorException {
        MyFileIterator iterator = new MyFileIterator();
        while (iterator.hasNext()) {
            byte[] bytes = iterator.next();
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            byteBuffer.position(byteBuffer.position() + SIZE_OF_INTEGER);
            final byte[] key = new byte[byteBuffer.getInt()];
            byteBuffer.get(key);
            products.put(new String(key, StandardCharsets.UTF_8), Product.toProduct(bytes));
        }
        iterator.finish();
    }
    
    /**
     * A method used to remove a set of bytes from the file, holding a product by a given id
     * @param id String identifier to find the current byte[] in the file
     *
     * @throws FileNotFoundException If the file was not found
     */
    public void removeProductFromFileById(String id) throws FileNotFoundException {
        MyFileIterator iterator = new MyFileIterator();
        iterator.removeById(id);
        iterator.finish();
    }
    
    /**
     * A method used to remove all the products from the file (basically remove all the content from the file)
     * @throws FileNotFoundException If the was not found
     */
    public void removeAllProductsFromFile() throws FileNotFoundException {
        MyFileIterator iterator = new MyFileIterator();
        iterator.removeAll();
        iterator.finish();
    }
    
    /**
     * @author Adam Akiva
     * A class used to iterate over a byte[]
     */
    private class MyFileIterator implements Iterator<byte[]> {
        
        private final RandomAccessFile raf; // RandomAccessFile used to go over the file to do different actions
    
        /**
         * @throws FileNotFoundException If the file was not found
         */
        public MyFileIterator() throws FileNotFoundException {
            raf = new RandomAccessFile(file, "rw");
        }
    
        /**
         * @return if there is another set of bytes representing a product in the file
         */
        @Override
        public boolean hasNext() {
            try {
                return raf.getFilePointer() < raf.length();
            } catch (IOException ignored) {
                return false;
            }
        }
    
        /**
         * @return byte[] representing the next product in the file
         */
        @Override
        public byte[] next() {
            try {
                byte[] bytes = new byte[raf.readInt()];
                raf.seek(raf.getFilePointer() - SIZE_OF_INTEGER);
                raf.read(bytes);
                return bytes;
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
            return null;
        }
    
        /**
         * A method used to remove the next byte[] representing a product
         */
        @Override
        public void remove() {
            try {
                long pointer = raf.getFilePointer();
                int sizeOfProduct = raf.readInt();
                long newFileSize = raf.length() - sizeOfProduct;
                long sizeAfterProduct = raf.length() - pointer - sizeOfProduct;
                byte[] bytes = new byte[(int) sizeAfterProduct];
                raf.skipBytes(sizeOfProduct - SIZE_OF_INTEGER);
                raf.read(bytes);
                raf.seek(pointer);
                raf.write(bytes);
                raf.setLength(newFileSize);
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
        }
    
        /**
         * A method used to find the start index of the product with the given id
         * @param id String identifier to find the current byte[] in the file
         *
         * @return Long the start index of the product, -1 if EOF is reached
         */
        public long findById(String id) {
            try {
                raf.seek(0);
                while (hasNext()) {
                    int sizeOfProduct = raf.readInt();
                    byte[] key = new byte[raf.readInt()];
                    raf.read(key);
                    raf.seek(raf.getFilePointer() - key.length - SIZE_OF_INTEGER * 2);
                    if (new String(key, StandardCharsets.UTF_8).equals(id)) {
                        return raf.getFilePointer();
                    }
                    raf.skipBytes(sizeOfProduct);
                }
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
            return -1;
        }
    
        /**
         * A method used to replace from pointer index with the given byte[]
         * @param pointer start index where to replace the given byte[]
         * @param bytes byte[] to replace in the file
         */
        public void updateValue(long pointer, byte[] bytes) {
            try {
                int sizeOfProduct = raf.readInt();
                byte[] remainingValue = new byte[(int) (raf.length() - pointer - sizeOfProduct)];
                raf.seek(pointer);
                raf.skipBytes(sizeOfProduct);
                raf.read(bytes);
                raf.seek(pointer);
                raf.write(bytes);
                raf.write(remainingValue);
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
        }
        
        public void removeAll() {
            try {
                raf.setLength(0);
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
        }
        
        public void write(byte[] bytes) {
            try {
                raf.seek(raf.length());
                raf.write(bytes);
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
        }
        
        public void removeById(String id) {
            try {
                long pointer = findById(id);
                if (pointer != -1) {
                    raf.seek(pointer);
                    remove();
                }
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
        }
        
        public void finish() {
            try {
                raf.close();
            } catch (IOException e) {
                e.printStackTrace(); // should never happen
            }
        }
    }
}
