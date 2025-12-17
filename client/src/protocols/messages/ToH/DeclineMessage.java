package protocols.messages.ToH;

import protocols.MessageHandler;
import responses.ToH.DeclineResponse;

import java.io.IOException;

public class DeclineMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            DeclineResponse response = mapper.readValue(jsonBody, DeclineResponse.class);
            System.out.println("[TOH_DECLINED] Your offer has been declined by " + response.from());
        } catch (IOException e) {
            System.err.println("Failed to parse status: " + e.getMessage());
        }
    }
}