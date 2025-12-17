package protocol.commands.ToHGame;

import connection.ClientConnection;
import managers.ToHManager;
import protocol.MessageFormatter;
import protocol.commands.ICommandHandler;
import protocol.ClientMessenger;
import requests.ToHGame.InviteRequest;

public record InviteCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        InviteRequest request = gson.fromJson(jsonBody, InviteRequest.class);
        ClientConnection opponentConnection = registry.getConnection(request.opponent());

        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("TOH_RESP", 67);
            return;
        }

        // check if user exists
        if (opponentConnection == null) {
            messenger.sendError("TOH_RESP", 10000);
            return;
        }

        // check if user tries to initiate a game with themselves
        if (opponentConnection.equals(connection)) {
            messenger.sendError("TOH_RESP", 10001);
            return;
        }

        String inviter = registry.getUsername(connection);

        ToHManager toHManager = ToHManager.getInstance();
        // check if inviter or invitee already in  a game and sendinvite if they are not
        if (!toHManager.sendInvite(inviter, request.opponent())) {
            messenger.sendError("TOH_RESP", 10002);
            return;
        }

        messenger.sendOK("TOH_RESP");
        String message = MessageFormatter.createToHInvite(inviter);
        ClientMessenger.sendTo(opponentConnection, message);
    }
}