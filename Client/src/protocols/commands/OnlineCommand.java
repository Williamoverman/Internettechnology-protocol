package protocols.commands;

import protocols.CommandHandler;
import senders.CommandSender;

public record OnlineCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.online();
        return true;
    }
}