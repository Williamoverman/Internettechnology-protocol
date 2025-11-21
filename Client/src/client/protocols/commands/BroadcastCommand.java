package client.protocols.commands;

import client.protocols.CommandHandler;
import client.senders.CommandSender;

public record BroadcastCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.broadcast(command);
        return true;
    }
}
