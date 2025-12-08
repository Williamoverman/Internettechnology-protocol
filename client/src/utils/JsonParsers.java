package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

// found a better way than just this weird generic parser i made lol
@Deprecated
public class JsonParsers {
    private final ObjectMapper mapper;

    public JsonParsers() {
        this.mapper = new ObjectMapper();
    }

    /**
     * parse status response
     * @param jsonPart json to parse
     */
    public String parseStatusResponse(String jsonPart) {
        String status = "";

        try {
            JsonNode jsonNode = mapper.readTree(jsonPart);
            if (jsonNode.has("status") && jsonNode.get("status").asText().equalsIgnoreCase("OK")) {
                status = "[" + jsonNode.get("status").asText() + "]";
            } else if (jsonNode.has("code")) {
                String code = jsonNode.has("code") ? jsonNode.get("code").asText() : "Unknown error";
                String desc = switch (code) {
                    case "5000" -> "User with this name already exists";
                    case "5001" -> "Username has an invalid format or length";
                    case "5002" -> "Already logged in";
                    case "6000" -> "You need to be logged in to broadcast messages";
                    case "8000" -> "Pong without ping";
                    default -> code;
                };
                status = "[ERROR] " + desc;
            }
        } catch (Exception e) {
            System.err.println("Failed to parse response: " + e.getMessage());
        }

        return status;
    }

    /**
     * generic parser
     * @param valuesToParse list of values to look for
     * @param jsonBody the values to parse
     * @return map of parsed values
     */
    public HashMap<String, String> genericParser(ArrayList<String> valuesToParse, String jsonBody) {
        HashMap<String, String> parsedValues = new HashMap<>();

        try {
            JsonNode jsonNode = mapper.readTree(jsonBody);
            for (String value : valuesToParse) {
                if (jsonNode.has(value))
                    parsedValues.put(value, jsonNode.get(value).asText());
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        return parsedValues;
    }
}
