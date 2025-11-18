package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageHandler {
    private final ServerConnection connection;

    public MessageHandler(ServerConnection connection) {
        this.connection = connection;
    }

    public void handleMessage(String message) {
        if (message == null)
            return;

        if (message.equalsIgnoreCase("ping")) {
            connection.sendMessage("PONG");
            return;
        }

        // exact match for server broadcast from other users
        if (message.startsWith("BROADCAST ")) {
            parseAndPrintBroadcast(message.substring("BROADCAST ".length()));
            return;
        }

        // server responses
        if (message.contains("RESP")) {
            if (message.startsWith("BROADCAST_RESP "))
                parseResponse(message.substring("BROADCAST_RESP ".length()));
            else if (message.startsWith("LOGON_RESP"))
                parseResponse(message.substring("LOGON_RESP ".length()));
        }
    }

    private void parseAndPrintBroadcast(String jsonPart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);

            if (!jsonNode.has("username") || !jsonNode.has("message")) {
                System.err.println("Invalid broadcast from server: missing username/message");
                return;
            }

            String username = jsonNode.get("username").asText();
            String userMessage = jsonNode.get("message").asText();

            System.out.println("[BROADCAST] <" + username + ">: " + userMessage);
        } catch (Exception e) {
            System.err.println("Failed to parse broadcast: " + e.getMessage());
        }
    }

    private void parseResponse(String jsonPart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);
            if (jsonNode.has("status") && jsonNode.get("status").asText().equalsIgnoreCase("OK")) {
                System.out.println("[OK]");
            } else if (jsonNode.has("status") && jsonNode.get("status").asText().equalsIgnoreCase("ERROR")) {
                String code = jsonNode.has("code") ? jsonNode.get("code").asText() : "Unknown error";
                if (code.equalsIgnoreCase("6000"))
                    code = "You need to be logged in to broadcast messages";

                System.out.println("[ERROR] " + code);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse broadcast response: " + e.getMessage());
        }
    }
}
