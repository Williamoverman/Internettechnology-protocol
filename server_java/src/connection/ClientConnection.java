package connection;

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
        this.writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), StandardCharsets.UTF_8), true); // autoFlush
        this.connected = true;
    }

    public String readCommand() throws IOException {
        String line = reader.readLine();
        if (line == null) connected = false;
        return line;
    }

    public void sendMessage(String message) {
        if (connected && writer != null) {
            writer.println(message);
        }
    }

    public boolean isConnected() {
        return connected && !clientSocket.isClosed();
    }

    public void exit() {
        connected = false;
        try { reader.close(); } catch (Exception e) {}
        try { writer.close(); } catch (Exception e) {}
        try { clientSocket.close(); } catch (Exception e) {}
    }
}