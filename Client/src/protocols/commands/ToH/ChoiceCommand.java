package protocols.commands.ToH;

import protocols.CommandHandler;
import senders.CommandSender;

public record ChoiceCommand(CommandSender sender) implements CommandHandler {
    @Override
    public boolean execute(String command) {
        sender.choice(command);
        return true;
    }
}
