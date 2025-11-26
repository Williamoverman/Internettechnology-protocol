import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    public static void main(String[] args) {
        SocketManager socketManager = new SocketManager();
        try {
            socketManager.setup();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            socketManager.stop();
        }
    }
}