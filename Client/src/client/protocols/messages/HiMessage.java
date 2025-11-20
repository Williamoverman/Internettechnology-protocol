package client.protocols.messages;

import client.protocols.Message;

import java.util.ArrayList;
import java.util.HashMap;

public class HiMessage implements Message {
    private final String version;

    public HiMessage(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("version");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        this.version = parsedValues.get("version");
    }

    @Override
    public void print() {
        System.out.println("[VERSION] <" + version + ">: ");
    }
}
