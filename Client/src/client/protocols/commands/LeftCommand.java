package client.protocols.commands;

import client.protocols.Command;

import java.util.ArrayList;
import java.util.HashMap;

public class LeftCommand implements Command {
    private final String username;

    public LeftCommand(String jsonBody) {
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
