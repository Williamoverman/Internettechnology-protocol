package client.protocols.messages;

import client.protocols.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class BroadcastMessage implements Message {
    private final String username;
    private final String message;

    public BroadcastMessage(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("username");
        jsonValues.add("message");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        this.username = parsedValues.get("username");
        this.message = parsedValues.get("message");
    }

    @Override
    public void print() {
        System.out.println("[BROADCAST] <" + username + ">: " + message);
    }
}
