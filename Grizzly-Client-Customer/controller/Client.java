package controller;

import org.apache.logging.log4j.*;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import model.Customer;
import model.Message;
import model.Rental;
import model.Transactions;
import model.Equipment;
 
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
    private ObjectInputStream objIs;
    private ObjectOutputStream objOs;
    private Socket connectionSocket;
    private String action = "";
    private boolean signUpSuccessful = false;
    private boolean loginInSuccessful= false;
    private static final Logger logger = LogManager.getLogger(Client.class);

    public Client() {
        this.createConnection();
        this.configureStreams();
    }

    private void createConnection() {
        try {
            // Create a socket to connect to the server
            connectionSocket = new Socket("127.0.0.1", 1024);
            logger.info("Connection created-Client");
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }

    private void configureStreams() {
        try {
            // Create an input stream to receive data from the server
            objIs = new ObjectInputStream(connectionSocket.getInputStream());

            // Create an output stream to send data to the server
            objOs = new ObjectOutputStream(connectionSocket.getOutputStream());

            logger.info("Streams configured-Client");
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void closeConnection() {
        try {
            objOs.close();
            objIs.close();
            connectionSocket.close();
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    public void sendAction(String action) {
        this.action = action;
        try {
            objOs.writeObject(action);
            System.out.println("action sent");
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Send Customer object to the server
    public void sendCustomer(Customer cusObj) {
        try {
            objOs.writeObject(cusObj);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Send login information for Customer
    public void sendLoginCustomer(String cust_id, String cust_password) {
        try {
            objOs.writeObject(cust_id); // Send the user ID
            objOs.writeObject(cust_password); // Send the password
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Send login information for Staff
    public void sendLoginStaff(String emp_id, String emp_password) {
        try {
            objOs.writeObject(emp_id); // Send the user ID
            objOs.writeObject(emp_password); // Send the password
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Request to view all equipment
    public List<Equipment> sendViewEquipmentRequest() {
        //sendAction("View Equipment");
        return receiveEquipmentList();
    }

    // Request to view equipment by category
    public List<Equipment> sendViewEquipmentCategoryRequest(String category) {
        try {
            objOs.writeObject(category);
            return receiveEquipmentList();
        } catch (IOException e) {
            logger.error(e);
            return new ArrayList<>();
        }
    }

    // Send request to get status for a specific equipment
    public void sendStatus(String equipmentId) {
        try {
            objOs.writeObject("Get Status");
            objOs.writeObject(equipmentId);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Update status of a rental
    public void updateStatus(Rental rental) {
        try {
            objOs.writeObject(rental);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Receive a list of equipment from the server
    private List<Equipment> receiveEquipmentList() {
        try {
            return (List<Equipment>) objIs.readObject();
        } catch (ClassNotFoundException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
        return new ArrayList<>();
    }

    // Send equipment object to the server
    public void sendEquipment(Equipment equipment) {
        try {
            objOs.writeObject(equipment);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Send customer message to the server
    public void sendCustomerMessage(Message message) {
        try {
            objOs.writeObject(message);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Send rental request to the server
    public void sendRentalRequest(Rental rental) {
        try {
            objOs.writeObject(rental);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    // Retrieve transactions by customer ID from the server
    public List<Transactions> retrieveTransactionById(String customerId) {
        try {
            objOs.writeObject(customerId);
            return receiveInvoice();
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    // Receive a list of transactions (invoice) from the server
    public List<Transactions> receiveInvoice() {
        try {
            return (List<Transactions>) objIs.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e);
        }
        return new ArrayList<>();
    }

    // Get customer messages by customer ID from the server
    public List<Message> getCustomerMessagesById(String customerId) {
        try {
            objOs.writeObject(customerId);
            return (List<Message>) objIs.readObject();
        } catch (IOException | ClassNotFoundException e) {
            logger.error(e);
        }
        return new ArrayList<>();
    }
    public List<Transactions> retrieveTransactionByIdCust(String customerId) {
        try {
            // Write customer ID to the ObjectOutputStream
            objOs.writeObject(customerId);
            // Receive and return the list of Transactions objects from the ObjectInputStream
            return receiveInvoice();
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
            // Return an empty list in case of an exception
            return new ArrayList<>();
        }
    }
    
 // Method to receive responses from the server
    public void receiveResponse() {
        try {
            // Check the action type and perform appropriate actions
            if (action.equalsIgnoreCase("Signup Customer")) {
                // Handling signup response
                Boolean flag = (Boolean) objIs.readObject();
                signUpSuccessful = flag;
                if (signUpSuccessful) {
                    JOptionPane.showMessageDialog(null, "Record Added Successfully", "Add Record Status",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Login Customer") || action.equalsIgnoreCase("Login Employee")) {
                // Handling login response
                Boolean flag = (Boolean) objIs.readObject();
                loginInSuccessful = flag;
                if (loginInSuccessful) {
                    JOptionPane.showMessageDialog(null, "Login is successful", "Successful Login",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Add Equipment")) {
                // Handling response for adding equipment
                Boolean flag = (Boolean) objIs.readObject();
            } else if (action.equalsIgnoreCase("View Equipment")) {
                // Handling response for viewing equipment
                Boolean flag = (Boolean) objIs.readObject();
            } else if (action.equalsIgnoreCase("View Equipment Category")) {
                // Handling response for viewing equipment category
                Boolean flag = (Boolean) objIs.readObject();
                if (!flag) {
                    JOptionPane.showMessageDialog(null, "This is not a category", "Category not found",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Customer Message")) {
                // Handling response for customer message
                Boolean flag = (Boolean) objIs.readObject();
            } else if (action.equalsIgnoreCase("Rental Request")) {
                // Handling response for rental request
                try {
                    Boolean isEquipmentBooked = (Boolean) objIs.readObject();
                    if (isEquipmentBooked != null && !isEquipmentBooked) {
                        JOptionPane.showMessageDialog(null, "Equipment ID is already booked", "Booking Error",
                                JOptionPane.ERROR_MESSAGE);
                    } else {
                    	JOptionPane.showMessageDialog(null, "Booked Successfully", "Booking",
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException | ClassNotFoundException ex) {
                    ex.printStackTrace();
                    logger.error(ex);
                }
            } else if (action.equalsIgnoreCase("Update Status")) {
                // Handling response for updating status
                Boolean flag = (Boolean) objIs.readObject();
            } else if (action.equalsIgnoreCase("Get Status")) {
                // Handling response for getting status
                Boolean flag = (Boolean) objIs.readObject();
            } else if (action.equalsIgnoreCase("Retrieve Transaction by ID")) {
                // Handling response for retrieving transaction by ID
                boolean flag = (boolean) objIs.readObject();
            } else if (action.equalsIgnoreCase("Customer Message by ID")) {
                // Handling response for customer message by ID
                boolean flag = (boolean) objIs.readObject();
            }
            else if (action.equalsIgnoreCase("Retrieve Transaction by ID Cust")) {
                boolean flag = (boolean) objIs.readObject();
            }
            
        } catch (ClassCastException ex) {
            logger.error(ex);
        } catch (ClassNotFoundException ex) {
            logger.error(ex);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    // Getter method to check if signup was successful
    public boolean isSignUpSuccessful() {
        return signUpSuccessful;
    }

    // Getter method to check if login was successful
    public boolean isLoginInSuccessful() {
        return loginInSuccessful;
    }
	
}

