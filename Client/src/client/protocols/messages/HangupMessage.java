package client.protocols.messages;

import client.protocols.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class HangupMessage implements Message {
    private final String reason;

    public HangupMessage(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("reason");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        this.reason = parsedValues.get("reason");
    }

    @Override
    public void print() {
        System.out.println("[HANGUP] " + reason);
    }
}
