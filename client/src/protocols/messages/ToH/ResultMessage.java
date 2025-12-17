package protocols.messages.ToH;

import protocols.MessageHandler;
import responses.ToH.ResultResponse;

import java.io.IOException;

public class ResultMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        try {
            ResultResponse response = mapper.readValue(jsonBody, ResultResponse.class);
            System.out.printf(
                    """
                    Round %d | %s
                    Winner: %s
                    Score : You %d – %d Opponent
                    %n""", response.round(),
                response.coin(),
                response.winner(),
                response.score().you(),
                response.score().opponent()
            );
        } catch (IOException e) {
            System.err.println("Failed to parse status: " + e.getMessage());
        }
    }
}