# Ort-Singalovski Collage final project

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

## Languages used: **Java, Python**

### A summary of the project is as follows: An application on a parent mobile phone to track views of sexual content by their kids in their computer. The goal of the project is not to block the sexual content, it is to notify the parent of their child behavior and let them decide how to deal with it.

---
### The project is composed of many different parts and technologies: The project has 2 client side applications, server and a database.

### One client is on the parent's phone and the other one is a hidden client on the kid's computer.
---

### The client on the kid's computer checks the screen of the computer in a static interval for any sexual content and if such content was found it is uploaded to the database which causes a new popup message in their parent's phone on real time to notify them.

### The computer client uses Firebase real-time database as a link between the computer and phone client applications.

---
### The server is used to check the screen every static interval to see if it displaying any sexual content, saving the user credentials using AES encryption and linking the client side applications with Firebase real-time database.

---
## About the detection of sexual content:

### I implemented using Tensorflow a model containing about 50,000 pictures containing sexual content to learn from. Obviously considering the dataset the algorithm is not the best however it can be implemented just as easily of a much bigger dataset. The code for the model is inside the tensorflow model, for obvious reasons the dataset is not attached.

### The learning process was done on my personal computer over 3 days considering the equipment I have.
---

### ***Written by: Adam Akiva***
### ***Email: adam.akiva14@gmail.com***