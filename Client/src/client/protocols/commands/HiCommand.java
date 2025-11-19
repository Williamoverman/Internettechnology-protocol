package client.protocols.commands;

import java.util.ArrayList;
import java.util.HashMap;

public class HiCommand implements Command {
    private final String version;

    public HiCommand(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("version");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        this.version = parsedValues.get("version");
    }

    public void print() {
        System.out.println("[VERSION] <" + version + ">: ");
    }
}
