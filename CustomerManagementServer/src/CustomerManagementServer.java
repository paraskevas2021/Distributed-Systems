/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */

import java.io.*;
import java.net.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class CustomerManagementServer {

    public CustomerManagementServer(int port) {
        try {
            // Creating RMI Registry
            Registry registry = LocateRegistry.createRegistry(1099);
            System.out.println("RMI Registry created on port 1099.");

            // Creating CustomerManagementService
            CustomerManagementServiceImpl customerManagementService = new CustomerManagementServiceImpl();
            
            // Binding the service to RMI Registry
            Naming.rebind("CustomerManagementService", customerManagementService);
            System.out.println("CustomerManagementService bound to RMI Registry.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Customer Management Server started...");

            // Connect to the Warehouse Management Server
            Socket warehouseSocket = new Socket("localhost", 5001);
            ObjectOutputStream warehouseOut = new ObjectOutputStream(warehouseSocket.getOutputStream());
            ObjectInputStream warehouseIn = new ObjectInputStream(warehouseSocket.getInputStream());

            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept incoming client connections
                System.out.println("Client connected...");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CustomerManagementServer(5000);
    }
}
