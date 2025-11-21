package client.protocols.messages;

import client.protocols.MessageHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class BroadcastMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("username");
        jsonValues.add("message");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        String username = parsedValues.get("username");
        String message = parsedValues.get("message");

        System.out.println("[BROADCAST] <" + username + ">: " + message);
    }
}
