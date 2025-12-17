package protocols.messages.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import protocols.MessageHandler;
import responses.common.OnlineResponse;

public class OnlineMessage implements MessageHandler {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(String jsonBody) {
        try {
            OnlineResponse response = mapper.readValue(jsonBody, OnlineResponse.class);

            System.out.println("\n=== ONLINE USERS (" + response.usernames().size() + ") ===");
            for (String username : response.usernames()) {
                System.out.println("  • " + username);
            }
            System.out.println("========================\n");
        } catch (JsonProcessingException e) {
            System.err.println("Failed to parse online users: " + e.getMessage());
        }
    }
}
