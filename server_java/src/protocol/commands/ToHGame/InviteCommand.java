package protocol.commands.ToHGame;

import connection.ClientConnection;
import protocol.commands.ICommandHandler;
import protocol.ClientMessenger;
import requests.ToHGame.InviteRequest;

public record InviteCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        InviteRequest request = gson.fromJson(jsonBody, InviteRequest.class);

        // check if user exists
        if (registry.getConnection(request.opponent()) == null) {
            messenger.sendError("TOH_RESP", 10000);
            return;
        }

        // check if user tries to initiate a game with themselves
        if (registry.getConnection(request.opponent()) == connection) {
            messenger.sendError("TOH_RESP", 10001);
            return;
        }


        messenger.sendOK("TOH_RESP");
    }
}