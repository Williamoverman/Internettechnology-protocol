package protocols.commands.common;

import protocols.CommandHandler;

public class HelpCommand implements CommandHandler {
    @Override
    public boolean execute(String command) {
        System.out.println("\n==== Chat Client Commands ====");
        System.out.println("login <username> - Login with username");
        System.out.println("broadcast <message> - Broadcast a message");
        System.out.println("online - Get a list of online users");
        System.out.println("dm <username> <message> - DM to a user");
        System.out.println("help - Show this help");
        System.out.println("bye - Exit server and client");
        System.out.println("==== Tails Or Heads game commands ====");
        System.out.println("invite <opponent> - invite a user to a game of ToH");
        System.out.println("accept - accept the invitation");
        System.out.println("decline - decline the invitation");
        System.out.println("choice <choice> - make a choice");
        System.out.println("==== File transfer commands ====");
        System.out.println("fileoffer <user> <filepath> - offer a user an file");
        System.out.println("fileaccept - accept the file");
        System.out.println("filedecline - decline the file");
        System.out.println("======================================");
        return true;
    }
}
