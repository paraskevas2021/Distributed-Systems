/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminGUI extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JTextField productNameField;
    private JTextField quantityField;
    private JButton addProductButton;
    private JButton removeProductButton;
    private JButton checkStockButton;
    private JButton exitButton;
    private CustomerManagementInterface userManager;
    private String username;
    private final Object lock = new Object(); // Lock object for wait-notify

    public AdminGUI(CustomerManagementInterface userManager, String username) {
        this.userManager = userManager;
        this.username = username;
        setTitle("Admin Panel");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        titleLabel = new JLabel("Admin Panel", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        mainPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Product Name:"), gbc);

        gbc.gridx = 1;
        productNameField = new JTextField(15);
        mainPanel.add(productNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        quantityField = new JTextField(15);
        mainPanel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        addProductButton = new JButton("Add Product");
        addProductButton.addActionListener(this);
        mainPanel.add(addProductButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        removeProductButton = new JButton("Remove Product");
        removeProductButton.addActionListener(this);
        mainPanel.add(removeProductButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        checkStockButton = new JButton("Check Stock");
        checkStockButton.addActionListener(this);
        mainPanel.add(checkStockButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        mainPanel.add(exitButton, gbc);

        add(mainPanel);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addProductButton) {
            String productName = productNameField.getText();
            String quantityString = quantityField.getText();
            if (!quantityString.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityString);
                    if (productName != null && !productName.isEmpty()) {
                        try {
                            synchronized (lock) {
                                String response = userManager.addProduct(username, productName, quantity);
                                JOptionPane.showMessageDialog(this, response, "Information", JOptionPane.INFORMATION_MESSAGE);
                                clearForm();
                                lock.notify(); // Notify for thread unlocking
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Please enter a product name", "Error", JOptionPane.ERROR_MESSAGE);
                        clearForm();
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity", "Error", JOptionPane.ERROR_MESSAGE);
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a quantity", "Error", JOptionPane.ERROR_MESSAGE);
                clearForm();
            }
        } else if (e.getSource() == removeProductButton) {
            String productName = JOptionPane.showInputDialog(this, "Enter the product name to remove:");
            if (productName != null && !productName.isEmpty()) {
                try {
                    synchronized (lock) {
                        String response = userManager.removeProduct(username, productName);
                        JOptionPane.showMessageDialog(this, response, "Information", JOptionPane.INFORMATION_MESSAGE);
                        clearForm();
                        lock.notify(); // Notify for thread unlocking
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == checkStockButton) {
            try {
                Map<String, Integer> stock = userManager.listProducts();
                StringBuilder stockMessage = new StringBuilder("Stock Inventory:\n");
                for (Map.Entry<String, Integer> entry : stock.entrySet()) {
                    stockMessage.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                JOptionPane.showMessageDialog(this, stockMessage.toString(), "Stock Inventory",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == exitButton) {
            try {
                userManager.disconnectUser(username); // Disconnect user before closing
            } catch (RemoteException ex) {
                Logger.getLogger(AdminGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose(); // Close the window
        }
    }

    private void clearForm() {
        productNameField.setText("");
        quantityField.setText("");
    }
}
