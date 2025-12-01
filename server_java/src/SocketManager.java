import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketManager {
    private ServerSocket serverSocket;
    private boolean running;

    public void setup() throws IOException {
        serverSocket = new ServerSocket(Config.SERVER_PORT);
        running = true;

        while (running) {
            Socket clientSocket = serverSocket.accept();
            new Thread(new ClientHandler(clientSocket)).start();
        }
    }

    public void stop() {
        running = false;
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
