package client.protocols.messages;

import client.protocols.Message;

public class StatusMessage implements Message {
    private final String code;

    public StatusMessage(String jsonBody) {
        this.code = jsonParser.parseStatusResponse(jsonBody);
    }

    @Override
    public void print() {
        System.out.println(code);
    }
}
