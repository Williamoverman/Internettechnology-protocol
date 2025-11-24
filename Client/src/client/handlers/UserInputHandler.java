package client.handlers;

import client.dispatchers.InputDispatcher;
import client.senders.CommandSender;

import java.util.Scanner;

public class UserInputHandler {
    private final Scanner scanner;
    private final InputDispatcher inputDispatcher;

    public UserInputHandler(CommandSender commandSender) {
        this.scanner = new Scanner(System.in);
        this.inputDispatcher = new InputDispatcher(commandSender);
    }

    public void start() {
        inputDispatcher.dispatch("help", "");

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine().trim();

            if (input.isEmpty())
                continue;

            String[] parts = input.split(" ", 2);
            String header = parts[0].toLowerCase();
            String command = parts.length == 2 ? parts[1] : "";

            // dispatch command to correct handler
            boolean commandStatus = inputDispatcher.dispatch(header, command);

            if (commandStatus && header.equals("bye"))
                break;

            if (!commandStatus)
                System.out.println("Unknown command");
        }
    }

    public void close() {
        scanner.close();
    }
}
