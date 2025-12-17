package protocols.commands.ToH;

import protocols.CommandHandler;
import senders.CommandSender;

public record InviteCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.invite(command);
        return true;
    }
}
