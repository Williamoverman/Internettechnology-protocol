package client.handlers;

import client.connection.ServerConnection;
import client.dispatchers.MessageDispatcher;

public class MessageHandler {
    private final ServerConnection connection;
    private final MessageDispatcher dispatcher;

    public MessageHandler(ServerConnection connection) {
        this.connection = connection;
        this.dispatcher = new MessageDispatcher();
    }

    /**
     * Message handler for incoming server messages
     * @param message the message to handle
     */
    public void handleMessage(String message) {
        if (message == null)
            return;

        String trimmedMessage = message.trim();

        if (trimmedMessage.equalsIgnoreCase("ping")) {
            connection.sendMessage("PONG");
            return;
        }

        // Handle unknown command or parse error
        if (trimmedMessage.equals("UNKNOWN_COMMAND") || trimmedMessage.equals("PARSE_ERROR")) {
            System.out.println("[" + trimmedMessage + "]");
            return;
        }

        String[] parts = trimmedMessage.split(" ", 2);
        String header = parts[0].toUpperCase();
        String jsonBody = parts[1];

        // dispatch the message to the correct handler
        dispatcher.dispatch(header, jsonBody);
    }
}