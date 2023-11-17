package controller;

import java.awt.Container;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.text.Document;

import model.Customer;
import model.Customer.CustomerService;
import model.Equipment;
import model.Message;
import model.Rental;
import model.Staff;
import model.Transactions;

import org.apache.logging.log4j.*;
import controller.SessionFactoryBuilder;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.UUID;


public class Server extends Thread implements CustomerService{
    private ObjectOutputStream objOs;
    private ObjectInputStream objIs;
    private ServerSocket serverSocket;
    private Socket connectionSocket;
    private static Connection dbConn = null;
    private java.sql.Statement stmt;
    private ResultSet result = null;
    private boolean registrationSuccessful;
    private boolean loginInSuccessful;
    private List<Equipment> equipmentInventory = new ArrayList<>();
    private static final Logger logger= LogManager.getLogger(Server.class);
    int clientNo;

    public Server() 
    {
        this.createConnection();
                
    }
    
    public Server(Socket inSocket, int ClientNo) {
        this.connectionSocket = inSocket;
        this.clientNo = ClientNo;
    }

    private void createConnection() {
       try {
            // Create a new instance of the serverSocket listening on port 1024
            serverSocket = new ServerSocket(1024);
            logger.info("Server connection created");
        } catch (IOException ex) {
           logger.error(ex);
       }
    }

    public void run() {
        try {
        	if (connectionSocket == null) {
                logger.error("Connection socket is null. Aborting.");
                return;
           }
            // Instantiate the output stream
            objOs = new ObjectOutputStream(connectionSocket.getOutputStream());

            // Instantiate the input stream
            objIs = new ObjectInputStream(connectionSocket.getInputStream());
            logger.info("Server Streams configured");

            getDatabaseConnection();

            String action = (String) objIs.readObject();
            logger.info("Received action: " + action);

            if (action.equals("Signup Customer")) {
                Customer customer = (Customer) objIs.readObject();
                signupCustomer(customer);
            } else if (action.equals("Signup Employee")) {
                Staff employee = (Staff) objIs.readObject();
                signupEmployee(employee);
            } else if (action.equals("Login Customer")) {
                String cust_id = (String) objIs.readObject();
                String cust_password = (String) objIs.readObject();
                loginCustomer(cust_id, cust_password);
            } else if (action.equals("Login Employee")) {
                String emp_id = (String) objIs.readObject();
                String emp_password = (String) objIs.readObject();
                loginEmployee(emp_id, emp_password);
            } else if (action.equals("Add Equipment")) {
                Equipment equipment = (Equipment) objIs.readObject();
                addEquipment(equipment);
            } else if (action.equals("View Equipment")) {
                getAllEquipment();
            } else if (action.equals("View Equipment Category")) {
                String equipmentCategory = (String) objIs.readObject();
                viewEquipmentCategory(equipmentCategory);
                
            } else if (action.equals("Customer Message")) {
                Message message = (Message) objIs.readObject();
                addCustomerMessage(message);
            } else if (action.equals("Get Customer Messages")) {
                getAllCustomerMessages();
            } else if (action.equals("Respond to Customer")) {
                int messageID = (int) objIs.readObject();
                String response = (String) objIs.readObject();
                respondToCustomerMessage(messageID, response);
            }
            else if (action.equals("Rental Request"))
            {
            	Rental rental = (Rental) objIs.readObject();
            	createRentalEquipment(rental);
                
            }
            else if (action.equals("View Rental Requests")) {
                getAllRentalRequests();
            }
            else if (action.equals("Accept Rental Request")) {
            	logger.info("Before accepting request");
            	Rental rental = (Rental) objIs.readObject();
            	acceptRentalRequest(rental);
            	objOs.flush();
            }
            else if (action.equals("Retrieve Transaction by ID")) {
                String customerId = (String) objIs.readObject();
                retrieveTransactionById(customerId);
            }
            else if (action.equals("Customer Message by ID")) {
                String customerId = (String) objIs.readObject();
                getCustomerMessagesById(customerId);
            }
            else if (action.equals("Deny Request")) {
            	Rental rental = (Rental) objIs.readObject();
               denyRentalRequest(rental);
            }
            else if (action.equals("Retrieve Transaction by ID Cust")) {
                String customerId = (String) objIs.readObject();
                retrieveTransactionByIdCust(customerId);
            }
            
         
        }catch (IOException | ClassNotFoundException ex) {
            logger.error(ex);
        }
        finally
        {
        	closeConnection();
        }
        
    }
    
    
    
    private static Connection getDatabaseConnection() {
    	if(dbConn==null) {
        try {
        	String url = "jdbc:mysql://localhost:3306/grizzly";
            dbConn = DriverManager.getConnection(url, "root", "");
            
            JOptionPane.showMessageDialog(null, "DB Connection Established","CONNECTION STATUS",
            		JOptionPane.INFORMATION_MESSAGE);
            
        } catch (SQLException ex) {
        	JOptionPane.showMessageDialog(null, "could not connect to database\n"+ex,"Connection Failure",
            		JOptionPane.ERROR_MESSAGE);
        }
    }
		return dbConn;
}
    
    
    
    private void closeConnection() {
        try {
            if (objOs != null) {
                objOs.close();
            }

            if (objIs != null) {
                objIs.close();
            }

            if (connectionSocket != null) {
                connectionSocket.close();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("Connection closed");
        }
    }


 //----------------------------ADD TO FILE -----------------------------------------------------  

    @Override
	public void signupCustomer(Customer customer) {
       

        String sql = "INSERT INTO grizzly.authcustomer(cust_id, cust_password, cust_firstName, cust_lastName, account) " +
                     "VALUES ('" + customer.getCust_id() + "','" + customer.getCust_password() + "','" +
                     customer.getCust_firstName() + "','" + customer.getCust_lastName() + "','" +
                     customer.getAccount() + "');";

        try {
            stmt = dbConn.createStatement();
            if (stmt.executeUpdate(sql) == 1) {
                objOs.writeObject(true);
                registrationSuccessful = true;
            } else {
                objOs.writeObject(false);
                registrationSuccessful = false;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            logger.error(ioe);
        } catch (SQLException e) {
            e.printStackTrace();
            logger.error(e);
        }
    }

    
    private void signupEmployee(Staff employee) {
        String sql = "INSERT INTO grizzly.authemployee(emp_id,emp_password, emp_firstName, emp_lastName)" 
                    + "VALUES ('" + employee.getEmp_id()+ "','"+ employee.getEmp_password() + "','"  + employee.getEmp_firstName()
                    + "','" + employee.getEmp_lastName() + "');";      
        try {
        	stmt = dbConn.createStatement();
        	System.out.println("Before registration successful-server");
            if ((stmt.executeUpdate(sql) == 1)){
                objOs.writeObject(true);
                registrationSuccessful = true;
                System.out.println("After registration successful-server");
            } else {
                objOs.writeObject(false);
                registrationSuccessful = false;
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            logger.error(ioe);
         } catch (SQLException e) {
        e.printStackTrace();
        logger.error(e);
    }
   }
    
    @Override
    public void loginCustomer(String cust_id, String cust_password) {
        try {
            String query = "SELECT * FROM authcustomer WHERE cust_id = ? AND cust_password = ?";
            PreparedStatement preparedStatement = dbConn.prepareStatement(query);
            preparedStatement.setString(1, cust_id);
            preparedStatement.setString(2, cust_password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Successful login, send a success signal to the client
                objOs.writeObject(true);
                loginInSuccessful = true;
            } else {
                // Failed login, send a failure signal to the client
                objOs.writeObject(false);
                loginInSuccessful = false;
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }

    private void loginEmployee(String emp_id, String emp_password) {
        try {
            String query = "SELECT * FROM authemployee WHERE emp_id = ? AND emp_password = ?";
            PreparedStatement preparedStatement = dbConn.prepareStatement(query);
            preparedStatement.setString(1, emp_id);
            preparedStatement.setString(2, emp_password);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Successful login, send a success signal to the client
                objOs.writeObject(true);
                loginInSuccessful = true;
            } else {
                // Failed login, send a failure signal to the client
                objOs.writeObject(false);
                loginInSuccessful = false;
            }
        } catch (SQLException | IOException ex) {
            logger.error(ex);
        }
        
    }

    private void addEquipment(Equipment equipment) {
        String sql = "INSERT INTO grizzly.equipment(equipment_id, equipment_name, equipment_status, equipment_category, cost)" 
                + " VALUES ('" + equipment.getEquipment_id() + "','" + equipment.getEquipment_name() + "','" + equipment.getEquipment_status() + "','"  
                + equipment.getEquipment_category() + "','" + equipment.getCost() + "');"; 

        try {
            stmt = dbConn.createStatement();
            if (stmt.executeUpdate(sql) == 1) {
                objOs.writeObject(true); 
            } else {
                objOs.writeObject(false);
            }
        } catch (IOException ioe) { 
            logger.error(ioe);
        } catch (SQLException e) {
            logger.error(e);
        }
    }


    private void viewEquipmentCategory(String equipment_category) {
        try (Session session = SessionFactoryBuilder.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<Equipment> criteriaQuery = criteriaBuilder.createQuery(Equipment.class);
            Root<Equipment> root = criteriaQuery.from(Equipment.class);

            criteriaQuery.select(root)
                    .where(criteriaBuilder.equal(root.get("equipment_category"), equipment_category));

            List<Equipment> categoryEquipment = session.createQuery(criteriaQuery).getResultList();

            objOs.writeObject(categoryEquipment); // Send the list to the client
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void getAllEquipment() {
        try (Session session = SessionFactoryBuilder.getSessionFactory().openSession()) {
            CriteriaQuery<Equipment> criteriaQuery = session.getCriteriaBuilder().createQuery(Equipment.class);
            criteriaQuery.select(criteriaQuery.from(Equipment.class));

            List<Equipment> equipmentList = session.createQuery(criteriaQuery).getResultList();

            // Send the list of equipment to the client
            objOs.writeObject(equipmentList);
        } catch (IOException e) {
            logger.error(e);
        }
    }



    private void addCustomerMessage(Message message) throws IOException {
        try (Session session = SessionFactoryBuilder.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();

            try {
                session.save(message);
                transaction.commit();
                objOs.writeObject(true);
            } catch (Exception ex) {
                if (transaction != null) {
                    transaction.rollback();
                }
                logger.error(ex);
            }
        }
    }


    private void getAllCustomerMessages() throws IOException {
        try (Session session = SessionFactoryBuilder.getSessionFactory().openSession()) {
            List<Message> messages = session.createQuery("FROM Message", Message.class).getResultList();
            objOs.writeObject(messages);
        } catch (IOException ex) {
            logger.error(ex);
        }
    }

    private void respondToCustomerMessage(int message_id, String empmessage) throws IOException {
        try (Session session = SessionFactoryBuilder.getSessionFactory().openSession()) {
            Transaction transaction = session.beginTransaction();
            
            Message message = session.get(Message.class, message_id); 
            message.setStatus("Responded");
            message.setEmpMessage(empmessage);
           
            
            transaction.commit();
        }
    }

    private void getCustomerMessagesById(String cust_id) throws IOException {
        try (Session session = SessionFactoryBuilder.getSessionFactory().openSession()) {
            Query<Message> query = session.createQuery("FROM Message WHERE cust_id = :cust_id", Message.class);
            query.setParameter("cust_id", cust_id);

            List<Message> messages = query.getResultList();
            objOs.writeObject(messages);
        }
    }



    private void createRentalEquipment(Rental rental) throws IOException {
        String checkExistenceQuery = "SELECT COUNT(*) FROM rental WHERE equipment_id = ?";
        try {
            PreparedStatement checkStatement = dbConn.prepareStatement(checkExistenceQuery);
            checkStatement.setString(1, rental.getEquipment_id());
            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next() && resultSet.getInt(1) > 0) {
            	JOptionPane.showMessageDialog(null, "This equipment is already requested.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            	objOs.writeObject(false);
                return;
                //throw new SQLException("Equipment ID already booked");
            }

            // Continue with inserting the rental record into the database
            String insertRentalQuery = "INSERT INTO rental (customer_id, customer_name, equipment_id, equipment_name, date, equipment_category, cost) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement insertStatement = dbConn.prepareStatement(insertRentalQuery);
            insertStatement.setString(1, rental.getCustomer_id());
            insertStatement.setString(2, rental.getCustomer_name());
            insertStatement.setString(3, rental.getEquipment_id());
            insertStatement.setString(4, rental.getEquipment_name());
            insertStatement.setDate(5, new java.sql.Date(rental.getDate().getTime()));
            insertStatement.setString(6, rental.getEquipment_category());
            insertStatement.setDouble(7, rental.getCost());
            insertStatement.executeUpdate();
            
            JOptionPane.showMessageDialog(null, "Equipment Requested.", "Booking Success", JOptionPane.INFORMATION_MESSAGE);
        	objOs.writeObject(true);
        	
        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }


    private void getAllRentalRequests() {
        String selectQuery = "SELECT * FROM rental";
        try {
            stmt = dbConn.createStatement();
            ResultSet result = stmt.executeQuery(selectQuery);

            List<Rental> rentalList = new ArrayList<>();
            while (result.next()) {
                Rental rental = new Rental();
                rental.setEquipment_id(result.getString("equipment_id"));
                rental.setEquipment_name(result.getString("equipment_name"));
                rental.setDate(result.getDate("date"));
                rental.setCustomer_id(result.getString("customer_id"));
                rental.setCustomer_name(result.getString("customer_name"));
                rental.setEquipment_category(result.getString("equipment_category"));
                rental.setCost(result.getDouble("cost"));
                rentalList.add(rental);
            }

            // Send the list of rental requests to the client
            objOs.writeObject(rentalList);
        } catch (SQLException | IOException e) {
            logger.error(e);
        }
    }
    

    // Add a method to get the rental cost based on equipment_id
    private double getRentalCost(String equipmentId) throws SQLException {
        String costQuery = "SELECT cost FROM equipment WHERE equipment_id = ?";
        PreparedStatement costStatement = dbConn.prepareStatement(costQuery);
        costStatement.setString(1, equipmentId);
        ResultSet costResult = costStatement.executeQuery();

        if (costResult.next()) {
            return costResult.getDouble("cost");
        }

        return 0.0; // Default value if cost is not found
    }
    
    private void denyRentalRequest(Rental rental) throws IOException {
        try (Session session = SessionFactoryBuilder.getSessionFactory().openSession()) {
            // Delete the rental request from the database
            Transaction transaction = session.beginTransaction();
            session.delete(rental);
            transaction.commit();
            
            // Send denial message to the customer
            String customerId = rental.getCustomer_id();
            String denialMessage = "Your rental request for equipment '" + rental.getEquipment_name() +
                    "' has been denied. Please contact us for more information.";
            
            Message denialMessageEntity = new Message();
            denialMessageEntity.setCustID(customerId);
            denialMessageEntity.setEmpMessage(denialMessage);
            
            session.save(denialMessageEntity);
            
            objOs.writeObject(true); // Indicate success
        } catch (HibernateException | IOException e) {
            logger.error(e);
            objOs.writeObject(false); // Indicate failure
        }
    }

    
    private void acceptRentalRequest(Rental rental) {
        try {
            // Check if the customer has enough balance
            String checkBalanceQuery = "SELECT account FROM authcustomer WHERE cust_id = ?";
            PreparedStatement checkBalanceStatement = dbConn.prepareStatement(checkBalanceQuery);
            checkBalanceStatement.setString(1, rental.getCustomer_id());
            ResultSet balanceResult = checkBalanceStatement.executeQuery();

            if (balanceResult.next()) {
                double customerBalance = balanceResult.getDouble("account");
                double rentalCost = getRentalCost(rental.getEquipment_id()); // Implement this method

                if (customerBalance >= rentalCost) {
                    // Subtract the cost from the customer's account
                    String updateBalanceQuery = "UPDATE authcustomer SET account = account - ? WHERE cust_id = ?";
                    PreparedStatement updateBalanceStatement = dbConn.prepareStatement(updateBalanceQuery);
                    updateBalanceStatement.setDouble(1, rentalCost);
                    updateBalanceStatement.setString(2, rental.getCustomer_id());
                    updateBalanceStatement.executeUpdate();

                    // Insert the transaction into the transaction table
                    String insertTransactionQuery = "INSERT INTO transaction (transaction_id, transaction_date, customer_id, customer_name, equipment_id, equipment_name, date, equipment_category, cost) VALUES (NULL, ?, ?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertTransactionStatement = dbConn.prepareStatement(insertTransactionQuery);
                    insertTransactionStatement.setDate(1, new java.sql.Date(System.currentTimeMillis()));
                    insertTransactionStatement.setString(2, rental.getCustomer_id());
                    insertTransactionStatement.setString(3, rental.getCustomer_name());
                    insertTransactionStatement.setString(4, rental.getEquipment_id());
                    insertTransactionStatement.setString(5, rental.getEquipment_name());
                    insertTransactionStatement.setDate(6, new java.sql.Date(rental.getDate().getTime()));
                    insertTransactionStatement.setString(7, rental.getEquipment_category());
                    insertTransactionStatement.setDouble(8, rentalCost);
                    insertTransactionStatement.executeUpdate();

                    logger.info("Accept rental request-sever");
                    // Notify the client about the successful acceptance
                    objOs.writeObject(true);
                   // objOs.flush();
                } else {
                    // Notify the client that the customer has insufficient balance
                    objOs.writeObject(false);
                }
            }
        } catch (SQLException | IOException ex) {
            ex.printStackTrace();
            logger.error(ex);
        }
    }
    
    public void retrieveTransactionById(String customer_id) {
        String getTransactionByIdQuery = "SELECT transaction_id, transaction_date, customer_id, " +
                "customer_name, equipment_id, equipment_name, date, equipment_category, cost, cust_account " +
                "FROM transaction WHERE customer_id = ?";
        
        try (PreparedStatement ps = dbConn.prepareStatement(getTransactionByIdQuery)) {
            ps.setString(1, customer_id);
            
            try (ResultSet result = ps.executeQuery()) {
                List<Transactions> transactions = new ArrayList<>();

                while (result.next()) {
                    Transactions retrievedTransaction = new Transactions();
                    retrievedTransaction.setTransactionId(result.getInt("transaction_id"));
                    retrievedTransaction.setTransactionDate(result.getDate("transaction_date"));
                    retrievedTransaction.setCustomerId(result.getString("customer_id"));
                    retrievedTransaction.setCustomerName(result.getString("customer_name"));
                    retrievedTransaction.setEquipmentId(result.getString("equipment_id"));
                    retrievedTransaction.setEquipmentName(result.getString("equipment_name"));
                    retrievedTransaction.setDate(result.getDate("date"));
                    retrievedTransaction.setEquipmentCategory(result.getString("equipment_category"));
                    retrievedTransaction.setCost(result.getDouble("cost"));

                    double cost = result.getDouble("cost");
                    
                    

                    // Deduct equipment cost from customer's account and update transaction
                    updateCustomerAccountAndTransaction(customer_id, cost, retrievedTransaction);

                    transactions.add(retrievedTransaction);
                    
                    
                }

                objOs.writeObject(transactions);
               // System.out.println(transactions);

            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error("Error while processing retrieved transactions: {}", ex.getMessage());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("Error while retrieving transactions: {}", ex.getMessage());
        }
    }

    private void updateCustomerAccountAndTransaction(String customer_id, double cost, Transactions retrievedTransaction) {
        try {
            // Retrieve current account balance
            String getAccountBalanceQuery = "SELECT account FROM authcustomer WHERE cust_id = ?";
            
            try (PreparedStatement ps = dbConn.prepareStatement(getAccountBalanceQuery)) {
                ps.setString(1, customer_id);
                
                try (ResultSet result = ps.executeQuery()) {
                    if (result.next()) {
                        double currentBalance = result.getDouble("account");
                        double updatedBalance =currentBalance - cost  ;

                        // Update the customer's account balance
                        String updateBalanceQuery = "UPDATE authcustomer SET account = ? WHERE cust_id = ?";
                        
                        try (PreparedStatement updatePs = dbConn.prepareStatement(updateBalanceQuery)) {
                            updatePs.setDouble(1, updatedBalance);
                            updatePs.setString(2, customer_id);
                            updatePs.executeUpdate();

                            // Set the updated customer account balance in the transaction object
                            retrievedTransaction.setCustAccount(updatedBalance);
                            
                         // Inside the while loop
                            try (PreparedStatement updateTransactionPs = dbConn.prepareStatement("UPDATE transaction SET cust_account = ? WHERE transaction_id = ?")) {
                                updateTransactionPs.setDouble(1, updatedBalance);
                                updateTransactionPs.setInt(2, retrievedTransaction.getTransactionId());
                                updateTransactionPs.executeUpdate();
                            }

                        }
                    }
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("Error while updating account balance: {}", ex.getMessage());
        }
    }
    
    public void retrieveTransactionByIdCust(String customer_id) {
        String getTransactionByIdQuery = "SELECT transaction_id, transaction_date, customer_id, " +
                "customer_name, equipment_id, equipment_name, date, equipment_category, cost, cust_account " +
                "FROM transaction WHERE customer_id = ?";
        
        try (PreparedStatement ps = dbConn.prepareStatement(getTransactionByIdQuery)) {
            ps.setString(1, customer_id);
            
            try (ResultSet result = ps.executeQuery()) {
                List<Transactions> transactions = new ArrayList<>();

                while (result.next()) {
                    Transactions retrievedTransaction = new Transactions();
                    retrievedTransaction.setTransactionId(result.getInt("transaction_id"));
                    retrievedTransaction.setTransactionDate(result.getDate("transaction_date"));
                    retrievedTransaction.setCustomerId(result.getString("customer_id"));
                    retrievedTransaction.setCustomerName(result.getString("customer_name"));
                    retrievedTransaction.setEquipmentId(result.getString("equipment_id"));
                    retrievedTransaction.setEquipmentName(result.getString("equipment_name"));
                    retrievedTransaction.setDate(result.getDate("date"));
                    retrievedTransaction.setEquipmentCategory(result.getString("equipment_category"));
                    retrievedTransaction.setCost(result.getDouble("cost"));
                    retrievedTransaction.setCustAccount(result.getDouble("cust_account"));

                    transactions.add(retrievedTransaction);
                }

                objOs.writeObject(transactions);

            } catch (IOException ex) {
                ex.printStackTrace();
                logger.error("Error while processing retrieved transactions: {}", ex.getMessage());
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            logger.error("Error while retrieving transactions: {}", ex.getMessage());
        }
    }


}




        

