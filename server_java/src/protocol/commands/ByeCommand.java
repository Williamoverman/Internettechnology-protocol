package protocol.commands;

import protocol.ICommandHandler;
import protocol.ClientMessenger;

public record ByeCommand(ClientMessenger sender) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}
