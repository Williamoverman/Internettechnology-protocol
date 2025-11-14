package client;

public class CommandSender {
    private final ServerConnection connection;

    public CommandSender(ServerConnection connection) {
        this.connection = connection;
    }

    public void sendCommand(String command) {
        connection.sendMessage(command);
    }

    public void quit() {
        sendCommand("BYE");
    }
}
