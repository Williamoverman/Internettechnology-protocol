package protocols.messages;

import protocols.MessageHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class LeftMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("username");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        String username = parsedValues.get("username");

        System.out.println("[LEFT] <" + username + ">");
    }
}
