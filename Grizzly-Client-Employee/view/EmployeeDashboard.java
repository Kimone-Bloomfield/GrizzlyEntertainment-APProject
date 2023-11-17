package view;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import controller.Client;
import model.Equipment;
import model.Message;
import model.Rental;
import model.Staff;
import model.Transactions;

public class EmployeeDashboard implements Serializable{
	
	private List<Equipment> equipmentList = new ArrayList<>();
	private List<Rental> rentalRequests= new ArrayList<>();
	private JDesktopPane desktopPane; 
	private static final String PDF_FILE_PATH = "Invoice.pdf";
	private static final Logger logger= LogManager.getLogger(EmployeeDashboard.class);
	
	public EmployeeDashboard() {
		
		
	    JFrame frame = new JFrame("Employee Dashboard");
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setSize(800, 600);
	    frame.setLayout(new BorderLayout());
	    
	    desktopPane = new JDesktopPane();

	    // Create Menu Bar
	    JMenuBar menuBar = new JMenuBar();

	    JMenu rentalMenu = new JMenu("Rental Requests");
	    rentalMenu.setMnemonic(KeyEvent.VK_R); // Alt+R will open the Rental Requests menu

	    JMenuItem viewRentalRequestsItem = new JMenuItem("View Rental Requests");
	    viewRentalRequestsItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
	    viewRentalRequestsItem.setToolTipText("View rental requests");
	    rentalMenu.add(viewRentalRequestsItem);

	    JMenu equipmentMenu = new JMenu("Equipment Inventory");
	    equipmentMenu.setMnemonic(KeyEvent.VK_E); // Alt+E will open the Equipment Inventory menu

	    JMenuItem viewEquipmentItem = new JMenuItem("View Equipment");
	    viewEquipmentItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK)); // Ctrl+E shortcut
	    viewEquipmentItem.setToolTipText("View equipment inventory");
	    equipmentMenu.add(viewEquipmentItem);
	    
	    JMenuItem addEquipmentItem = new JMenuItem("Add Equipment");
	    addEquipmentItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_U, InputEvent.CTRL_DOWN_MASK)); // Ctrl+E shortcut
	    addEquipmentItem.setToolTipText("Add equipment inventory");
	    equipmentMenu.add(addEquipmentItem);

	    JMenu messagingMenu = new JMenu("Messaging");
	    messagingMenu.setMnemonic(KeyEvent.VK_M); // Alt+M will open the Messaging menu
	    
	    JMenuItem viewCustomerMessagesItem = new JMenuItem("View Customer Messages");
        viewCustomerMessagesItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_M, InputEvent.CTRL_DOWN_MASK));
        viewCustomerMessagesItem.setToolTipText("View customer messages");
        messagingMenu.add(viewCustomerMessagesItem);

	    JMenu billingMenu = new JMenu("Billing");
	    billingMenu.setMnemonic(KeyEvent.VK_B); // Alt+B will open the Billing menu

	    JMenuItem createInvoiceItem = new JMenuItem("Create Invoice");
	    createInvoiceItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_DOWN_MASK)); // Ctrl+I shortcut
	    createInvoiceItem.setToolTipText("Create a new invoice"); 

	    billingMenu.add(createInvoiceItem);

	    menuBar.add(rentalMenu);
	    menuBar.add(equipmentMenu);
	    menuBar.add(messagingMenu);
	    menuBar.add(billingMenu);

	    frame.setJMenuBar(menuBar);

	 // Create Internal Frames for different tasks
	    JInternalFrame requestsFrame = new JInternalFrame("Rental Requests", true, true, true, true);
	    requestsFrame.setSize(400, 300);

	    JInternalFrame messagingFrame = new JInternalFrame("Messaging", true, true, true, true);
	    messagingFrame.setSize(400, 300);

	    JInternalFrame billingFrame = new JInternalFrame("Billing", true, true, true, true);
	    billingFrame.setSize(400, 300);

	    // ActionListener for "View Rental Requests"
	    viewRentalRequestsItem.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            Client client = new Client();
	            client.sendAction("View Rental Requests");
	            List<Rental> rentalList = client.receiveRentalRequest();

	            // Create a DefaultTableModel with JButton columns for Accept and Deny
	            DefaultTableModel tableModel = new DefaultTableModel(
	                    new String[]{"Customer ID", "Customer Name", "Equipment ID", "Equipment Name", "Date", "Accept", "Deny"}, 0
	            ) {
	                @Override
	                public Class<?> getColumnClass(int columnIndex) {
	                    return columnIndex == 5 || columnIndex == 6 ? JButton.class : Object.class;
	                }

	                @Override
	                public boolean isCellEditable(int row, int column) {
	                    return column == 5 || column == 6;
	                }
	            };

	            JTable rentalTable = new JTable(tableModel);
	            rentalTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
	            rentalTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());

	            JScrollPane scrollPane = new JScrollPane(rentalTable);

	            // Add an action listener to handle button clicks
	            rentalTable.addMouseListener(new MouseAdapter() {
	                @Override
	                public void mouseClicked(MouseEvent e) {
	                    int columnAccept = rentalTable.getColumnModel().getColumnIndex("Accept");
	                    int columnDeny = rentalTable.getColumnModel().getColumnIndex("Deny");
	                    int row = rentalTable.rowAtPoint(e.getPoint());

	                    if (row >= 0) {
	                        int clickedColumn = rentalTable.columnAtPoint(e.getPoint());

	                        // Inside the action listener for the "Accept" button in the dashboard
	                        if (clickedColumn == columnAccept) {
	                            // Handle Accept button click
	                            System.out.println("Accept button clicked at row " + row);

	                            // Get the rental details for the selected row
	                            Rental selectedRental = rentalList.get(row);

	                            // Send the rental request to the server for acceptance
	                            client.sendAction("Accept Rental Request");
	                            System.out.println("Action sent");
	                            client.sendRentalRequest(selectedRental);
	                            System.out.println("after send rental request2");

	                            //boolean acceptanceStatus = client.receiveRentalResponse();
	                            client.receiveResponse();
	                            System.out.println("receive response");

	                                   }
	                        else if (clickedColumn== columnDeny)
	                        {
	                        	 Rental selectedRental = rentalList.get(row);
	                        	client.sendAction("Deny Request");
	                        	client.denyRentalRequest(selectedRental);
	                        	client.receiveResponse();
	                        }
	                    }
	                }
	            });

	            // Use a forEach loop to populate the tableModel
	            rentalList.forEach(rental -> {
	                Object[] rowData = {
	                        rental.getCustomer_id(),
	                        rental.getCustomer_name(),
	                        rental.getEquipment_id(),
	                        rental.getEquipment_name(),
	                        rental.getDate(),
	                        "Accept",
	                        "Deny"
	                };
	                tableModel.addRow(rowData);
	            });

	            createInternalFrame(frame, "Rental Requests", scrollPane);
	        }
	    });

	    viewEquipmentItem.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            Client client = new Client();
	            client.sendAction("View Equipment");
	            List<Equipment> equipmentList = client.receiveEquipmentList();
	            client.receiveResponse();

	            // Pass the main frame to the createInternalFrame method
	            createInternalFrame(frame, "Equipment Inventory", new JScrollPane(new JTable(new DefaultTableModel(
	                equipmentList.stream()
	                    .map(equipment -> new Object[]{
	                        equipment.getEquipment_id(),
	                        equipment.getEquipment_name(),
	                        equipment.getEquipment_status(),
	                        equipment.getEquipment_category(),
	                        equipment.getCost()
	                    })
	                    .toArray(Object[][]::new),
	                new String[]{"Equipment ID", "Equipment Name", "Equipment Status", "Equipment Category", "Cost"}
	            ))));
	        }
	    });

	    
	    addEquipmentItem.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            JDialog addEquipmentDialog = new JDialog(frame, "Add Equipment", true);
	            addEquipmentDialog.setSize(500, 300);

	            // Create input fields and buttons
	            JTextField equipmentIDField = new JTextField(20);
	            JTextField equipmentNameField = new JTextField(20);
	            JTextField equipmentCategoryField = new JTextField(20);
	            JTextField equipmentStatusField = new JTextField(20);
	            JTextField equipmentCostField = new JTextField(20);
	            JButton addButton = new JButton("Add");

	            // Create a panel to hold the form components using GridBagLayout
	            JPanel formPanel = new JPanel(new GridBagLayout());
	            GridBagConstraints gbc = new GridBagConstraints();
	            gbc.fill = GridBagConstraints.HORIZONTAL;
	            gbc.insets = new Insets(5, 5, 5, 5);

	            gbc.gridx = 0;
	            gbc.gridy = 0;
	            formPanel.add(new JLabel("Equipment ID:"), gbc);

	            gbc.gridx = 1;
	            formPanel.add(equipmentIDField, gbc);

	            gbc.gridx = 0;
	            gbc.gridy = 1;
	            formPanel.add(new JLabel("Equipment Name:"), gbc);

	            gbc.gridx = 1;
	            formPanel.add(equipmentNameField, gbc);

	            gbc.gridx = 0;
	            gbc.gridy = 2;
	            formPanel.add(new JLabel("Equipment Category:"), gbc);

	            gbc.gridx = 1;
	            formPanel.add(equipmentCategoryField, gbc);

	            gbc.gridx = 0;
	            gbc.gridy = 3;
	            formPanel.add(new JLabel("Equipment Status:"), gbc);

	            gbc.gridx = 1;
	            formPanel.add(equipmentStatusField, gbc);

	            gbc.gridx = 0;
	            gbc.gridy = 4;
	            formPanel.add(new JLabel("Equipment Cost:"), gbc);

	            gbc.gridx = 1;
	            formPanel.add(equipmentCostField, gbc);

	            gbc.gridx = 0;
	            gbc.gridy = 5;
	            gbc.gridwidth = 2; // Span two columns
	            formPanel.add(addButton, gbc);

	            addButton.addActionListener(new ActionListener() {
	                @Override
	                public void actionPerformed(ActionEvent e) {
	                    // Validate and add the equipment
	                    if (validateFields(equipmentIDField, equipmentNameField, equipmentCategoryField, equipmentStatusField, equipmentCostField)) {
	                        try {
	                            // Parse the cost field to a double
	                            double equipmentCost = Double.parseDouble(equipmentCostField.getText());

	                            addEquipment(equipmentIDField.getText(), equipmentNameField.getText(),
	                                    equipmentStatusField.getText(), equipmentCategoryField.getText(),
	                                    equipmentCost);

	                            Client client = new Client();
	                            Equipment equipment = new Equipment(equipmentIDField.getText(), equipmentNameField.getText(),
	                                    equipmentStatusField.getText(), equipmentCategoryField.getText(),
	                                    equipmentCost);

	                            client.sendAction("Add Equipment");
	                            client.sendEquipment(equipment);
	                            client.receiveResponse();

	                            addEquipmentDialog.dispose();
	                        } catch (NumberFormatException ex) {
	                            JOptionPane.showMessageDialog(addEquipmentDialog, "Please enter a valid numerical cost.");
	                        }
	                    } else {
	                        JOptionPane.showMessageDialog(addEquipmentDialog, "Please fill in all fields.");
	                    }
	                }
	            });

	            addEquipmentDialog.add(formPanel);
	            addEquipmentDialog.setVisible(true);
	        }
	        // New method to validate all fields
	        private boolean validateFields(JTextField... fields) {
	            for (JTextField field : fields) {
	                if (field.getText().isEmpty()) {
	                    return false;
	                }
	            }
	            return true;
	        }
	    });


	  
	    
	    viewCustomerMessagesItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Client client = new Client();
                client.sendAction("Get Customer Messages");
                List<Message> messageList  = client.receiveAllCustomerMessages();
                client.receiveResponse();

                // Display customer messages in a table
                displayCustomerMessages(messageList);
                
            }
        });

	    createInvoiceItem.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	retrieveTransactionById();
	          
	        }
	    });

	    frame.add(requestsFrame);
	    frame.add(messagingFrame);
	    frame.add(billingFrame);

	    frame.setVisible(true);
	    
	  
	    
}
	

	private void createInternalFrame(String title, int width, int height, Component component) {
	    JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
	    internalFrame.setSize(width, height);
	    internalFrame.add(component);
	    internalFrame.setVisible(true);
	    desktopPane.add(internalFrame);
	}

	
	private void displayCustomerMessages(List<Message> messages) {
        JFrame customerMessagesFrame = new JFrame("Customer Messages");
        customerMessagesFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        customerMessagesFrame.setSize(800, 600);

        DefaultTableModel tableModel = new DefaultTableModel();
        tableModel.addColumn("Message ID");
        tableModel.addColumn("Customer Message");
        tableModel.addColumn("Employee Response");
        tableModel.addColumn("Status");

        for (Message message : messages) {
            tableModel.addRow(new Object[]{
                message.getMessageID(),
                message.getCustMessage(),
                message.getEmpMessage(),
                message.getStatus()
            });
        }

        JTable customerMessagesTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(customerMessagesTable);
        customerMessagesFrame.add(scrollPane);

     // Add a mouse click listener to respond to a message
        customerMessagesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = customerMessagesTable.rowAtPoint(e.getPoint());
                if (row >= 0) {
                    Message selectedMessage = messages.get(row);
                    JTextArea empResponseTextArea = new JTextArea(5, 20);

                    // Create a panel to add the response components
                    JPanel responsePanel = new JPanel(new BorderLayout());
                    responsePanel.add(new JLabel("Employee Response:"), BorderLayout.NORTH);
                    responsePanel.add(new JScrollPane(empResponseTextArea), BorderLayout.CENTER);

                    // Show the response panel for the selected message
                    int result = JOptionPane.showOptionDialog(
                            customerMessagesFrame,
                            responsePanel,
                            "Respond to Customer Message",
                            JOptionPane.OK_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            new Object[]{"OK"},
                            null);

                    // Check if the user clicked "OK"
                    if (result == JOptionPane.OK_OPTION) {
                        String empResponse = empResponseTextArea.getText();
                        selectedMessage.setEmpMessage(empResponse);

                        Client client = new Client();
                        client.sendAction("Respond to Customer");
                        client.sendResponseToCustomerMessage(selectedMessage.getMessageID(), empResponse);
                        client.receiveResponse();

                        // Update the message status in the table
                        customerMessagesTable.getModel().setValueAt("Responded", row, 3);

                        // Clear the response field
                        empResponseTextArea.setText("");
                    }
                }
            }
        });


        customerMessagesFrame.setVisible(true);
        
        
    }
	
	private void retrieveTransactionById() {
        JTextField customerIdField = new JTextField();
        int option = JOptionPane.showOptionDialog(
                desktopPane,
                customerIdField,
                "Enter Customer ID",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                null
        );

        if (option == JOptionPane.OK_OPTION) {
            Client client = new Client();
            String customerId = customerIdField.getText();

            // Send the request to the server
            System.out.println("Before sending action");
            client.sendAction("Retrieve Transaction by ID");
            List<Transactions> transactions = client.retrieveTransactionById(customerId);
            System.out.println("After getting id");
            System.out.println(customerId);

            // Receive the response from the server
            System.out.println(transactions);
            client.receiveResponse();
            System.out.println("After response");

            generateInvoicePDF(transactions);
        }
    }

	private void generateInvoicePDF(List<Transactions> transactions) {
	    // Create a PDF document
	    Document document = new Document();

	    try {
	        // Create a PdfWriter instance
	        PdfWriter.getInstance(document, new FileOutputStream(PDF_FILE_PATH));

	        // Open the document for writing
	        document.open();

	        // Add content to the PDF document
	        addInvoiceHeader(document);
	        
	        if (transactions != null && !transactions.isEmpty()) {
	            for (Transactions transaction : transactions) {
	                addTransactionDetails(document, transaction);
	            }
	        } else {
	            document.add(new Paragraph("No transactions found for the given ID."));
	        }

	        addThankYouMessage(document);

	        // Close the document
	        document.close();

	        // Open the generated PDF file
	        Desktop desktop = Desktop.getDesktop();
	        desktop.open(new File(PDF_FILE_PATH));
	    } catch (DocumentException | IOException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(desktopPane, "Error generating PDF: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
	    }
	}

	private void addInvoiceHeader(Document document) throws DocumentException {
	    // Add modern invoice header with lines and borders
	    Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, BaseColor.BLACK);
	    Font subheaderFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

	    // Set background color for the header
	    PdfPCell headerCell = new PdfPCell();
	    headerCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
	    headerCell.setBorder(Rectangle.NO_BORDER);

	    // Add business name and address
	    Paragraph businessName = new Paragraph("Grizzly Entertainment", headerFont);
	    businessName.setAlignment(Element.ALIGN_CENTER);
	    headerCell.addElement(businessName);

	    Paragraph businessAddress = new Paragraph("123 Main Street, Kingston, Jamaica", subheaderFont);
	    businessAddress.setAlignment(Element.ALIGN_CENTER);
	    headerCell.addElement(businessAddress);

	    // Add header cell to the table
	    PdfPTable headerTable = new PdfPTable(1);
	    headerTable.setWidthPercentage(100);
	    headerTable.addCell(headerCell);
	    document.add(headerTable);

	    // Add horizontal line
	    document.add(new LineSeparator());

	    // Add invoice details
	    PdfPCell detailsCell = new PdfPCell();
	    detailsCell.setBorder(Rectangle.NO_BORDER);

	    Paragraph invoiceTitle = new Paragraph("Invoice", headerFont);
	    invoiceTitle.setAlignment(Element.ALIGN_CENTER);
	    detailsCell.addElement(invoiceTitle);

	    // Add date
	    Paragraph currentDate = new Paragraph("Date: " + new Date(), subheaderFont);
	    currentDate.setAlignment(Element.ALIGN_RIGHT);
	    detailsCell.addElement(currentDate);

	    // Add vertical space
	    detailsCell.addElement(Chunk.NEWLINE);

	    // Add details cell to the table
	    PdfPTable detailsTable = new PdfPTable(1);
	    detailsTable.setWidthPercentage(100);
	    detailsTable.addCell(detailsCell);
	    document.add(detailsTable);
	}


	private void addTransactionDetails(Document document, Transactions transaction) throws DocumentException {
	    // Add transaction details
	    Font transactionFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

	    document.add(new Paragraph("Transaction ID: " + transaction.getTransactionId(), transactionFont));
	    document.add(new Paragraph("Transaction Date: " + transaction.getTransactionDate(), transactionFont));
	    document.add(new Paragraph("Customer ID: " + transaction.getCustomerId(), transactionFont));
	    document.add(new Paragraph("Customer Name: " + transaction.getCustomerName(), transactionFont));
	    document.add(new Paragraph("Equipment ID: " + transaction.getEquipmentId(), transactionFont));
	    document.add(new Paragraph("Equipment Name: " + transaction.getEquipmentName(), transactionFont));
	    document.add(new Paragraph("Date: " + transaction.getDate(), transactionFont));
	    document.add(new Paragraph("Equipment Category: " + transaction.getEquipmentCategory(), transactionFont));
	    document.add(new Paragraph("Cost: $" + transaction.getCost(), transactionFont));
	    document.add(new Paragraph("Balance: $" + transaction.getCustAccount(), transactionFont));
	    document.add(Chunk.NEWLINE);
	}

	private void addThankYouMessage(Document document) throws DocumentException {
	    // Add thank you message
	    Font thankYouFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
	    Paragraph thankYou = new Paragraph("Thank you for your business!", thankYouFont);
	    thankYou.setAlignment(Element.ALIGN_CENTER);
	    document.add(thankYou);
	}
	private void populateEquipmentTable(List<Equipment> equipmentList) {
	    DefaultTableModel tableModel = new DefaultTableModel();
	    tableModel.addColumn("Equipment ID");
	    tableModel.addColumn("Equipment Name");
	    tableModel.addColumn("Equipment Status");
	    tableModel.addColumn("Equipment Category");
	    tableModel.addColumn("Cost");

	    for (Equipment equipment : equipmentList) {
	        tableModel.addRow(new Object[]{
	            equipment.getEquipment_id(),
	            equipment.getEquipment_name(),
	            equipment.getEquipment_status(),
	            equipment.getEquipment_category(),
	            equipment.getCost()
	        });
	    }

	    JTable equipmentTable = new JTable(tableModel);
	    JScrollPane scrollPane = new JScrollPane(equipmentTable);

	    createInternalFrame(null, "Equipment Inventory", scrollPane);
	}
	
	class ButtonRenderer extends DefaultTableCellRenderer {
	    private JButton button;

	    public ButtonRenderer() {
	        button = new JButton();
	    }

	    @Override
	    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
	        button.setText(value.toString());
	        return button;
	    }
	}
	
	
	
	private void createInternalFrame(JFrame mainFrame, String title, JScrollPane scrollPane) {
	    JInternalFrame internalFrame = new JInternalFrame(title, true, true, true, true);
	    internalFrame.setSize(400, 300);
	    internalFrame.add(scrollPane);
	    internalFrame.setVisible(true);
	    // Add the internalFrame to the main frame
	    mainFrame.add(internalFrame);
	}
	

    // Method to add equipment to the inventory
    public void addEquipment(String equipment_id, String equipment_name, String equipment_status, String equipment_category, double cost) {
        Equipment equipment = new Equipment(equipment_id, equipment_name,equipment_status, equipment_category, cost);
        equipmentList.add(equipment);
    }
   
}

