package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.apache.logging.log4j.*;

import controller.Client;
import model.Staff;

public class AuthView implements Serializable
{
    private JFrame frame;
    private JPanel loginPanel;
    private JPanel signupPanel;
    private JTextField idField;
    private JPasswordField passwordField;
    private JTextField signupIdField;
    private JPasswordField signupPasswordField;
    private JTextField signupFirstNameField;
    private JTextField signupLastNameField;
    private boolean registrationSuccessful;
    private static final Logger logger= LogManager.getLogger(AuthView.class);


    public AuthView() {

        frame = new JFrame("Grizzly Entertainment Equipment Rental");
        frame.setSize(400, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setBackground(Color.blue);

        loginPanel = new JPanel();
        loginPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JLabel titleLabel = new JLabel("Equipment Rental");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        loginPanel.add(titleLabel, gbc);

        JLabel usernameLabel = new JLabel("ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        loginPanel.add(usernameLabel, gbc);

        idField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPanel.add(idField, gbc);

        JLabel passwordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        loginPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        loginPanel.add(passwordField, gbc);
        
        System.out.println("initial ...");

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = idField.getText();
                String password = new String(passwordField.getPassword());
                Client client = new Client(); // Create a client instance

                if (id.endsWith("grizzly")) {
                    // Send a login request to the server
                    client.sendAction("Login Employee");
                    client.sendLoginStaff(id, password);
                    client.receiveResponse();
                    logger.info("Login response received");

                    if (client.isLoginInSuccessful()) 
                    {
                    	JOptionPane.showMessageDialog(frame, "Login Successful. Welcome to grizzly");
                    	logger.info("Login successful");
                        openEmployeeDashboard();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Login failed. Please try again.");
                        logger.info("Failed Login");
                    }
                } 
                
                // Close the connection after login
                client.closeConnection();
            }
        });

        gbc.gridx = 1;
        gbc.gridy = 6;
        loginPanel.add(loginButton, gbc);

        frame.add(loginPanel, BorderLayout.CENTER);

        JButton signupButton = new JButton("Sign Up");
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Switch to the signup panel
                frame.remove(loginPanel);
                frame.add(signupPanel, BorderLayout.CENTER);
                frame.revalidate();
                frame.repaint();
            }
        });
        gbc.gridx = 1;
        gbc.gridy = 7;
        loginPanel.add(signupButton, gbc);

        frame.add(loginPanel, BorderLayout.CENTER);

        // signup panel
        signupPanel = new JPanel();
        signupPanel.setLayout(new GridBagLayout());

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
       // gbc.gridwidth = 1;
       
        JLabel signupTitleLabel = new JLabel("Sign Up");
        signupTitleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        signupTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
       // gbc.gridwidth = 2; // Span 2 columns
        signupPanel.add(signupTitleLabel, gbc);

        JLabel signupUsernameLabel = new JLabel("ID:");
        gbc.gridx = 0;
        gbc.gridy = 1;
        signupPanel.add(signupUsernameLabel, gbc);

        signupIdField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 4; // Span 4 columns
        signupPanel.add(signupIdField, gbc);

        JLabel signupPasswordLabel = new JLabel("Password:");
        gbc.gridx = 0;
        gbc.gridy = 2;
        signupPanel.add(signupPasswordLabel, gbc);

        signupPasswordField = new JPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        signupPanel.add(signupPasswordField, gbc);

        JLabel firstNameLabel = new JLabel("First Name:");
        gbc.gridx = 0;
        gbc.gridy = 3;
        signupPanel.add(firstNameLabel, gbc);

        signupFirstNameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 3;
        signupPanel.add(signupFirstNameField, gbc);

        // Add last name field to signup panel
        JLabel lastNameLabel = new JLabel("Last Name:");
        gbc.gridx = 0;
        gbc.gridy = 4;
        signupPanel.add(lastNameLabel, gbc);

        signupLastNameField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 4;
        signupPanel.add(signupLastNameField, gbc);
        
        signupIdField.setPreferredSize(new Dimension(50, 70));
        signupPasswordField.setPreferredSize(new Dimension(50, 70));
        signupFirstNameField.setPreferredSize(new Dimension(50, 70));
        signupLastNameField.setPreferredSize(new Dimension(50, 70));
        
        

        JButton signupSubmitButton = new JButton("Sign Up");
        signupSubmitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String id = signupIdField.getText();
                String password = new String(signupPasswordField.getPassword());
                String firstName = signupFirstNameField.getText();
                String lastName = signupLastNameField.getText();

                // Check if any field is null or empty
                if (id.isEmpty() || password.isEmpty() || firstName.isEmpty() || lastName.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "All fields are required. Please fill in all the fields.");
                    return; // Exit the method if any field is null or empty
                }

                if (id.endsWith("grizzly")) {
                    // Employee sign-up
                    Client client = new Client();
                    Staff staffObj = new Staff(id, password, firstName, lastName);

                    client.sendAction("Signup Employee");
                    client.sendStaff(staffObj);
                    client.receiveResponse();

                    if (client.isSignUpSuccessful()) {
                        frame.remove(signupPanel);
                        frame.add(loginPanel, BorderLayout.CENTER);
                        frame.revalidate();
                        frame.repaint();

                        JOptionPane.showMessageDialog(frame, "Registration Successful! You can now log in.");
                        logger.info("Registration Successful");
                    } else {
                        JOptionPane.showMessageDialog(frame, "Registration failed. Please try again.");
                        logger.info("Registration failed");
                    }
                }
            }
        });


        gbc.gridx = 1;
        gbc.gridy = 7;
        signupPanel.add(signupSubmitButton, gbc);

        

    }   
    
    public void show() {
        frame.setVisible(true);
    }


    private void openEmployeeDashboard() {
    	
        EmployeeDashboard employeeDashboard = new EmployeeDashboard();
        frame.dispose(); 
    }
    



}
