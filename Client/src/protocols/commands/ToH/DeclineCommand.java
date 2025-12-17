package protocols.commands.ToH;

import protocols.CommandHandler;
import senders.CommandSender;

public record DeclineCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.decline();
        return true;
    }
}