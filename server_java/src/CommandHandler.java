public class CommandHandler {
    private final CommandDispatcher dispatcher;

    public CommandHandler() {
        this.dispatcher = new CommandDispatcher();
    }

    /**
     * Command handler for receiving commands from clients
     * @param command the command to handle
     */
    public void handleCommand(String command) {
        if (command == null)
            return;

        dispatcher.dispatch();
    }
}
