package connection;

import managers.ToHManager;
import managers.UserRegistry;
import protocol.ClientMessenger;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnection {
    private Socket clientSocket;
    private BufferedReader reader;
    private PrintWriter writer;
    private volatile boolean connected;

    public ClientConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true);
        this.connected = true;
    }

    public String readCommand() throws IOException {
        String line = reader.readLine();
        if (line == null)
            connected = false;
        return line;
    }

    public void sendMessage(String message) {
        if (connected && writer != null)
            writer.println(message);
    }

    public boolean isConnected() {
        return connected && !clientSocket.isClosed();
    }

    public void exit() {
        connected = false;

        UserRegistry userRegistry = UserRegistry.getInstance();
        String username = userRegistry.getUsername(this);
        if (username != null) {
            ToHManager.getInstance().removePlayer(username);
            ClientMessenger.broadcastLeft(userRegistry.getAllExcept(username), username);
        }

        userRegistry.removeConnection(this);

        try {
            reader.close();
            writer.close();
            clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}