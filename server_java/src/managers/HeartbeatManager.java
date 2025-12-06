package managers;

import connection.ClientConnection;
import sender.MessageSender;

public class HeartbeatManager {
    private final ClientConnection connection;
    private final MessageSender sender;

    private static final long PING_INTERVAL = 10000;
    private static final long TIMEOUT = 3000;
    private volatile long lastPongTime = System.currentTimeMillis();

    public HeartbeatManager(ClientConnection connection, MessageSender sender) {
        this.connection = connection;
        this.sender = sender;
    }

    public void notifyPong() {
        lastPongTime = System.currentTimeMillis();
    }

    public void start() {
        long gracePeriod = System.currentTimeMillis() + 15000;

        try {
            Thread.sleep(5000);

            while (connection.isConnected()) {
                sender.ping();

                long now = System.currentTimeMillis();

                if (now > gracePeriod && now - lastPongTime > (PING_INTERVAL + TIMEOUT)) {
                    System.out.println("Client timeout - no PONG received");
                    connection.exit();
                    break;
                }

                Thread.sleep(PING_INTERVAL);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
