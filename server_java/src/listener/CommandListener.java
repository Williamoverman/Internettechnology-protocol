package listener;

import connection.ClientConnection;
import handlers.CommandHandler;

import java.io.IOException;

public class CommandListener implements Runnable {
    private final ClientConnection connection;
    private final CommandHandler handler;
    private volatile boolean running;

    public CommandListener(ClientConnection connection, CommandHandler handler) {
        this.connection = connection;
        this.handler = handler;
        this.running = true;
    }

    @Override
    public void run() {
        try {
            while (running && connection.isConnected()) {
                String command = connection.readCommand();
                if (command == null)
                    break;
                handler.handleCommand(command);
            }
        } catch (IOException e) {
            if (running)
                System.err.println(e.getMessage());
        } finally {
            System.out.println("Command listener stopped");
        }
    }

    public void stop() {
        running = false;
    }
}
