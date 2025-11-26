import java.io.IOException;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private ClientConnection clientConnection;
    private CommandHandler commandHandler;
    private CommandListener commandListener;
    private MessageSender messageSender;

    public ClientHandler(Socket clientSocket) throws IOException {
        this.clientSocket = clientSocket;
        this.clientConnection = new ClientConnection(clientSocket);
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

    }

    private void disconnect() {

    }
}
