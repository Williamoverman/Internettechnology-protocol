package protocol.commands;

import com.google.gson.JsonSyntaxException;
import connection.ClientConnection;
import managers.HeartbeatManager;
import protocol.ICommandHandler;
import requests.LogonRequest;
import protocol.ClientMessenger;

public record LogonCommand(ClientMessenger messenger, HeartbeatManager manager, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        try {
            LogonRequest request = gson.fromJson(jsonBody, LogonRequest.class);
            String username = request.username();
            if (username == null || username.isEmpty()) {
                messenger.sendError("LOGON_RESP", 5001);
                return;
            }

            if (registry.isLoggedIn(connection)) {
                messenger.sendError("LOGON_RESP", 5002);
                return;
            }

            if (registry.userExists(username)) {
                messenger.sendError("LOGON_RESP", 5000);
                return;
            }

            registry.addUser(username, connection);
            messenger.sendOK("LOGON_RESP");

            ClientMessenger.broadcastJoined(registry.getAllExcept(username), username);

            manager.notifyPong();
            new Thread(manager::start).start();
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
