/*
 * 321/2020040 Γιαννακόπουλος Παρασκευάς
 * 321/2020105 Κωνσταντάρας Ιωάννης
 */

import java.io.Serializable;

/**
 * Represents a response from a server operation.
 */
public class Response implements Serializable {
    private boolean success; // Indicates whether the operation was successful
    private String message; // Message associated with the response

    /**
     * Constructs a Response object.
     * @param success Indicates whether the operation was successful.
     * @param message Message associated with the response.
     */
    public Response(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Gets the success status of the response.
     * @return True if the operation was successful, false otherwise.
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Gets the message associated with the response.
     * @return The message string.
     */
    public String getMessage() {
        return message;
    }
}

