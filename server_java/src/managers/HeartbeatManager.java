package managers;

import connection.ClientConnection;
import protocol.ClientMessenger;

public class HeartbeatManager {
    private final ClientConnection connection;
    private final ClientMessenger messenger;

    private static final long PING_INTERVAL = 10000;
    private static final long TIMEOUT = 3000;
    private volatile long lastPongTime = System.currentTimeMillis();

    public HeartbeatManager(ClientConnection connection, ClientMessenger messenger) {
        this.connection = connection;
        this.messenger = messenger;
    }

    public void notifyPong() {
        lastPongTime = System.currentTimeMillis();
    }

    public void start() {
        long gracePeriod = System.currentTimeMillis() + 15000;

        try {
            Thread.sleep(5000);

            while (connection.isConnected()) {
                messenger.sendPing();

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
