package protocol.commands.filetransfer;

import connection.ClientConnection;
import protocol.ClientMessenger;
import protocol.commands.ICommandHandler;

public record SendCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}