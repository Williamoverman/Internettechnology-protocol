package listeners;

import handlers.MessageHandler;
import connection.ServerConnection;

import java.io.IOException;

public class MessageListener implements Runnable {
    private final ServerConnection connection;
    private final MessageHandler messageHandler;
    private volatile boolean running;

    public MessageListener(ServerConnection connection, MessageHandler messageHandler) {
        this.connection = connection;
        this.messageHandler = messageHandler;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running && connection.isConnected()) {
                String message = connection.readMessage();
                if (message == null)
                    break;
                messageHandler.handleMessage(message);
            }
        } catch (IOException e) {
            if (running)
                System.err.println(e.getMessage());
        } finally {
            System.out.println("Message listener stopped");
        }
    }

    public void stop() {
        running = false;
    }
}
