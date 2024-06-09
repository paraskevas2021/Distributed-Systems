/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */
 /*
 * WarehouseManagementServer.java
 * Code for a warehouse management server.
 * The server accepts requests from clients for product listing, purchasing, adding, removing, and updating products in the inventory.
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class WarehouseManagementServer {

    private static final int PORT = 5001;
    private static final Map<String, Integer> inventory = new HashMap<>();

    // Initializing the inventory with some products
    static {
        inventory.put("Laptop", 10);
        inventory.put("Smartphone", 20);
        inventory.put("Headphones", 15);
        inventory.put("Monitor", 8);
        inventory.put("Keyboard", 25);
    }

    public static void main(String[] args) {
        try ( ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Warehouse Management Server is running on port " + PORT);

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("Connected: " + socket);

                // Creating a new thread to serve the client
                Thread thread = new Thread(new ClientHandler(socket));
                thread.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Inner class for handling client requests
    private static class ClientHandler implements Runnable {

        private Socket socket;
        private ObjectInputStream in;
        private ObjectOutputStream out;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());

                while (socket.isConnected()) {
                    Request request = null;
                    try {
                        request = (Request) in.readObject();
                    } catch (EOFException e) {
                        System.out.println("Connection closed by client.");
                        break;
                    }

                    if (request == null) {
                        break;
                    }

                    // Processing the request based on its type
                    if (request.getType().equals("list")) {
                        sendProductListAndStock();
                    } else if (request.getType().equals("buy")) {
                        processPurchase(request);
                    } else if (request.getType().equals("add")) {
                        addProductToInventory(request);
                    } else if (request.getType().equals("remove")) {
                        removeProductFromInventory(request);
                    } else if (request.getType().equals("update")) {
                        updateProductQuantity(request);
                    } else {
                        out.writeObject("Invalid request");
                        out.flush();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        // Method to send the product list and stock to the client
        private void sendProductListAndStock() throws IOException {
            out.writeObject(new HashMap<>(inventory));
            out.flush();
        }

        // Method to process a purchase request
        private void processPurchase(Request request) throws IOException {
            String productName = request.getProduct();
            int quantity = request.getQuantity();
            String responseMessage;

            synchronized (inventory) {
                Integer availableQuantity = inventory.get(productName);

                if (availableQuantity != null && availableQuantity >= quantity) {
                    inventory.put(productName, availableQuantity - quantity);
                    responseMessage = "Purchase completed for product: " + productName + ", Quantity: " + quantity
                            + ". Available stock: " + inventory.get(productName);
                } else {
                    responseMessage = "Purchase failed for product: " + productName + ", Quantity: " + quantity
                            + ". Product is not available in sufficient quantity.";
                }

                out.writeObject(responseMessage);
                out.flush();
            }
        }

        // Method to add a product to the inventory
        private void addProductToInventory(Request request) throws IOException {
            String productName = request.getProduct();
            int quantity = request.getQuantity();

            synchronized (inventory) {
                if (inventory.containsKey(productName)) {
                    int currentQuantity = inventory.get(productName);
                    inventory.put(productName, currentQuantity + quantity);
                } else {
                    inventory.put(productName, quantity);
                }

                out.writeObject("Product '" + productName + "' added to inventory.");
                out.flush();
            }
        }

        // Method to remove a product from the inventory
        private void removeProductFromInventory(Request request) throws IOException {
            String productName = request.getProduct();

            synchronized (inventory) {
                if (inventory.containsKey(productName)) {
                    inventory.remove(productName);
                    out.writeObject("Product '" + productName + "' removed from inventory.");
                } else {
                    out.writeObject("Product '" + productName + "' not found in inventory.");
                }
                out.flush();
            }
        }

        // Method to update the quantity of a product in the inventory
        private void updateProductQuantity(Request request) throws IOException {
            String productName = request.getProduct();
            int quantity = request.getQuantity();

            synchronized (inventory) {
                if (inventory.containsKey(productName)) {
                    inventory.put(productName, quantity);
                    out.writeObject("Quantity of product '" + productName + "' updated to " + quantity);
                } else {
                    out.writeObject("Product '" + productName + "' not found in inventory.");
                }
                out.flush();
            }
        }
    }
}
