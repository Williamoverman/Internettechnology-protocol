package protocols.messages;

import protocols.MessageHandler;
import responses.HangupResponse;

import java.io.IOException;

public class HangupMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            HangupResponse response = mapper.readValue(jsonBody, HangupResponse.class);
            String desc = switch (response.reason()) {
                case 7000 -> "No pong received";
                default -> "Unknown reason: " + response.reason();
            };
            System.out.println("[HANGUP] " + desc);
        } catch (IOException e) {
            System.err.println("Failed to parse hangup: " + e.getMessage());
        }
    }
}
