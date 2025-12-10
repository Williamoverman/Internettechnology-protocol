package protocol.commands;

import connection.ClientConnection;
import managers.ToHManager;
import protocol.ICommandHandler;
import protocol.ClientMessenger;
import protocol.MessageFormatter;
import requests.ToHRequest;

public record ToHCommand(ClientMessenger messenger, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (!registry.isLoggedIn(connection)) {
            messenger.sendError("TOH_GAME_RESP", 67);
            return;
        }

        ToHRequest request = gson.fromJson(jsonBody, ToHRequest.class);
        ToHManager toHManager = ToHManager.getInstance();

        if (request.opponent() != null)
            handleGameCreation(request.opponent(), connection, toHManager);

        if (request.choice() != null)
            handleChoice(request.choice(), connection, toHManager);
    }

    private void handleGameCreation(String opponent, ClientConnection playerOne, ToHManager manager) {
        ClientConnection playerTwo = registry.getConnection(opponent);
        if (playerTwo == null) {
            messenger.sendError("TOH_GAME_RESP", 10000);
            return;
        }

        if (playerTwo == playerOne) {
            messenger.sendError("TOH_GAME_RESP", 10001);
            return;
        }

        if (manager.isInGame(playerTwo)) {
            messenger.sendError("TOH_GAME_RESP", 10002);
            return;
        }

        if (manager.hasPendingInvite(playerTwo)) {
            messenger.sendError("TOH_GAME_RESP", 10003);
            return;
        }

        messenger.sendOK("TOH_GAME_RESP");
        manager.invite(playerOne, playerTwo);

        String message = MessageFormatter.createInvite(registry.getUsername(connection));
        ClientMessenger.sendTo(playerTwo, message);
    }

    private void handleChoice(String choice, ClientConnection connection, ToHManager manager) {
        if (!choice.equalsIgnoreCase("heads") || !choice.equalsIgnoreCase("tails")) {
            messenger.sendError("TOH_GAME_RESP", 10004);
            return;
        }

        ClientConnection playerOne = manager.getPendingInviter(connection);
        if (playerOne != null) {

        }
    }
}