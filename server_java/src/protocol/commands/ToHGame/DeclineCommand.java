package protocol.commands.ToHGame;

import connection.ClientConnection;
import managers.ToHManager;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;

public record DeclineCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("TOH_RESP", 67);
            return;
        }

        String decliner = registry.getUsername(connection);
        ToHManager gameManager = ToHManager.getInstance();

        String inviter = gameManager.declineInvite(decliner);
        if (inviter == null) {
            messenger.sendError("TOH_RESP", 10003);
            return;
        }

        ClientConnection inviterConnection = registry.getConnection(inviter);
        if (inviterConnection != null) {
            String message = MessageFormatter.createDeclined(decliner);
            ClientMessenger.sendTo(inviterConnection, message);
        }
    }
}
