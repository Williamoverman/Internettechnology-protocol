package protocols.commands;

import protocols.CommandHandler;
import senders.CommandSender;

public record ByeCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.quit();
        return true;
    }
}
