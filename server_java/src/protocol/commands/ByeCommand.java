package protocol.commands;

import com.google.gson.JsonSyntaxException;
import connection.ClientConnection;
import managers.UserRegistry;
import protocol.ICommandHandler;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import requests.BroadcastRequest;

public record ByeCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        try {
            UserRegistry registry = UserRegistry.getInstance();
            String username = registry.getUsername(connection);

            messenger.sendOK("BYE_RESP");
            registry.removeUser(username);
            ClientMessenger.broadcastLeft(registry.getAllExcept(username), username);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
