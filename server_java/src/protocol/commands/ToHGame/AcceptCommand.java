package protocol.commands.ToHGame;

import connection.ClientConnection;
import games.ToH;
import managers.ToHManager;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;

public record AcceptCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("TOH_RESP", 67);
            return;
        }

        String accepter = registry.getUsername(connection);
        ToHManager gameManager = ToHManager.getInstance();

        String inviter = gameManager.acceptInvite(accepter);
        if (inviter == null) {
            messenger.sendError("TOH_RESP", 10003);
            return;
        }

        ToH game = gameManager.getGame(accepter);
        messenger.sendStart(inviter, game.getRoundNumber());

        ClientConnection inviterConnection = registry.getConnection(inviter);
        if (inviterConnection != null) {
            String message = MessageFormatter.createStart(accepter, game.getRoundNumber());
            ClientMessenger.sendTo(inviterConnection, message);
        }
    }
}
