package protocol;

import connection.ClientConnection;
import utils.Config;

import java.util.List;

public class ClientMessenger {
    private final ClientConnection connection;

    public ClientMessenger(ClientConnection connection) {
        this.connection = connection;
    }

    /**
     * send wlcome to client
     */
    public void sendWelcome() {
        connection.sendMessage(MessageFormatter.createWelcome(Config.SERVER_VERSION));
    }

    /**
     * send ping to client
     */
    public void sendPing() {
        connection.sendMessage(MessageFormatter.createPing());
    }

    /**
     * Send unknown command to client
     */
    public void sendUnknownCommand() {
        connection.sendMessage(MessageFormatter.createUnknownCommand());
    }

    /**
     * send OK to client
     * @param header header to send (OK)
     */
    public void sendOK(String header) {
        connection.sendMessage(MessageFormatter.createOkResponse(header));
    }

    /**
     * Send error to client
     * @param header error header
     * @param code error code
     */
    public void sendError(String header, int code) {
        connection.sendMessage(MessageFormatter.createErrorResponse(header, code));
    }

    /**
     * Broadcasts a message to multiple clients
     * @param connections list of client connections
     * @param message the formatted message to send
     */
    public static void broadcast(List<ClientConnection> connections, String message) {
        connections.forEach(conn -> conn.sendMessage(message));
    }

    /**
     * sends JOINED notification to multiple clients
     * @param connections list of client connections
     * @param username the username that joined
     */
    public static void broadcastJoined(List<ClientConnection> connections, String username) {
        String message = MessageFormatter.createJoined(username);
        broadcast(connections, message);
    }

    /**
     * Sends LEFT notif to multiple clients
     * @param connections list of connectedd clients
     * @param username the username that left
     */
    public static void broadcastLeft(List<ClientConnection> connections, String username) {
        String message = MessageFormatter.createLeft(username);
        broadcast(connections, message);
    }

    /**
     * Sends a message to a single specific connection
     * @param connection the target connection
     * @param message the formatted message to send
     */
    public static void sendTo(ClientConnection connection, String message) {
        connection.sendMessage(message);
    }
}
