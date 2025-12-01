import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ClientConnection {
    private Socket clientSocket;
    private InputStream input;
    private OutputStream output;
    private boolean connected;

    public ClientConnection(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.input = clientSocket.getInputStream();
        this.output = clientSocket.getOutputStream();
        this.connected = true;
    }

    public void exit() throws IOException {
        if (input != null) input.close();
        if (output != null) output.close();
        if (clientSocket != null) clientSocket.close();
    }

    public void sendMessage(String message) throws IOException {
        if (connected && output != null) {
            output.write(message.getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
    }

    public String readCommand() throws IOException {
        if (connected && input != null)
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        return null;
    }

    public boolean isConnected() {
        return connected && clientSocket != null && !clientSocket.isClosed();
    }
}