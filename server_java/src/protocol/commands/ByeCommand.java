package protocol.commands;

import connection.ClientConnection;
import protocol.ICommandHandler;
import protocol.ClientMessenger;

public record ByeCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        messenger.sendOK("BYE_RESP");
        connection.exit();
    }
}
