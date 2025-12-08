package protocol.commands;

import connection.ClientConnection;
import protocol.ICommandHandler;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import requests.DmRequest;

public record DmCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("BROADCAST_RESP", 67);
            return;
        }

        DmRequest request = gson.fromJson(jsonBody, DmRequest.class);
        String username = request.username();
        String message = request.message();

        if (message == null || message.isEmpty())
            return;

        if (!registry.userExists(username)) {
            messenger.sendError("DM_RESP", 9000);
            return;
        }

        ClientConnection dmTo = registry.getConnection(username);
        if (dmTo == connection) {
            messenger.sendError("DM_RESP", 9001);
            return;
        }

        String finalMessage = MessageFormatter.createDm(registry.getUsername(connection), message);

        messenger.sendOK("DM_RESP");
        ClientMessenger.sendTo(dmTo, finalMessage);
    }
}
