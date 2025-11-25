package protocols.messages;

import protocols.MessageHandler;

public class StatusMessage implements MessageHandler {
    @Override
    public void handle(String jsonBody) {
        String code = jsonParser.parseStatusResponse(jsonBody);

        System.out.println(code);
    }
}
