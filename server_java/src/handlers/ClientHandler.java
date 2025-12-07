package handlers;

import connection.ClientConnection;
import dispatchers.CommandDispatcher;
import listener.CommandListener;
import managers.HeartbeatManager;
import protocol.ClientMessenger;

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
        ClientMessenger messenger = new ClientMessenger(clientConnection);
        HeartbeatManager heartBeatManager = new HeartbeatManager(clientConnection, messenger);

        CommandDispatcher dispatcher = new CommandDispatcher(messenger, heartBeatManager, clientConnection);
        CommandHandler commandHandler = new CommandHandler(dispatcher);

        messenger.sendWelcome();

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
