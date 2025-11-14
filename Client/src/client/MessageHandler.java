package client;

public class MessageHandler {
    private final ServerConnection connection;

    public MessageHandler(ServerConnection connection) {
        this.connection = connection;
    }

    public void handleMessage(String message) {
        if (message == null)
            return;

        if (message.equalsIgnoreCase("ping")) {
            connection.sendMessage("PONG");
            return;
        }

        System.out.println("Server: " + message);
    }
}
