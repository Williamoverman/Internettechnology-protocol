package protocol.commands;

import protocol.ICommandHandler;
import protocol.ClientMessenger;

public record OnlineCommand(ClientMessenger sender) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}
