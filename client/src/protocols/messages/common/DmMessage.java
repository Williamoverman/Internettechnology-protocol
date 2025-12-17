package protocols.messages.common;

import protocols.MessageHandler;
import responses.common.DmResponse;

import java.io.IOException;

public class DmMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            DmResponse response = mapper.readValue(jsonBody, DmResponse.class);
            System.out.println("[DM] <" + response.username() + ">: " + response.message());
        } catch (IOException e) {
            System.err.println("Failed to parse broadcast: " + e.getMessage());
        }
    }
}
