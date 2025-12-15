package protocol.commands.ToHGame;

import connection.ClientConnection;
import protocol.ClientMessenger;
import protocol.commands.ICommandHandler;

public record AcceptCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}
