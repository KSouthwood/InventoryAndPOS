# [Inventory and POS System](https://hyperskill.org/projects/211)

## About

A wine merchant asked you to develop a system for receiving and organizing their inventory and sales. The task is simple
yet complex—you need to make a program that allows users to add and keep track of the incoming orders. It must employ
a database that stores the orders and shows from where the wine was acquired. Users should be also able to edit orders
so that they know that the supplier has been paid or the wine has been sold to wine lovers.

### Stage 1: [The inventory manager](https://hyperskill.org/projects/211/stages/1057/implement)

#### _Summary_

Write a program that includes a simple GUI that allows users to select a variety of wines, assign a supplier, and 
input how many cases are added.

##### _Description_

To start with, you need to build a simple GUI that allows the wine merchant to process the incoming orders from 
their suppliers.

The merchant has trouble remembering how much they paid for their incoming orders. For the first step, we'll design 
a window that first presents the three wine options: White Sauvignon, Red Merlot and Rosé Zinfandel. After they've 
chosen the wine to order, get the number of cases being ordered (minimum 1 and maximum 10) and the name of the 
supplier. Show a message in the window with the total number of bottles ordered and `Success` if everything is correct.

### Stage 2: [Supply orders](https://hyperskill.org/projects/211/stages/1058/implement)

#### _Summary_

Keep track of the received orders from multiple suppliers. Improve the GUI with a table that keeps all incoming orders.

##### _Description_

So far, we are leaving the incoming orders as they are, and the Merchant has lost track of the inventory. Now, we 
need to help the Merchant keep track of all their incoming orders in an extended table. Build a second GUI screen 
that contains a JTable with a detailed description of the orders. This view should also contain a filter button for 
each supplier.

### Stage 3: [Database](https://hyperskill.org/projects/211/stages/1059/implement)

#### _Summary_

Implement SQL functionality so that the program can save the status of the order even after closing the application.

##### _Description_

Now that we have the interface we want, let's keep the orders between invocations of the program by saving them to a 
SQL database. The filename of the database will be passed to the RootWindow class.

### Stage 4: [Inventory](https://hyperskill.org/projects/211/stages/1060/implement)

#### _Summary_

Add a third screen where the program can display the status of the inventory from the supplier orders.

### Stage 5: [Point of Sale](https://hyperskill.org/projects/211/stages/1061/implement)

#### _Summary_

Add a simple POS system that creates orders for customers. It should communicate with the inventory to see if users 
have enough stock of the specified wines to complete the order.
