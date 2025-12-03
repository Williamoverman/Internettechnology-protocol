package protocol.commands;

import protocol.ICommandHandler;
import sender.MessageSender;

public record OnlineCommand(MessageSender sender) implements ICommandHandler {
    @Override
    public void process(String jsonBody) {

    }
}
