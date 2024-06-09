/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */

import java.io.Serializable;

public class Request implements Serializable {
    private String type;
    private String product;
    private int quantity;

    // Constructor for requests without product and quantity
    public Request(String type) {
        this.type = type;
    }

    // Constructor for requests with product but without quantity
    public Request(String type, String product) {
        this.type = type;
        this.product = product;
    }

    // Constructor for requests with both product and quantity
    public Request(String type, String product, int quantity) {
        this.type = type;
        this.product = product;
        this.quantity = quantity;
    }

    // Getter for request type
    public String getType() {
        return type;
    }

    // Getter for requested product
    public String getProduct() {
        return product;
    }

    // Getter for requested quantity
    public int getQuantity() {
        return quantity;
    }
}

