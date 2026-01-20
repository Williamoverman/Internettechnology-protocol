package protocol.commands.filetransfer;

import protocol.commands.ICommandHandler;

public record AcceptCommand(protocol.ClientMessenger messenger, connection.ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}