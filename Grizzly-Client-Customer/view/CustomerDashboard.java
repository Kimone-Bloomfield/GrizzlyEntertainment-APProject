package view;

import java.awt.FlowLayout;
import java.awt.LayoutManager;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import controller.Client;
import model.Message;
import model.Rental;
import model.Transactions;
import model.Equipment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CustomerDashboard implements Serializable {
    private JFrame frame;
    private JDesktopPane desktopPane;
    private static final Logger logger= LogManager.getLogger(CustomerDashboard.class);

    public CustomerDashboard() {
        // Create the main window
        frame = new JFrame("Customer Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        // Create a desktop pane to hold internal frames
        desktopPane = new JDesktopPane();
        frame.setContentPane(desktopPane);

        // Create a menu bar
        JMenuBar menuBar = new JMenuBar();

        // Create menu items with mnemonics
        JMenu equipmentMenu = new JMenu("Equipment");
        equipmentMenu.setMnemonic('E');

        JMenu transactionsMenu = new JMenu("Transactions");
        transactionsMenu.setMnemonic('T');

        JMenu messageMenu = new JMenu("Message");
        messageMenu.setMnemonic('M');

        // Create menu items with keyboard shortcuts
        JMenuItem viewEquipmentByCategoryMenuItem = createMenuItem("View Equipment by Category", 'C');
        viewEquipmentByCategoryMenuItem.setToolTipText("View equipment by category");
        equipmentMenu.add(viewEquipmentByCategoryMenuItem);
        JMenuItem viewTransactionsMenuItem = createMenuItem("View Transactions", 'T');
        JMenuItem leaveMessageMenuItem = createMenuItem("Leave a message", 'M');
        JMenuItem viewMessagesMenuItem = createMenuItem("View Messages", 'V');
        messageMenu.add(viewMessagesMenuItem);

        viewEquipmentByCategoryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a dialog to prompt the user to enter the equipment category
                String category = JOptionPane.showInputDialog(frame, "Enter Equipment Category:");
                if (category != null && !category.isEmpty()) {
                	Client client= new Client();
                    client.sendAction("View Equipment Category");
                    List<Equipment> equipmentList = client.sendViewEquipmentCategoryRequest(category);
                	client.receiveResponse();
                	
                    // Display the equipment list in the internal frame
                    createInternalFrame("Equipment by Category: " + category, 500, 300, equipmentList);

                }
            }
        });                    


        viewTransactionsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Create a dialog to prompt the user to enter the customer ID
                String customerID = JOptionPane.showInputDialog(frame, "Enter Customer ID:");
                if (customerID != null && !customerID.isEmpty()) {
                    // Request transactions from the server
                    Client client = new Client();
                    client.sendAction("Retrieve Transaction by ID Cust");
                    List<Transactions> transactions = client.retrieveTransactionById(customerID);
                    client.receiveResponse();

                    // Display the transactions in a table
                    createTransactionInternalFrame(transactions);
                }
            }
        });


        leaveMessageMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createInternalFrameWithMessage("Leave a message", 400, 350);
            }
        });
        
        viewMessagesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String customerId = JOptionPane.showInputDialog(frame, "Enter Customer ID:");
                if (customerId != null && !customerId.isEmpty()) {
                    // Request customer messages from the server
                    Client client = new Client();
                    client.sendAction("Customer Message by ID");
                    List<Message> messages = client.getCustomerMessagesById(customerId);
                    client.receiveResponse();

                    // Display the messages in a table
                    createMessageInternalFrame(messages);
                }
            }
        });

        // Create tooltips for menu items
        viewEquipmentByCategoryMenuItem.setToolTipText("View available equipment");
        viewTransactionsMenuItem.setToolTipText("View past transactions");
        leaveMessageMenuItem.setToolTipText("Leave a message");

        // Add menu items to menus
        equipmentMenu.add(viewEquipmentByCategoryMenuItem);
        transactionsMenu.add(viewTransactionsMenuItem);
        messageMenu.add(leaveMessageMenuItem);

        // Add menus to the menu bar
        menuBar.add(equipmentMenu);
        menuBar.add(transactionsMenu);
        menuBar.add(messageMenu);

        // Set the menu bar for the frame
        frame.setJMenuBar(menuBar);

        // Make the frame visible
        frame.setVisible(true);
    }

    private JMenuItem createMenuItem(String label, char mnemonic) {
        JMenuItem menuItem = new JMenuItem(label);
        menuItem.setMnemonic(mnemonic);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(mnemonic, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        return menuItem;
    }

    private void createInternalFrame(String title, int width, int height, List<Equipment> equipmentList) {
        JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
        internalFrame.setSize(width, height);
        internalFrame.setLocation(100, 100);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Equipment ID");
        tableModel.addColumn("Equipment Name");
        tableModel.addColumn("Equipment Category");
        tableModel.addColumn("Cost");
        tableModel.addColumn("Request");

        for (Equipment equipment : equipmentList) {
            Object[] rowData = new Object[]{
                    equipment.getEquipment_id(),
                    equipment.getEquipment_name(),
                    equipment.getEquipment_category(),
                    equipment.getCost(),
                    "Request" // Add the request button
            };
            tableModel.addRow(rowData);
        }

        JTable equipmentTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(equipmentTable);

        equipmentTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());

        equipmentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = equipmentTable.getColumnModel().getColumnIndex("Request");
                int row = equipmentTable.rowAtPoint(e.getPoint());

                if (column == 4 && row >= 0) {
                    String equipmentId = tableModel.getValueAt(row, 0).toString();
                    String equipmentName = tableModel.getValueAt(row, 1).toString();

                    JPanel requestPanel = createRequestPanel(equipmentId, equipmentName, equipmentTable);
                    int result = JOptionPane.showConfirmDialog(internalFrame, requestPanel, "Request Equipment", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (result == JOptionPane.OK_OPTION) {
                        Date rentalDate = getDateFromRequestPanel(requestPanel);
                        String customer_id = ((JTextField) requestPanel.getComponent(1)).getText();
                        String customer_name = ((JTextField) requestPanel.getComponent(3)).getText();

                        if (rentalDate != null) {
                            Client client = new Client();
                            Rental rental = new Rental(customer_id, customer_name, equipmentId, equipmentName, rentalDate, equipmentTable.getValueAt(row, 2).toString(), Double.parseDouble(equipmentTable.getValueAt(row, 3).toString()));
                            client.sendAction("Rental Request");
                            client.sendRentalRequest(rental);

                            int equipmentStatusColumnIndex = 2;
                            int equipmentStatusRow = row;
                            tableModel.fireTableDataChanged();
                        }
                    }
                }
            }
        });

        internalFrame.getContentPane().add(scrollPane);
        desktopPane.add(internalFrame);
        internalFrame.setVisible(true);
    }
    
    private Date getDateFromRequestPanel(JPanel requestPanel) {
     Component[] components = requestPanel.getComponents();
      for (Component component : components) {
          if (component instanceof JTextField) {
             JTextField textField = (JTextField) component;
              if (textField.getName() != null && textField.getName().equals("dateTextField")) {
                  String dateString = textField.getText();
                  try {
                     SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); // Adjust the date format as needed
                      return dateFormat.parse(dateString);
                  } catch (ParseException e) {
                      logger.error(e);
                }
              }
          }
      }
      return null; // Return null if the date cannot be parsed
  }
    private void createMessageInternalFrame(List<Message> messages) {
        if (messages == null) {
            JOptionPane.showMessageDialog(frame, "Error retrieving messages. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JInternalFrame internalFrame = new JInternalFrame("Messages", true, true, true, true);
        internalFrame.setSize(500, 500);
        internalFrame.setLocation(100, 100);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Message ID");
        tableModel.addColumn("Customer ID");
        tableModel.addColumn("Message");
        tableModel.addColumn("Status");
        tableModel.addColumn("Employee Response");

        for (Message message : messages) {
            Object[] rowData = new Object[]{
                    message.getMessageID(),
                    message.getCustID(),
                    message.getCustMessage(),
                    message.getStatus(),
                    message.getEmpMessage()
            };
            tableModel.addRow(rowData);
        }

        JTable messageTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(messageTable);

        messageTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = messageTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    Message selectedMessage = messages.get(row);
                    // Display details of the selected message in a JOptionPane
                    displayMessageDetails(selectedMessage);
                }
            }
        });

        internalFrame.getContentPane().add(scrollPane);
        desktopPane.add(internalFrame);
        internalFrame.setVisible(true);
    }

    private void displayMessageDetails(Message message) {
        String messageDetails = "Message ID: " + message.getMessageID() + "\n" +
                "Customer ID: " + message.getCustID() + "\n" +
                "Message: " + message.getCustMessage() + "\n" +
                "Status: " + message.getStatus() + "\n" +
                "Employee Response: " + message.getEmpMessage();

        JOptionPane.showMessageDialog(frame, messageDetails, "Message Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void createTransactionInternalFrame(List<Transactions> transactions) {
        JInternalFrame internalFrame = new JInternalFrame("Transactions", true, true, true, true);
        internalFrame.setSize(500, 500);
        internalFrame.setLocation(100, 100);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Transaction ID");
        tableModel.addColumn("Transaction Date");

        for (Transactions transaction : transactions) {
            Object[] rowData = new Object[]{
                    transaction.getTransactionId(),
                    transaction.getTransactionDate()
            };
            tableModel.addRow(rowData);
        }

        JTable transactionTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(transactionTable);

        transactionTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = transactionTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    Transactions selectedTransaction = transactions.get(row);
                    // Display details of the selected transaction in a JOptionPane
                    displayTransactionDetails(selectedTransaction);
                }
            }
        });

        internalFrame.getContentPane().add(scrollPane);
        desktopPane.add(internalFrame);
        internalFrame.setVisible(true);
    }

    // Add this method to the CustomerDashboard class
    private void displayTransactionDetails(Transactions transaction) {
        String message = "Transaction ID: " + transaction.getTransactionId() + "\n" +
                "Transaction Date: " + transaction.getTransactionDate() + "\n" +
                "Customer ID: " + transaction.getCustomerId() + "\n" +
                "Customer Name: " + transaction.getCustomerName() + "\n" +
                "Equipment ID: " + transaction.getEquipmentId() + "\n" +
                "Equipment Name: " + transaction.getEquipmentName() + "\n" +
                "Rental Date: " + transaction.getDate() + "\n" +
                "Equipment Category: " + transaction.getEquipmentCategory() + "\n" +
                "Cost: " + transaction.getCost() + "\n" +
                "Customer Account: " + transaction.getCustAccount();

        JOptionPane.showMessageDialog(frame, message, "Transaction Details", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private JPanel createRequestPanel(String equipmentId, String equipmentName, JTable equipmentTable) {
        JPanel panel = new JPanel(new GridLayout(8, 2));

        JLabel custidLabel = new JLabel("Customer ID:");
        JTextField custidTextField = new JTextField();
        custidTextField.setEditable(true);

        JLabel custnameLabel = new JLabel("Customer Name:");
        JTextField custnameTextField = new JTextField();
        custnameTextField.setEditable(true);

        JLabel idLabel = new JLabel("Equipment ID:");
        JTextField idTextField = new JTextField(equipmentId);
        idTextField.setEditable(false);

        JLabel nameLabel = new JLabel("Equipment Name:");
        JTextField nameTextField = new JTextField(equipmentName);
        nameTextField.setEditable(false);

        JLabel categoryLabel = new JLabel("Equipment Category:");
        JTextField categoryTextField = new JTextField(equipmentTable.getValueAt(equipmentTable.getSelectedRow(), 2).toString());
        categoryTextField.setEditable(false);

        JLabel costLabel = new JLabel("Cost:");
        JTextField costTextField = new JTextField(equipmentTable.getValueAt(equipmentTable.getSelectedRow(), 3).toString());
        costTextField.setEditable(false);

        JLabel dateLabel = new JLabel("Date:");
        JTextField dateTextField = new JTextField();
        dateTextField.setName("dateTextField");

        panel.add(custidLabel);
        panel.add(custidTextField);
        panel.add(custnameLabel);
        panel.add(custnameTextField);
        panel.add(idLabel);
        panel.add(idTextField);
        panel.add(nameLabel);
        panel.add(nameTextField);
        panel.add(categoryLabel);
        panel.add(categoryTextField);
        panel.add(costLabel);
        panel.add(costTextField);
        panel.add(dateLabel);
        panel.add(dateTextField);

        return panel;
    }

    class ButtonRenderer extends DefaultTableCellRenderer {
        private JButton button;

        public ButtonRenderer() {
            button = new JButton("Request");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return button;
        }
    }

    private void createInternalFrameWithMessage(String title, int width, int height) {
        JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
        internalFrame.setSize(width, height);
        internalFrame.setLocation(100, 100);

        JPanel messagePanel = new JPanel(null);

        // Add cust_id input field
        JLabel custIdLabel = new JLabel("Your ID:");
        JTextField custIdTextField = new JTextField();
        custIdLabel.setBounds(10, 10, 80, 20);
        custIdTextField.setBounds(100, 10, 150, 20);

        // Add message input field
        JLabel messageLabel = new JLabel("Message:");
        JTextArea messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        messageLabel.setBounds(10, 40, 80, 20);
        
        JScrollPane messageScrollPane = new JScrollPane(messageTextArea);
        messageScrollPane.setBounds(10, 70, 250, 100);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(10, 180, 80, 25);
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String messageText = messageTextArea.getText();
                String custId = custIdTextField.getText(); // Get cust_id from the input field
                if (!messageText.isEmpty() && !custId.isEmpty()) {
                    // Create a new CustomerMessage object with the message and cust_id
                    System.out.println("Message Text before creating Message object: " + messageText);
                    Message customerMessage = new Message(custId, messageText, "New", null);
                    System.out.println("Message Text after creating Message object: " + customerMessage.getCustMessage());

                    // Send the message to the server
                    Client client = new Client();
                    client.sendAction("Customer Message");
                    client.sendCustomerMessage(customerMessage);
                    client.receiveResponse();

                    JOptionPane.showMessageDialog(frame, "Message submitted successfully", "Message Submission", JOptionPane.INFORMATION_MESSAGE);
                    messageTextArea.setText(""); // Clear the text area after submission
                    custIdTextField.setText(""); // Clear the cust_id field after submission
                    logger.info("Message Submitted");
                } else {
                    JOptionPane.showMessageDialog(frame, "Please enter both message and ID", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        messagePanel.add(custIdLabel);
        messagePanel.add(custIdTextField);
        messagePanel.add(messageLabel);
        messagePanel.add(messageScrollPane);
        messagePanel.add(submitButton);

        internalFrame.getContentPane().add(messagePanel);
        desktopPane.add(internalFrame);
        internalFrame.setVisible(true);
    }
}
