/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;

public interface CustomerManagementInterface extends Remote {

    // Method to authenticate a user
    String authenticate(String username, String password) throws RemoteException;

    // Method to process a purchase
    String processPurchase(String username, String product, int quantity) throws RemoteException;

    // Method to list products
    Map<String, Integer> listProducts() throws RemoteException;

    // Method to add a product
    String addProduct(String username, String productName, int quantity) throws RemoteException;

    // Method to remove a product
    String removeProduct(String username, String productName) throws RemoteException;

    // Method to search products by category
    Map<String, Integer> searchProductsByCategory(String category) throws RemoteException;

    // Method to send a request for product list
    String sendListRequest() throws RemoteException;

    // Method to disconnect a user
    void disconnectUser(String username) throws RemoteException;
}
