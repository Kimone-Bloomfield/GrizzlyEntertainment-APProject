package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;

import model.Message;
import model.Rental;
import model.Equipment;
import model.Staff;
import model.Transactions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Client {
    private ObjectInputStream objIs;
    private ObjectOutputStream objOs;
    private Socket connectionSocket;
    private String action = "";
    private boolean signUpSuccessful = false;
    private boolean loginInSuccessful = false;
    private static final Logger logger = LogManager.getLogger(Client.class);

    public Client() {
        // Initialize the client by creating a connection and configuring streams
        this.createConnection();
        this.configureStreams();
    }

    private void createConnection() {
        try {
            // Create a socket to connect to the server
            connectionSocket = new Socket("127.0.0.1", 1024);
            logger.info("Connection created-Client");
        } catch (IOException ex) {
            // Log an error if there is an issue creating the connection
            logger.error("Error creating connection: " + ex.getMessage());
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
            // Log an error if there is an issue configuring the streams
            logger.error("Error configuring streams: " + ex.getMessage());
        }
    }

    public void closeConnection() {
        try {
            // Close the output stream, input stream, and the connection socket
            objOs.close();
            objIs.close();
            connectionSocket.close();
        } catch (IOException ex) {
            // Log an error if there is an issue closing the connection
            logger.error("Error closing connection: " + ex.getMessage());
        }
    }

    public void sendAction(String action) {
        // Set the action to be sent to the server
        this.action = action;
        try {
            // Write the action to the output stream and flush
            objOs.writeObject(action);
            objOs.flush();
        } catch (IOException e) {
            // Log an error if there is an issue sending the action
            logger.error("Error sending action: " + e.getMessage());
        }
    }

    //--------------------------SEND CUSTOMER/STAFF/EQUIPMEMT/MESSAGE OBJECT-----------------------------------
    
    public void sendStaff(Staff staObj) {
        try {
            // Write the Staff object to the ObjectOutputStream
            objOs.writeObject(staObj);
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
        }
    }

    public void sendLoginCustomer(String cust_id, String cust_password) {
        try {
            // Write customer ID and password to the ObjectOutputStream
            objOs.writeObject(cust_id); 
            objOs.writeObject(cust_password);
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
        }
    }

    public void sendLoginStaff(String emp_id, String emp_password) {
        try {
            // Write employee ID and password to the ObjectOutputStream
            objOs.writeObject(emp_id);
            objOs.writeObject(emp_password);
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
        }
    }
	
    public void sendEquipment(Equipment equipment) {
        try {
            // Write the Equipment object to the ObjectOutputStream
            objOs.writeObject(equipment);
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
        }
    }

    public void sendCustomerMessage(Message message) {
        try {
            // Write the Message object to the ObjectOutputStream
            objOs.writeObject(message);
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
        }
    }

    public void sendStatus(Equipment equipment) {
        try {
            // Write the Equipment object to the ObjectOutputStream
            objOs.writeObject(equipment);
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
        }
    }

    public void updateStatus(Rental rental) {
        try {
            // Write the Rental object to the ObjectOutputStream
            objOs.writeObject(rental);
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
        }
    }

    public List<Equipment> sendViewEquipmentCategoryRequest(Equipment obj) {
        try {
            // Write the Equipment object to the ObjectOutputStream
            objOs.writeObject(obj);
            // Receive and return the list of Equipment objects from the ObjectInputStream
            return receiveEquipmentList();
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
            // Return an empty list in case of an exception
            return new ArrayList<>();
        }
    }

    public List<Equipment> equipmentList(List<Equipment> equipmentList) {
        try {
            // Write the list of Equipment objects to the ObjectOutputStream
            objOs.writeObject(equipmentList);
            // Receive and return the list of Equipment objects from the ObjectInputStream
            return receiveEquipmentList();
        } catch (IOException e) {
            // Log any IOException that occurs during the process
            logger.error(e);
            // Return an empty list in case of an exception
            return new ArrayList<>();
        }
    }

    public List<Equipment> receiveEquipmentList() {
        try {
            // Read and return the list of Equipment objects from the ObjectInputStream
            return (List<Equipment>) objIs.readObject();
        } catch (ClassNotFoundException ex) {
            // Log any ClassNotFoundException that occurs during the process
            logger.error(ex);
        } catch (IOException ex) {
            // Log any IOException that occurs during the process
            logger.error(ex);
        }
        // Return an empty list in case of an exception
        return new ArrayList<>();
    }

    public List<Message> receiveAllCustomerMessages() {
        try {
            // Read and return the list of Message objects from the ObjectInputStream
            return (List<Message>) objIs.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            // Log any ClassNotFoundException or IOException that occurs during the process
            logger.error(ex);
        }
        // Return an empty list in case of an exception
        return new ArrayList<>();
    }

    public void sendResponseToCustomerMessage(int messageID, String empmessage) {
        try {
            // Write message ID and employee message to the ObjectOutputStream
            objOs.writeObject(messageID);
            objOs.writeObject(empmessage);
        } catch (IOException ex) {
            // Log any IOException that occurs during the process
            logger.error(ex);
        }
    }

    public List<Rental> receiveRentalRequest() {
        try {
            // Read and return the list of Rental objects from the ObjectInputStream
            return (List<Rental>) objIs.readObject();
        } catch (ClassNotFoundException | IOException ex) {
            // Log any ClassNotFoundException or IOException that occurs during the process
            logger.error(ex);
        }
        // Return an empty list in case of an exception
        return new ArrayList<>();
    }

    public List<Rental> sendRentalRequest(Rental selectedRental) {
        try {
            // Read and return the list of Rental objects from the ObjectInputStream
            return (List<Rental>) objIs.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Log any IOException or ClassNotFoundException that occurs during the process
            logger.error(e);
            // Return an empty list in case of an exception
            return Collections.emptyList();
        }
    }

     public boolean receiveRentalResponse() {
        try {
            // Read and return the boolean value from the ObjectInputStream
            return (boolean) objIs.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Log any IOException or ClassNotFoundException that occurs during the process
            logger.error(e);
        }
        // Return false in case of an exception
        return false;
    }

    public List<Transactions> retrieveTransactionById(String customerId) {
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

    public List<Transactions> receiveInvoice() {
        try {
            // Read and return the list of Transactions objects from the ObjectInputStream
            return (List<Transactions>) objIs.readObject();
        } catch (IOException | ClassNotFoundException e) {
            // Log any IOException or ClassNotFoundException that occurs during the process
            logger.error(e);
        }
        // Return an empty list in case of an exception
        return new ArrayList<>();
    }
    
    public void denyRentalRequest(Rental rental) {
    	try
    	{
    	objOs.writeObject(rental);
    	}catch(IOException e)
    	{
    		logger.error(e);
    	}
    }


    
    public void receiveResponse() {
        try {
            // Check the type of action and handle the response accordingly
            if (action.equalsIgnoreCase("Signup Customer") || action.equalsIgnoreCase("Signup Employee")) {
                boolean flag = (boolean) objIs.readObject();
                signUpSuccessful = flag;
                if (signUpSuccessful) {
                    // Display success message for successful signup
                    JOptionPane.showMessageDialog(null, "Record Added Successfully", "Add Record Status",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed signup
                    JOptionPane.showMessageDialog(null, "Failed to add record", "Record Add Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Login Customer") || action.equalsIgnoreCase("Login Employee")) {
                boolean flag = (boolean) objIs.readObject();
                loginInSuccessful = flag;
                if (loginInSuccessful) {
                    // Display success message for successful login
                    JOptionPane.showMessageDialog(null, "Login is successful", "Successful Login",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed login
                    JOptionPane.showMessageDialog(null, "Login Failed", "Failed Login",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Add Equipment")) {
                boolean flag = (boolean) objIs.readObject();
                if (flag) {
                    // Display success message for successful equipment addition
                    JOptionPane.showMessageDialog(null, "Equipment Added", "Equipment Added",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed equipment addition
                    JOptionPane.showMessageDialog(null, "Failed to add equipment", "Equipment Add Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("View Equipment Category")) {
                boolean flag = (boolean) objIs.readObject();
                if (!flag) {
                    // Display message if the category is not found
                    JOptionPane.showMessageDialog(null, "This is not a category", "Category not found",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("View Equipment")) {
                boolean flag = (boolean) objIs.readObject();
                if (flag) {
                    // Display success message for successful equipment viewing
                    JOptionPane.showMessageDialog(null, "Equipment Viewed Successfully", "View Equipment Status",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed equipment viewing
                    JOptionPane.showMessageDialog(null, "Failed to view equipment", "View Equipment Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Get Customer Messages")) {
                List<Message> messages = (List<Message>) objIs.readObject();
                // Process and display the messages as needed
            } else if (action.equalsIgnoreCase("Respond to Customer")) {
                int messageID = (int) objIs.readObject();
                boolean flag = (boolean) objIs.readObject();
                if (flag) {
                    // Display success message for successful response
                    JOptionPane.showMessageDialog(null, "Response sent successfully", "Response Status", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed response
                    JOptionPane.showMessageDialog(null, "Failed to send response", "Response Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("View Rental Request")) {
                boolean flag = (boolean) objIs.readObject();
                if (flag) {
                    // Display success message for successful rental request viewing
                    JOptionPane.showMessageDialog(null, "Rental Request Viewed Successfully", "View Rental Request Status",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed rental request viewing
                    JOptionPane.showMessageDialog(null, "Failed to view rental request", "View Rental Request Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Update Status")) {
                boolean flag = (boolean) objIs.readObject();
                if (flag) {
                    // Display success message for successful status update
                    JOptionPane.showMessageDialog(null, "Status Updated Successfully", "Update Status Status",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed status update
                    JOptionPane.showMessageDialog(null, "Failed to update status", "Update Status Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Get Status")) {
                boolean flag = (boolean) objIs.readObject();
                if (flag) {
                    // Display success message for successful status retrieval
                    JOptionPane.showMessageDialog(null, "Status Retrieved Successfully", "Get Status Status",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed status retrieval
                    JOptionPane.showMessageDialog(null, "Failed to retrieve status", "Get Status Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Accept Rental Request")) {
                boolean flag = (boolean) objIs.readObject();
                if (flag) {
                    // Display success message for successful rental request acceptance
                    JOptionPane.showMessageDialog(null, "Rental Request Accepted Successfully", "Accept Rental Request Status",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    // Display error message for failed rental request acceptance
                    JOptionPane.showMessageDialog(null, "Failed to accept rental request", "Accept Rental Request Failed",
                            JOptionPane.ERROR_MESSAGE);
                }
            } else if (action.equalsIgnoreCase("Retrieve Transaction by ID")) {
                boolean flag = (boolean) objIs.readObject();
            }
            else if (action.equalsIgnoreCase("Deny Request")) {
                boolean flag = (boolean) objIs.readObject();
            }
        } catch (SocketException se) {
            // Log SocketException
            logger.error("SocketException: " + se.getMessage());
        } catch (ClassCastException | ClassNotFoundException | IOException ex) {
            // Log other exceptions
            logger.error(ex);
        }
    }

    // Getter methods for signup and login success flags
    public boolean isSignUpSuccessful() {
        return signUpSuccessful;
    }

    public boolean isLoginInSuccessful() {
        return loginInSuccessful;
    }
	
}
