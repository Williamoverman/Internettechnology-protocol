package protocols.messages.ToH;

import protocols.MessageHandler;
import responses.ToH.TieResponse;

import java.io.IOException;

public class TieMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            TieResponse response = mapper.readValue(jsonBody, TieResponse.class);
            System.out.println("[TOH_TIE] Its a tie!\nRound: " + response.round());
        } catch (IOException e) {
            System.err.println("Failed to parse status: " + e.getMessage());
        }
    }
}