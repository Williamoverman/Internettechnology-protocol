package client.protocols.commands;

import client.protocols.CommandHandler;
import client.senders.CommandSender;

public record LoginCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.login(command);
        return true;
    }
}
