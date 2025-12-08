package senders;

import connection.ServerConnection;

public class CommandSender {
    private final ServerConnection connection;

    public CommandSender(ServerConnection connection) {
        this.connection = connection;
    }

    public void sendCommand(String command) {
        connection.sendMessage(command);
    }

    public void login(String username) {
        connection.sendMessage("LOGON {\"username\":\"" + username + "\"}");
    }

    public void broadcast(String message) {
        connection.sendMessage("BROADCAST_REQ {\"message\":\"" + message + "\"}");
    }

    public void quit() {
        sendCommand("BYE");
    }

    public void online() { sendCommand("ONLINE_REQ"); }

    public void dm(String message) { sendCommand("DM_REQ " + message); }
}
