package protocols.messages.ToH;

import protocols.MessageHandler;
import responses.ToH.EndResultResponse;

import java.io.IOException;

public class EndResultMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            EndResultResponse response = mapper.readValue(jsonBody, EndResultResponse.class);
            System.out.printf(
                    """
                    Game Over
                    Winner: %s
                    Final : You %d – %d Opponent
                    %n""", response.winner(),
                response.finalScore().you(),
                response.finalScore().opponent()
            );
        } catch (IOException e) {
            System.err.println("Failed to parse status: " + e.getMessage());
        }
    }
}