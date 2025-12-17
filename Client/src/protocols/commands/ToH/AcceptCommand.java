package protocols.commands.ToH;

import protocols.CommandHandler;
import senders.CommandSender;

public record AcceptCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.accept();
        return true;
    }
}
