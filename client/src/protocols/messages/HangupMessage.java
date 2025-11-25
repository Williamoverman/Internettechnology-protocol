package protocols.messages;

import protocols.MessageHandler;

import java.util.ArrayList;
import java.util.HashMap;

public class HangupMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        ArrayList<String> jsonValues = new ArrayList<>();
        jsonValues.add("reason");

        HashMap<String, String> parsedValues = jsonParser.genericParser(jsonValues, jsonBody);
        String reason = parsedValues.get("reason");

        System.out.println("[HANGUP] " + reason);
    }
}
