package client;

import java.util.Scanner;

public class UserInputHandler {
    private final Scanner scanner;
    private final CommandSender commandSender;

    public UserInputHandler(Scanner scanner, CommandSender commandSender) {
        this.scanner = scanner;
        this.commandSender = commandSender;
    }

    public void start() {
        printHelp();

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                continue;

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                commandSender.quit();
                break;
            }

            if (input.equalsIgnoreCase("help")) {
                printHelp();
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
        System.out.println("LOGON {\"username\":\"your-username\"} - Login with username");
        System.out.println("help - Show this help");
        System.out.println("exit - Exit client");
        System.out.println("============================\n");
    }
}
