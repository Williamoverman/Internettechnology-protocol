package client.protocols.messages;

import client.protocols.MessageHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class HiMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("version");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        String version = parsedValues.get("version");

        System.out.println("[VERSION] <" + version + ">: ");
    }
}
