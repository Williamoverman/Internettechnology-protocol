package protocols.messages;

import protocols.MessageHandler;
import responses.LeftResponse;

import java.io.IOException;

public class LeftMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            LeftResponse response = mapper.readValue(jsonBody, LeftResponse.class);
            System.out.println("[LEFT] <" + response.username() + ">");
        } catch (IOException e) {
            System.err.println("Failed to parse left: " + e.getMessage());
        }
    }
}
