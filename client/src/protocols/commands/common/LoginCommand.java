package protocols.commands.common;

import protocols.CommandHandler;
import senders.CommandSender;

public record LoginCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.login(command);
        return true;
    }
}
