package managers;

import handlers.ClientHandler;
import utils.Config;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketManager {
    private ServerSocket serverSocket;
    private volatile boolean running;
    private ExecutorService executor;

    public void setup() throws IOException {
        serverSocket = new ServerSocket(Config.SERVER_PORT);
        running = true;
        executor = Executors.newFixedThreadPool(5);
        System.out.println("Server running, listening...");

        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                executor.submit(new ClientHandler(clientSocket));
            } catch (IOException e) {
                if (running)
                    System.err.println("Error accepting client connection " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed())
                serverSocket.close();

            if (executor != null)
                executor.shutdownNow();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
