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

/**
 * UserGUI represents the graphical user interface for regular users.
 */
public class UserGUI extends JFrame implements ActionListener {

    private JLabel titleLabel;
    private JTextField quantityField;
    private JComboBox<String> productDropdown;
    private JButton buyButton;
    private JButton listButton;
    private JButton exitButton;
    private CustomerManagementInterface userManager;
    private String username;

    /**
     * Constructs a UserGUI object.
     *
     * @param userManager The CustomerManagementInterface object for user
     * management.
     * @param username The username of the current user.
     */
    public UserGUI(CustomerManagementInterface userManager, String username) {
        this.userManager = userManager;
        this.username = username;
        setTitle("User Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        titleLabel = new JLabel("User Menu", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        mainPanel.add(titleLabel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Product:"), gbc);

        gbc.gridx = 1;
        String[] products = getProductNames();
        productDropdown = new JComboBox<>(products);
        productDropdown.setPreferredSize(new Dimension(200, 25));
        mainPanel.add(productDropdown, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Quantity:"), gbc);

        gbc.gridx = 1;
        quantityField = new JTextField(10);
        mainPanel.add(quantityField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        buyButton = new JButton("Buy");
        buyButton.setPreferredSize(new Dimension(100, 30));
        buyButton.addActionListener(this);
        mainPanel.add(buyButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        listButton = new JButton("List");
        listButton.setPreferredSize(new Dimension(100, 30));
        listButton.addActionListener(this);
        mainPanel.add(listButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        exitButton = new JButton("Exit");
        exitButton.addActionListener(this);
        mainPanel.add(exitButton, gbc);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Retrieves the names of available products from the server.
     *
     * @return An array of product names.
     */
    private String[] getProductNames() {
        try {
            Map<String, Integer> stock = userManager.listProducts();
            if (stock != null && !stock.isEmpty()) {
                return stock.entrySet().stream()
                        .filter(entry -> entry.getValue() > 0)
                        .map(Map.Entry::getKey)
                        .toArray(String[]::new);
            } else {
                System.out.println("No products received from server.");
                return new String[0];
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new String[0];
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == buyButton) {
            String quantityString = quantityField.getText();
            if (!quantityString.isEmpty()) {
                try {
                    int quantity = Integer.parseInt(quantityString);
                    String productName = (String) productDropdown.getSelectedItem();
                    String response = userManager.processPurchase(username, productName, quantity);
                    JOptionPane.showMessageDialog(this, response, "Information", JOptionPane.INFORMATION_MESSAGE);
                    clearForm();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number for quantity", "Error", JOptionPane.ERROR_MESSAGE);
                    clearForm();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    clearForm();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a quantity", "Error", JOptionPane.ERROR_MESSAGE);
                clearForm();
            }
        } else if (e.getSource() == listButton) {
            try {
                Map<String, Integer> stock = userManager.listProducts();
                StringBuilder stockMessage = new StringBuilder("Stock Inventory:\n");
                for (Map.Entry<String, Integer> entry : stock.entrySet()) {
                    stockMessage.append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
                }
                JOptionPane.showMessageDialog(this, stockMessage.toString(), "Stock Inventory", JOptionPane.INFORMATION_MESSAGE);
                updateDropdown();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else if (e.getSource() == exitButton) {
            try {
                userManager.disconnectUser(username);
            } catch (RemoteException ex) {
                Logger.getLogger(UserGUI.class.getName()).log(Level.SEVERE, null, ex);
            }
            dispose();
        }
    }

    /**
     * Updates the product dropdown list based on the current stock.
     */
    private void updateDropdown() {
        String[] products = getProductNames();
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(products);
        productDropdown.setModel(model);
    }

    /**
     * Clears the input fields in the form.
     */
    private void clearForm() {
        productDropdown.setSelectedIndex(0);
        quantityField.setText("");
    }
}
