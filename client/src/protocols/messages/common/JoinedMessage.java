package protocols.messages.common;

import protocols.MessageHandler;
import responses.common.JoinedResponse;

import java.io.IOException;

public class JoinedMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            JoinedResponse response = mapper.readValue(jsonBody, JoinedResponse.class);
            System.out.println("[JOINED] <" + response.username() + ">");
        } catch (IOException e) {
            System.err.println("Failed to parse joined: " + e.getMessage());
        }
    }
}
