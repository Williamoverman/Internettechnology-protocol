package protocol.commands.filetransfer;

import protocol.commands.ICommandHandler;

public record DeclineCommand(protocol.ClientMessenger messenger, connection.ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}