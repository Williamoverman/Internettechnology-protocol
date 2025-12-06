package handlers;

import connection.ClientConnection;
import dispatchers.CommandDispatcher;
import listener.CommandListener;
import managers.HeartbeatManager;
import sender.MessageSender;

import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private CommandListener commandListener;
    private ClientConnection clientConnection;

    public ClientHandler(Socket clientSocket) throws IOException {
        System.out.println("New client");
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            initialize();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            disconnect();
        }
    }

    private void initialize() throws IOException {
        clientConnection = new ClientConnection(clientSocket);
        MessageSender sender = new MessageSender(clientConnection);
        HeartbeatManager heartBeatManager = new HeartbeatManager(clientConnection, sender);

        CommandDispatcher dispatcher = new CommandDispatcher(sender, heartBeatManager, clientConnection);
        CommandHandler commandHandler = new CommandHandler(dispatcher);

        sender.sendWelcome();

        commandListener = new CommandListener(clientConnection, commandHandler);
        commandListener.run();
    }

    private void disconnect() {
        if (commandListener != null)
            commandListener.stop();

        if (clientConnection != null)
            clientConnection.exit();
    }
}
