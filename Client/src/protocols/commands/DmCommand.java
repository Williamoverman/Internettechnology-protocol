package protocols.commands;

import protocols.CommandHandler;
import senders.CommandSender;

public record DmCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        String[] splitted = command.split(" ", 2);
        String username = splitted[0];
        String message = splitted[1];

        String json = String.format(
                "{\"username\":\"%s\",\"message\":\"%s\"}",
                username,
                message
        );

        sender.dm(json);
        return true;
    }
}