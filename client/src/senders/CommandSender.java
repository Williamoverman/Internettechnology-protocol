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

    public void invite(String opponent) {
        connection.sendMessage("TOH_INVITE_REQ {\"opponent\":\"" + opponent + "\"}");
    }

    public void choice(String choice) {
        connection.sendMessage("TOH_CHOICE {\"choice\":\"" + choice + "\"}");
    }

    public void accept() {
        sendCommand("TOH_ACCEPT");
    }

    public void decline() {
        sendCommand("TOH_DECLINE");
    }

    public void fileSend(String recipient, String filename, long size, String checksum) {
        String json = String.format(
                "{\"recipient\":\"%s\",\"filename\":\"%s\",\"size\":%d,\"checksum\":\"%s\"}",
                escapeJson(recipient), escapeJson(filename), size, checksum
        );
        connection.sendMessage("FILE_SEND_REQ " + json);
    }

    public void fileAccept() {
        connection.sendMessage("FILE_ACCEPT");
    }

    public void fileDecline() {
        connection.sendMessage("FILE_DECLINE");
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
