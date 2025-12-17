package protocols.messages.common;

import protocols.MessageHandler;
import responses.common.BroadcastResponse;

import java.io.IOException;

public class BroadcastMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            BroadcastResponse response = mapper.readValue(jsonBody, BroadcastResponse.class);
            System.out.println("[BROADCAST] <" + response.username() + ">: " + response.message());
        } catch (IOException e) {
            System.err.println("Failed to parse broadcast: " + e.getMessage());
        }
    }
}
