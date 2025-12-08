package protocols;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface MessageHandler {
    ObjectMapper mapper = new ObjectMapper();

    /**
     * print function to display message from server to readable text for the user
     */
    void handle(String jsonBody);
}
