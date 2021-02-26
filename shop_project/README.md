# Collage end course project for practicing design patterns

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Language used: **Java**
## Design patterns: **MVC, Command, Memento, Iterator, Observer and Singleton**
## Graphic libary: **JavaFX**

#### * The project had no requimernets for well made design

---
### **The main Window of the program:**

![Main Window look like this](https://raw.githubusercontent.com/AdamAkiva/studies_projects/master/java/java_course/shop_project/images/main_window.JPG)

---
### **The Add Customer Window of the program:**

![Add Customer Window look like this](https://raw.githubusercontent.com/AdamAkiva/studies_projects/master/java/java_course/shop_project/images/add_customer_window.JPG)

---
### **The Add Product Window of the program:**

![Add Product Window look like this](https://raw.githubusercontent.com/AdamAkiva/studies_projects/master/java/java_course/shop_project/images/add_product_window.JPG)

---
### **The Remove Product Window of the program:**

![Remove Product Window look like this](https://raw.githubusercontent.com/AdamAkiva/studies_projects/master/java/java_course/shop_project/images/remove_product_window.JPG)

---
### **The Display Customers Window of the program:**

![Display Customers Window look like this](https://raw.githubusercontent.com/AdamAkiva/studies_projects/master/java/java_course/shop_project/images/display_customers_window.JPG)

---
### **The Undo Button follows the memento pattern to undo a previous product inserion with a stack**

---
### **Dialog window to indicate a function was successfull or there was an error:**

![Dialog view look like this](https://raw.githubusercontent.com/AdamAkiva/studies_projects/master/java/java_course/shop_project/images/dialog_window.JPG)

---
## Where to find each design pattern:

1. MVC - **The entire program is based on the MVC pattern**
1. Command - **Every controller beside BaseController** (e.g Submit inside Add Customer button) as well as **IUserAction** in etc package
2. Memento - **ProductModel** in models package, **ProductModelStates** in models package, **Main** in view package
3. Iterator - **MyFile** in etc package
4. Observer - **IDiscount** in etc package, **Customer** in models package, **CustomerModel** in models package, **AddProductController** in controllers package
5. Singleton - **CustomerModel** in models package, **ProductModel** in models package

---
## Additoanl Information:

All the products are saved in products.bin file

---
### ***Written by: Adam Akiva***
### ***Email: adam.akiva14@gmail.com***

