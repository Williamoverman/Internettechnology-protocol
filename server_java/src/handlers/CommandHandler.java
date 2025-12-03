package handlers;

import dispatchers.CommandDispatcher;

public class CommandHandler {
    private final CommandDispatcher dispatcher;

    public CommandHandler(CommandDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * Command handler for receiving commands from clients
     * @param command the command to handle
     */
    public void handleCommand(String command) {
        if (command == null)
            return;

        String trimmedCommand = command.trim();

        String[] parts = trimmedCommand.split(" ", 2);
        String header = parts[0].toUpperCase();
        String jsonPayload = parts.length == 2 ? parts[1] : "";

        dispatcher.dispatch(header, jsonPayload);
    }
}
