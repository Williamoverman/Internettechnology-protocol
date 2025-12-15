package protocol.commands.common;

import com.google.gson.JsonSyntaxException;
import connection.ClientConnection;
import protocol.commands.ICommandHandler;
import protocol.MessageFormatter;
import requests.common.BroadcastRequest;
import protocol.ClientMessenger;

public record BroadcastCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        try {
            if (!registry.isLoggedIn(connection)) {
                messenger.sendError("BROADCAST_RESP", 67);
                return;
            }

            BroadcastRequest request = gson.fromJson(jsonBody, BroadcastRequest.class);
            String message = request.message();
            if (message == null || message.isEmpty())
                return;

            String username = registry.getUsername(connection);

            messenger.sendOK("BROADCAST_RESP");
            String broadcastMessage = MessageFormatter.createBroadcast(username, message);
            ClientMessenger.broadcast(registry.getAllExcept(username), broadcastMessage);
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
