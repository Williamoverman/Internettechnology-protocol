package client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MessageHandler {
    private final ServerConnection connection;

    public MessageHandler(ServerConnection connection) {
        this.connection = connection;
    }

    /**
     * Message handler for incoming server messages
     * @param message the message to handle
     */
    public void handleMessage(String message) {
        if (message == null)
            return;

        if (message.equalsIgnoreCase("ping")) {
            connection.sendMessage("PONG");
            return;
        }

        // Message if someone leaves
        if (message.startsWith("LEFT ")) {
            parseAndPrintMessage(message.substring("LEFT ".length()), "LEFT");
            return;
        }

        // exact match for server broadcast from other users
        if (message.startsWith("BROADCAST ")) {
            parseAndPrintMessage(message.substring("BROADCAST ".length()), "BROADCAST");
            return;
        }

        // Handle hangup (disconnect)
        if (message.startsWith("HANGUP ")) {
            parseHangup(message.substring("HANGUP ".length()));
            return;
        }

        // Handle pong error
        if (message.startsWith("PONG_ERROR ")) {
            parseError(message.substring("PONG_ERROR ".length()));
            return;
        }

        // Handle unknown command or parse error (plain text, no JSON)
        if (message.equals("UNKNOWN_COMMAND") || message.equals("PARSE_ERROR")) {
            System.out.println("[" + message + "]");
            return;
        }

        // server responses
        if (message.contains("RESP")) {
            if (message.startsWith("BROADCAST_RESP "))
                parseResponse(message.substring("BROADCAST_RESP ".length()));
            else if (message.startsWith("LOGON_RESP "))
                parseResponse(message.substring("LOGON_RESP ".length()));
            else if (message.startsWith("BYE_RESP "))
                parseResponse(message.substring("BYE_RESP ".length()));
        }
    }

    /**
     * Parse and print incoming server messages
     * @param jsonPart the json to parse
     * @param type what type of server message
     */
    private void parseAndPrintMessage(String jsonPart, String type) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);

            if (!jsonNode.has("username")) {
                System.err.println("Invalid " + type.toLowerCase() + " from server: missing username");
                return;
            }

            String username = jsonNode.get("username").asText();

            if (type.equals("BROADCAST") && jsonNode.has("message")) {
                // Broadcast type
                String userMessage = jsonNode.get("message").asText();
                System.out.println("[BROADCAST] <" + username + ">: " + userMessage);
            } else if (type.equals("LEFT")) {
                // LEFT type
                System.out.println("[LEFT] " + username + " has left the chat.");
            } else {
                System.err.println("Unknown message type: " + type);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse " + type.toLowerCase() + ": " + e.getMessage());
        }
    }

    /**
     * parse hangup
     * @param jsonPart json to parse
     */
    private void parseHangup(String jsonPart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);
            if (jsonNode.has("reason")) {
                int reason = jsonNode.get("reason").asInt();
                String desc = switch (reason) {
                    case 7000 -> "No pong received";
                    default -> "Unknown reason: " + reason;
                };
                System.err.println("[HANGUP] " + desc);
                connection.disconnect();
            }
        } catch (Exception e) {
            System.err.println("Failed to parse hangup: " + e.getMessage());
            connection.disconnect();
        }
    }

    /**
     * parse errors
     * @param jsonPart json to parse
     */
    private void parseError(String jsonPart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);
            if (jsonNode.has("code")) {
                int code = jsonNode.get("code").asInt();
                String desc = switch (code) {
                    case 8000 -> "Pong without ping";
                    default -> "Unknown error: " + code;
                };
                System.out.println("[ERROR] " + desc);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse error: " + e.getMessage());
        }
    }

    /**
     * parse response
     * @param jsonPart json to parse
     */
    private void parseResponse(String jsonPart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);
            if (jsonNode.has("status") && jsonNode.get("status").asText().equalsIgnoreCase("OK")) {
                System.out.println("[OK]");
            } else if (jsonNode.has("status") && jsonNode.get("status").asText().equalsIgnoreCase("ERROR")) {
                String code = jsonNode.has("code") ? jsonNode.get("code").asText() : "Unknown error";
                String desc = switch (code) {
                    case "5000" -> "User with this name already exists";
                    case "5001" -> "Username has an invalid format or length";
                    case "5002" -> "Already logged in";
                    case "6000" -> "You need to be logged in to broadcast messages";
                    default -> code;
                };
                System.out.println("[ERROR] " + desc);
            }
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
        }
    }
}