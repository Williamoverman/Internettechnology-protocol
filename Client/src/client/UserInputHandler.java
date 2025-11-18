package client;

import java.util.Scanner;

public class UserInputHandler {
    private final Scanner scanner;
    private final CommandSender commandSender;

    public UserInputHandler(CommandSender commandSender) {
        this.scanner = new Scanner(System.in);
        this.commandSender = commandSender;
    }

    public void start() {
        printHelp();

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                continue;

            if (input.equalsIgnoreCase("bye")) {
                commandSender.quit();
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                printHelp();
                continue;
            }

            if (input.toLowerCase().startsWith("login")) {
                String[] parts = input.split(" ", 2);
                if (parts.length < 2) {
                    System.out.println("Usage: login <username>");
                    continue;
                }
                commandSender.login(parts[1]);
                continue;
            }

            if (input.toLowerCase().startsWith("broadcast")) {
                String[] parts = input.split(" ", 2);
                if (parts.length < 2) {
                    System.out.println("Usage: broadcast <message>");
                    continue;
                }
                commandSender.broadcast(parts[1]);
                continue;
            }

            commandSender.sendCommand(input);
        }
    }

    public void close() {
        scanner.close();
    }

    private void printHelp() {
        System.out.println("\n==== Chat Client Commands ====");
        System.out.println("login <username> - Login with username");
        System.out.println("broadcast <message> - Broadcast a message");
        System.out.println("help - Show this help");
        System.out.println("bye - Exit server and client");
        System.out.println("============================\n");
    }
}
