package client;

import utils.Config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ServerConnection {
    private Socket socket;
    private BufferedReader reader;
    private PrintWriter writer;
    private boolean connected;

    public void connect() throws IOException {
        socket = new Socket(Config.SERVER_ADDRESS, Config.SERVER_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new PrintWriter(socket.getOutputStream(), true);
        connected = true;

        String greeting = reader.readLine();
        System.out.println("Server: " + greeting);
    }

    public void disconnect() {
        connected = false;
        closeResources();
    }

    private void closeResources() {
        try {
            if (writer != null) writer.close();
            if (reader != null) reader.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }

    public void sendMessage(String message) {
        if (connected && writer != null) {
            writer.println(message);
            writer.flush();
        }
    }

    public String readMessage() throws IOException {
        if (connected && reader != null)
            return reader.readLine();
        return null;
    }

    public boolean isConnected() {
        return connected && socket != null && !socket.isClosed();
    }
}
