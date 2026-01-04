package protocol.commands.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonSyntaxException;
import connection.ClientConnection;
import managers.HeartbeatManager;
import protocol.commands.ICommandHandler;
import requests.common.LogonRequest;
import protocol.ClientMessenger;

public record LogonCommand(ClientMessenger messenger, HeartbeatManager manager, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        if (jsonBody == null || jsonBody.isBlank()) {
            messenger.sendError("LOGON_RESP", 5001);
            return;
        }

        final LogonRequest request;

        try {
            request = gson.fromJson(jsonBody, LogonRequest.class);
        } catch (JsonSyntaxException e) {
            messenger.sendParseError();
            return;
        }

        if (request == null || request.username() == null) {
            messenger.sendParseError();
            return;
        }

        String username = request.username();

        if (!username.matches("^[A-Za-z0-9_]{3,14}$")) {
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

        ClientMessenger.broadcastJoined(
                registry.getAllExcept(username),
                username
        );

        manager.notifyPong();
        new Thread(manager::start).start();
    }
}
