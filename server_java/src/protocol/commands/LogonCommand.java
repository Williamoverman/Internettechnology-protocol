package protocol.commands;

import com.google.gson.JsonSyntaxException;
import connection.ClientConnection;
import managers.HeartbeatManager;
import managers.UserRegistry;
import protocol.ICommandHandler;
import requests.LogonRequest;
import sender.MessageSender;

public record LogonCommand(MessageSender sender, HeartbeatManager manager, ClientConnection connection) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {
        try {
            LogonRequest request = gson.fromJson(jsonBody, LogonRequest.class);
            String username = request.username();
            if (username == null || username.isEmpty()) {
                sender.sendError("LOGON_RESP", 5001);
                return;
            }

            if (UserRegistry.getInstance().isLoggedIn(connection)) {
                sender.sendError("LOGON_RESP", 5002);
                return;
            }

            if (UserRegistry.getInstance().userExists(username)) {
                sender.sendError("LOGON_RESP", 5000);
                return;
            }

            UserRegistry.getInstance().addUser(username, connection);
            sender.sendOK("LOGON_RESP");

            UserRegistry.getInstance().notifyAllJoined(username);

            manager.notifyPong();
            new Thread(manager::start).start();
        } catch (JsonSyntaxException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
