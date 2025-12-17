package protocols.messages.ToH;

import protocols.MessageHandler;
import responses.ToH.StartResponse;

import java.io.IOException;

public class StartMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            StartResponse response = mapper.readValue(jsonBody, StartResponse.class);
            System.out.println("[TOH_START] Your opponent is " + response.opponent() + " \nRound: " + response.round());
        } catch (IOException e) {
            System.err.println("Failed to parse status: " + e.getMessage());
        }
    }
}
