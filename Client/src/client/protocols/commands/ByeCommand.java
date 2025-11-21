package client.protocols.commands;

import client.protocols.CommandHandler;
import client.senders.CommandSender;

public record ByeCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.quit();
        return true;
    }
}
