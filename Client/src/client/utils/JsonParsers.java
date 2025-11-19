package client.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class JsonParsers {
    private final ObjectMapper mapper;

    public JsonParsers() {
        this.mapper = new ObjectMapper();
    }

    /**
     * Parse and print incoming server messages
     * @param jsonPart the json to parse
     * @param type what type of server message
     */
    public void parseAndPrintMessage(String jsonPart, String type) {
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
    public void parseHangup(String jsonPart) {
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
            }
        } catch (Exception e) {
            System.err.println("Failed to parse hangup: " + e.getMessage());
        }
    }

    /**
     * parse errors
     * @param jsonPart json to parse
     */
    public void parseError(String jsonPart) {
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
    public void parseResponse(String jsonPart) {
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

    /**
     * parse HI
     * @param jsonPart json to parse
     */
    public void parseHi(String jsonPart) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);
            if (jsonNode.has("version"))
                System.out.println("Version: " + jsonNode.get("version"));
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
        }
    }

    public HashMap<String, String> genericParser(ArrayList<String> valuesToParse, String jsonBody) {
        HashMap<String, String> parsedValues = new HashMap<>();

        try {
            for (String value : valuesToParse) {
                JsonNode jsonNode = mapper.readTree(jsonBody);
                if (jsonNode.has(value)) {
                    parsedValues.put(value, jsonNode.get(value).asText());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return parsedValues;
    }
}
