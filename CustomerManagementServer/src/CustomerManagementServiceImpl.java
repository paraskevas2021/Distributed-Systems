/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */

import java.io.*;
import java.net.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CustomerManagementServiceImpl extends UnicastRemoteObject implements CustomerManagementInterface {

    private Map<String, String> userCredentials;
    private Set<String> connectedUsers;

    // Constructor
    protected CustomerManagementServiceImpl() throws RemoteException {
        super();
        connectedUsers = new HashSet<>();
        userCredentials = new HashMap<>();
        userCredentials.put("admin", "admin");
        userCredentials.put("user", "user");
        userCredentials.put("user1", "user1");
    }

    // Method to authenticate users
    public synchronized String authenticate(String username, String password) throws RemoteException {
        if (userCredentials.containsKey(username)) {
            String storedPassword = userCredentials.get(username);
            if (storedPassword.equals(password)) {
                if (connectedUsers.contains(username)) {
                    return "User already connected";
                }
                connectedUsers.add(username);
                return username.equals("admin") ? "admin" : "user";
            } else {
                return "Incorrect password";
            }
        } else {
            return "User not found";
        }
    }

    // Method to disconnect a user
    public synchronized void disconnectUser(String username) {
        if (connectedUsers.contains(username)) {
            connectedUsers.remove(username);
            System.out.println("User " + username + " disconnected.");
        }
    }

    // Method to process a purchase
    @Override
    public String processPurchase(String username, String product, int quantity) throws RemoteException {
        Request request = new Request("buy", product, quantity);
        Object response = sendRequestToWarehouse(request);
        if (response instanceof String) {
            return (String) response;
        } else {
            return "Unexpected response from server";
        }
    }

    // Method to list products
    @Override
    public Map<String, Integer> listProducts() throws RemoteException {
        Request request = new Request("list");
        Object response = sendRequestToWarehouse(request);
        if (response instanceof Map) {
            return (Map<String, Integer>) response;
        } else {
            return new HashMap<>(); // Returning an empty list in case of error
        }
    }

    // Method to add a product
    @Override
    public String addProduct(String username, String productName, int quantity) throws RemoteException {
        if (userCredentials.containsKey(username) && username.equals("admin")) {
            Request request = new Request("add", productName, quantity);
            Object response = sendRequestToWarehouse(request);
            if (response instanceof String) {
                return (String) response;
            } else {
                return "Unexpected response from server";
            }
        } else {
            return "You don't have permission to add products.";
        }
    }

    // Method to remove a product
    @Override
    public String removeProduct(String username, String productName) throws RemoteException {
        if (userCredentials.containsKey(username) && username.equals("admin")) {
            Request request = new Request("remove", productName);
            Object response = sendRequestToWarehouse(request);
            if (response instanceof String) {
                return (String) response;
            } else {
                return "Unexpected response from server";
            }
        } else {
            return "You don't have permission to remove products.";
        }
    }

    // Method to search products by category
    @Override
    public Map<String, Integer> searchProductsByCategory(String category) throws RemoteException {
        return listProducts(); // For simplicity of implementation
    }

    // Method to send a request for product list
    @Override
    public String sendListRequest() throws RemoteException {
        return listProducts().toString();
    }

    // Method to communicate with the Warehouse Management Server
    private Object sendRequestToWarehouse(Request request) {
        try ( Socket socket = new Socket("localhost", 5001);  ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());  ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(request);
            out.flush();

            return in.readObject(); // Returning the received object
        } catch (IOException | ClassNotFoundException e) {
            synchronized (this) {
                try {
                    wait(); // Wait in case of communication error with the server
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return "Error communicating with warehouse server";
        } finally {
            synchronized (this) {
                notify(); // Notify completion of communication
            }
        }
    }

}
