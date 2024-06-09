import java.rmi.Naming;
import javax.swing.JOptionPane;

public class Client {
    private CustomerManagementInterface userManager;

    public Client(String serverAddress) {
        try {
            // Perform RMI lookup to locate the CustomerManagementService
            String name = "//localhost/CustomerManagementService";

            userManager = (CustomerManagementInterface) Naming.lookup(name);

            // Display the main menu for authentication
            while (true) {
                MainMenu mainMenu = new MainMenu(userManager);
                if (mainMenu.isCanceled()) {
                    System.out.println("User canceled the login.");
                    break;
                }

                String username = mainMenu.getUsername();
                String password = mainMenu.getPassword();
                String userType = userManager.authenticate(username, password);

                // If authentication is successful, create the corresponding GUI
                if (userType != null && (userType.equals("admin") || userType.equals("user"))) {
                    if (userType.equals("admin")) {
                        JOptionPane.showMessageDialog(null, "Successfully authenticated as admin");
                        new AdminGUI(userManager, username);
                    } else {
                        JOptionPane.showMessageDialog(null, "Successfully authenticated as user");
                        new UserGUI(userManager, username);
                    }
                    break;
                } else {
                    JOptionPane.showMessageDialog(null, "Authentication failed: " + userType, "Authentication Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new Client("127.0.0.1"); // Localhost address for RMI server
    }
}
