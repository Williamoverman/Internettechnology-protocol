package protocols.messages.common;

import protocols.MessageHandler;
import responses.common.HiResponse;

import java.io.IOException;

public class HiMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            HiResponse response = mapper.readValue(jsonBody, HiResponse.class);
            System.out.println("[VERSION] <" + response.version() + ">");
        } catch (IOException e) {
            System.err.println("Failed to parse version: " + e.getMessage());
        }
    }
}
