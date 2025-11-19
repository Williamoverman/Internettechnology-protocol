package client.protocols.commands;

import client.protocols.Command;

import java.util.ArrayList;
import java.util.HashMap;

public class BroadcastCommand implements Command {
    private final String username;
    private final String message;

    public BroadcastCommand(String jsonBody) {
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
