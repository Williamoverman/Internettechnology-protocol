package managers;

import connection.ClientConnection;
import protocol.ClientMessenger;

public class HeartbeatManager {
    private final ClientConnection connection;
    private final ClientMessenger messenger;

    private static final long PING_INTERVAL = 10000;
    private static final long TIMEOUT = 3000;

    private volatile long lastPongTime = System.currentTimeMillis();
    private volatile boolean awaitingPong = false;

    public HeartbeatManager(ClientConnection connection, ClientMessenger messenger) {
        this.connection = connection;
        this.messenger = messenger;
    }

    public synchronized void notifyPong() {
        awaitingPong = false;
        lastPongTime = System.currentTimeMillis();
    }

    public synchronized boolean isAwaitingPong() {
        return awaitingPong;
    }

    public void start() {
        try {
            Thread.sleep(PING_INTERVAL);

            while (connection.isConnected()) {
                awaitingPong = true;
                messenger.sendPing();

                long sentAt = System.currentTimeMillis();

                while (awaitingPong && System.currentTimeMillis() - sentAt < TIMEOUT) {
                    Thread.sleep(50);
                }

                if (awaitingPong) {
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
