package protocols;

import utils.JsonParsers;

public interface MessageHandler {
    // messages from the server need to be parsed
    JsonParsers jsonParser = new JsonParsers();

    /**
     * print function to display message from server to readable text for the user
     */
    void handle(String jsonBody);
}
