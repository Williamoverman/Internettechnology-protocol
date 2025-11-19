package client.protocols.commands;

import client.protocols.Command;

import java.util.ArrayList;
import java.util.HashMap;

public class HangupCommand implements Command {
    private final String reason;

    public HangupCommand(String jsonBody) {
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
