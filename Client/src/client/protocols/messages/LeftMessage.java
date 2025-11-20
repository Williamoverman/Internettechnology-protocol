package client.protocols.messages;

import client.protocols.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class LeftMessage implements Message {
    private final String username;

    public LeftMessage(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("username");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        this.username = parsedValues.get("username");
    }

    @Override
    public void print() {
        System.out.println("[LEFT] <" + username + ">");
    }
}
