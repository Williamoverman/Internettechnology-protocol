import connection.ServerConnection;
import handlers.MessageHandler;
import handlers.UserInputHandler;
import listeners.MessageListener;
import senders.CommandSender;

import java.io.*;

public class Client {
    private ServerConnection connection;
    private MessageHandler messageHandler;
    private MessageListener messageListener;
    private CommandSender commandSender;
    private UserInputHandler inputHandler;
    private Thread listenerThread;

    public static void main(String[] args) {
        Client client = new Client();
        client.start();
    }

    public void start() {
        try {
            initialize();
            startMessageListening();
            handleUserInput();
        } catch (IOException e) {
            System.err.println("Failed to connect: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void initialize() throws IOException {
        System.out.println("Connecting to server...");

        connection = new ServerConnection();
        connection.connect();

        messageHandler = new MessageHandler(connection);
        commandSender = new CommandSender(connection);
        inputHandler = new UserInputHandler(commandSender);
        messageListener = new MessageListener(connection, messageHandler);

        System.out.println("Connected successfully!\n");
    }

    private void startMessageListening() {
        listenerThread = new Thread(messageListener);
        listenerThread.start();
    }

    private void handleUserInput() {
        inputHandler.start();
    }

    private void shutdown() {
        System.out.println("\nShutting down...");

        if (messageListener != null)
            messageListener.stop();

        if (inputHandler != null)
            inputHandler.close();

        if (connection != null)
            connection.disconnect();

        System.out.println("Disconnected");
    }
}