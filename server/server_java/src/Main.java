import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.start();
    }

    public void start() {
        try {
            ServerSocket serverSocket = new ServerSocket(Config.SERVER_PORT);
            while (true) {
                Socket socket = serverSocket.accept();

                Thread newUser = new Thread(() -> messageProcessing(socket));
                newUser.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void messageProcessing(Socket socket) {

    }
}