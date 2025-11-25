package protocols.commands;

import protocols.CommandHandler;

public class HelpCommand implements CommandHandler {
    @Override
    public boolean execute(String command) {
        System.out.println("\n==== Chat Client Commands ====");
        System.out.println("login <username> - Login with username");
        System.out.println("broadcast <message> - Broadcast a message");
        System.out.println("help - Show this help");
        System.out.println("bye - Exit server and client");
        System.out.println("===================================\n");
        return true;
    }
}
