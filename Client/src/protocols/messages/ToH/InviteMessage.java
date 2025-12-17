package protocols.messages.ToH;

import protocols.MessageHandler;
import responses.ToH.InviteResponse;

import java.io.IOException;

public class InviteMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            InviteResponse response = mapper.readValue(jsonBody, InviteResponse.class);
            System.out.println("[TOH_INVITE] You have received an invite from " + response.from());
        } catch (IOException e) {
            System.err.println("Failed to parse status: " + e.getMessage());
        }
    }
}
