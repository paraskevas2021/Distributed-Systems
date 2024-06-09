/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MainMenu extends JDialog implements ActionListener {

    private JTextField inputUsername;
    private JPasswordField inputPassword;
    private JButton loginButton;
    private JButton cancelButton;
    private CustomerManagementInterface userManager;
    private boolean canceled = false;
    private String username;
    private String password;

    /**
     * Constructor for the MainMenu dialog.
     * @param userManager The interface for customer management.
     */
    public MainMenu(CustomerManagementInterface userManager) {
        this.userManager = userManager;
        setTitle("Login Menu");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setModal(true);
        setSize(400, 200);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Enter your username:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        inputUsername = new JTextField(15);
        mainPanel.add(inputUsername, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Enter your password:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPassword = new JPasswordField(15);
        inputPassword.addActionListener(this); // Listen for password input
        mainPanel.add(inputPassword, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        mainPanel.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        cancelButton = new JButton("Exit");
        cancelButton.addActionListener(this);
        mainPanel.add(cancelButton, gbc);

        add(mainPanel);
        setVisible(true);
    }

    /**
     * Check if the dialog was canceled.
     * @return True if the dialog was canceled, false otherwise.
     */
    public boolean isCanceled() {
        return canceled;
    }

    /**
     * Get the entered password.
     * @return The entered password as a string.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Get the entered username.
     * @return The entered username as a string.
     */
    public String getUsername() {
        return username;
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginButton || ae.getSource() == inputPassword) {
            if (inputUsername.getText().isEmpty() || inputPassword.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this, "Please fill in both fields.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                username = inputUsername.getText();
                password = new String(inputPassword.getPassword());
                setVisible(false);
                dispose();
            }
        } else if (ae.getSource() == cancelButton) {
            canceled = true;
            setVisible(false);
            dispose();
            System.exit(0); // Terminate the application
        }
    }
}
