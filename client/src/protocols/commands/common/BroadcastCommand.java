package protocols.commands.common;

import protocols.CommandHandler;
import senders.CommandSender;

public record BroadcastCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.broadcast(command);
        return true;
    }
}
