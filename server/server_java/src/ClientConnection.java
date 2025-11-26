import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

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

    public void sendMessage() {

    }

    public String readCommand() throws IOException {
        if (connected && input != null)
            return input.readAllBytes();
    }
}
