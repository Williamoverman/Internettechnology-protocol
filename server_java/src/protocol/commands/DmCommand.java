package protocol.commands;

import protocol.ICommandHandler;
import protocol.ClientMessenger;

public record DmCommand(ClientMessenger sender) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}
