package client.protocols;

public interface CommandHandler {
    /**
     * Execute command to the server or on the client
     */
    boolean execute(String command);
}
